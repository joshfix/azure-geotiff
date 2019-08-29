package com.joshfix.geotools.azure;

import com.joshfix.geotools.azure.cache.CacheEntryKey;
import com.joshfix.geotools.azure.cache.CacheManagement;
import com.joshfix.geotools.azure.utility.AzureUrlParser;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

import javax.imageio.stream.ImageInputStreamImpl;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.util.logging.Logger;

/**
 * @author joshfix
 * Created on 1/18/18
 */
public class AzureImageInputStreamImpl extends ImageInputStreamImpl {

    private long length;
    private int cacheBlockSize;
    private final AzureConnector connector;
    private final String containerName;
    private final String filename;
    private final String url;

    private final CloudBlobContainer container;
    private final CloudBlobClient client;

    private final static Logger LOGGER = Logger.getLogger(AzureImageInputStreamImpl.class.getName());

    public AzureImageInputStreamImpl(String url) throws URISyntaxException, InvalidKeyException, StorageException, MalformedURLException {
        this(new URL(url));
    }

    public AzureImageInputStreamImpl(URL url) throws InvalidKeyException, StorageException, URISyntaxException {
        this.url = url.toString();

        containerName = AzureUrlParser.getContainerName(url);
        filename = AzureUrlParser.getFilename(url);

        LOGGER.fine("Accessing container: " + containerName);
        LOGGER.fine("Accessing file: " + filename);

        connector = new AzureConnector(AzureUrlParser.getAccountName(url));
        client = connector.getAzureClient();
        container = client.getContainerReference(containerName);
        length = container.getBlobReferenceFromServer(filename).getProperties().getLength();
        cacheBlockSize = CacheManagement.DEFAULT.getCacheConfig().getChunkSizeBytes();
    }

    @Override
    public int read() throws IOException {
        byte rawValue = readRawValue();
        return Byte.toUnsignedInt(rawValue);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int readRemaining = len;
        ByteBuffer readBuffer = ByteBuffer.allocate(len);
        while (readRemaining > 0 && streamPos < this.length) {
            int block = getBlockIndex();
            int offset = getCurrentOffset();
            byte[] blockBytes = this.getFromCache(block);
            //block could be longer than what we want to read... or shorter, or longer than the rest of the block
            int bytesToRead = Math.min(readRemaining, blockBytes.length - offset);
            readBuffer.put(blockBytes, offset, bytesToRead);
            readRemaining -= bytesToRead;
            streamPos += bytesToRead;
        }

        ByteBuffer inputByteBuffer = ByteBuffer.wrap(b, off, len);
        inputByteBuffer.put(readBuffer.array(), 0, len);
        return len - readRemaining;
    }

    @Override
    public String readLine() throws IOException {
        throw new IOException("readLine NOT Supported");
    }

    public String getUrl() {
        return url;
    }

    public String getFilename() {
        return filename;
    }

    private byte[] getFromCache(int block) throws IOException {
        int blockSizeForBlock = this.calculateBlockSizeForBlock(block);
        CacheEntryKey keyForBlock = new CacheEntryKey(containerName, filename, block, blockSizeForBlock);
        return CacheManagement.DEFAULT.getChunk(keyForBlock, client);
    }

    private int calculateBlockSizeForBlock(int block) {
        long offsetInFile = this.cacheBlockSize * block;
        long remainingInFile = this.length - offsetInFile;
        return (int) Math.min(this.cacheBlockSize, remainingInFile);
    }

    private int getBlockIndex() {
        return (int) Math.floor((double) streamPos / this.cacheBlockSize);
    }

    private byte readRawValue() {
        // determine block & offset
        int block = getBlockIndex();
        int offset = getCurrentOffset();
        byte rawValue = readFromCache(block, offset);
        this.streamPos++;
        return rawValue;
    }

    private int getCurrentOffset() {
        return (int) (streamPos % this.cacheBlockSize);
    }

    private byte readFromCache(int block, int offset) {
        try {
            byte[] byteBlock = this.getFromCache(block);
            return byteBlock[offset];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
