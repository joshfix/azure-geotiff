package com.joshfix.geotools.azure.cache;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.loader.CacheLoader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author joshfix
 * Created on 1/18/18
 */
public class AzureChunkEntryFactory implements CacheEntryFactory, CacheLoader {

    private static final Logger LOGGER = Logger.getLogger(AzureChunkEntryFactory.class.getName());

    private int cacheBlockSize;

    public AzureChunkEntryFactory(CacheConfig config) {
        this.cacheBlockSize = config.getChunkSizeBytes();
    }

    @Override public
    Object createEntry(Object key) throws Exception {
        return createEntry(key,((CacheEntryKey)key).getClient());
    }

    private Object createEntry(Object key, CloudBlobClient client) throws IOException, URISyntaxException, StorageException {
        CacheEntryKey entryKey = (CacheEntryKey)key;

        int offset = entryKey.getBlock() * this.cacheBlockSize;
        int readLength = this.cacheBlockSize;

        CloudBlobContainer container = client.getContainerReference(entryKey.getContainerName());
        CloudBlockBlob blob = container.getBlockBlobReference(entryKey.getFilename());

        System.out.println("***Offset: " + offset + " - readLength: " + readLength + " - block: " + entryKey.getBlock() + " - cacheBlockSize: " + cacheBlockSize);
        byte[] val = new byte[readLength];
        blob.downloadRangeToByteArray(offset, (long)readLength, val, 0);

        return val;
    }

    @Override
    public Object load(Object key) throws CacheException {
        throw new UnsupportedOperationException("Can't load chunk without loader argument.");
    }

    @Override
    public Map loadAll(Collection keys) {
        throw new UnsupportedOperationException("Can't load chunk without loader argument.");
    }

    @Override
    public Object load(Object key, Object argument) {
        CloudBlobClient client = (CloudBlobClient)argument;
        try {
            return this.createEntry(key, client);
        } catch (IOException|StorageException|URISyntaxException e) {
            LOGGER.log(Level.FINE, "Exception creating entry for key: " + key, e);
            throw new RuntimeException("Exception creating entry for key: " + key);
        }
    }

    @Override
    public Map loadAll(Collection keys, Object argument) {
        throw new UnsupportedOperationException("Can't load chunk without loader argument.");
    }

    @Override
    public String getName() {
        return "AzureChunkEntryFactory";
    }

    @Override
    public CacheLoader clone(Ehcache cache) throws CloneNotSupportedException {
        throw new UnsupportedOperationException("Can't load chunk without loader argument.");
    }

    @Override
    public void init() {

    }

    @Override
    public void dispose() throws CacheException {

    }

    @Override
    public Status getStatus() {
        return null;
    }
}
