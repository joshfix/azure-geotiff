package com.joshfix.geotools.azure.cache;

import com.microsoft.azure.storage.blob.CloudBlobClient;

import java.io.Serializable;

/**
 * @author joshfix
 * Created on 1/18/18
 */
public class CacheEntryKey implements Serializable {

    private String containerName;
    private String filename;
    private int block;
    private int blockSize;

    private transient CloudBlobClient client;

    public CacheEntryKey(String containerName, String filename, int block, int blockSize) {
        this.setContainerName(containerName);
        this.filename = filename;
        this.block = block;
        this.blockSize = blockSize;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public CloudBlobClient getClient() {
        return client;
    }

    public void setClient(CloudBlobClient client) {
        this.client = client;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }


    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CacheEntryKey that = (CacheEntryKey) o;

        if (block != that.block)
            return false;
        if (blockSize != that.blockSize)
            return false;
        if (!containerName.equals(that.containerName))
            return false;
        return filename.equals(that.filename);

    }

    @Override public int hashCode() {
        int result = containerName.hashCode();
        result = 31 * result + filename.hashCode();
        result = 31 * result + block;
        result = 31 * result + blockSize;
        return result;
    }
}
