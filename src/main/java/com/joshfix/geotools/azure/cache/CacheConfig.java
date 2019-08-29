package com.joshfix.geotools.azure.cache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author joshfix
 * Created on 1/18/18
 */
public class CacheConfig {

    private static final Logger LOGGER = Logger.getLogger(CacheConfig.class.getName());

    //whether disk caching should be disabled
    public static final String AZURE_CACHING_DISABLE_DISK = "azure.caching.disableDisk";

    //whether off heap should be used. currently not supported
    public static final String AZURE_CACHING_USE_OFF_HEAP = "azure.caching.useOffHeap";

    //the "chunk" size used to cache images
    public static final String AZURE_CACHING_CHUNK_SIZE_BYTES = "azure.caching.chunkSizeBytes";

    //the disk cache size.
    public static final String AZURE_CACHING_DISK_CACHE_SIZE = "azure.caching.diskCacheSize";

    //path for the disk cache
    public static final String AZURE_CACHING_DISK_PATH = "azure.caching.diskPath";

    //alternatively an EhCache 2.x XML config can be used to override all cache config
    public static final String AZURE_CACHING_EH_CACHE_CONFIG = "azure.caching.ehCacheConfig";

    public static final int MEBIBYTE_IN_BYTES = 1048576;

    //in heap cache size in bytes
    public static final String AZURE_CACHING_HEAP_SIZE = "azure.caching.heapSize";

    private int timeToLiveSeconds = 60 * 15;
    private int timeToIdleSeconds = 60 * 5;
    private boolean useDiskCache = false;
    private boolean useOffHeapCache = false;
    private int chunkSizeBytes = 5 * MEBIBYTE_IN_BYTES;
    private int diskCacheSize = 5000 * MEBIBYTE_IN_BYTES;
    private int heapSize = 1000 * MEBIBYTE_IN_BYTES;
    private Path cachDirectory;
    private String configurationPath;

    public static CacheConfig getDefaultConfig() {
        CacheConfig config = new CacheConfig();

        if (Boolean.getBoolean(AZURE_CACHING_DISABLE_DISK)) {
            config.setUseDiskCache(false);
        }

        if (Boolean.getBoolean(AZURE_CACHING_USE_OFF_HEAP)) {
            config.setUseOffHeapCache(true);
        }

        if (System.getProperty(AZURE_CACHING_CHUNK_SIZE_BYTES) != null ) {
            try {
                Integer chunkSize = Integer.parseInt(System.getProperty(AZURE_CACHING_CHUNK_SIZE_BYTES));
                config.setChunkSizeBytes(chunkSize);
            }
            catch (NumberFormatException e) {
                LOGGER.log(Level.FINER, "Can't parse chunk size", e);
            }
        }

        if (System.getProperty(AZURE_CACHING_HEAP_SIZE) != null ) {
            try {
                Integer heapSize = Integer.parseInt(System.getProperty(AZURE_CACHING_HEAP_SIZE));
                config.setHeapSize(heapSize);
            }
            catch (NumberFormatException e) {
                LOGGER.log(Level.FINER, "Can't parse heap", e);
            }
        }

        if (System.getProperty(AZURE_CACHING_DISK_CACHE_SIZE) != null ) {
            try {
                Integer diskCacheSize = Integer.parseInt(System.getProperty(
                        AZURE_CACHING_DISK_CACHE_SIZE));
                config.setDiskCacheSize(diskCacheSize);
            }
            catch (NumberFormatException e) {
                LOGGER.log(Level.FINER, "Can't parse disk cache size", e);
            }
        }

        if (System.getProperty(AZURE_CACHING_DISK_PATH) != null) {
            try {
                String diskPath = System.getProperty(AZURE_CACHING_DISK_PATH);
                Path cachePath = Paths.get(diskPath);
                config.setCachDirectory(cachePath);
            }
            catch (InvalidPathException e) {
                LOGGER.log(Level.FINER, "Can't parse disk cache path", e);
            }
        }
        else {
            if (config.isUseDiskCache()) {
                try {
                    config.setCachDirectory(Files.createTempDirectory("azureCache"));
                } catch (IOException e) {
                    throw new RuntimeException("CAN'T CREATE TEMP CACHING DIRECTORY AND NO DIRECTORY SPECIFIED", e);
                }
            }
        }

        if (System.getProperty(AZURE_CACHING_EH_CACHE_CONFIG) != null) {
            String ehCachePath = System.getProperty(AZURE_CACHING_EH_CACHE_CONFIG);
            config.setConfigurationPath(ehCachePath);
        }

        return config;
    }

    public int getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    public int getTimeToIdleSeconds() {
        return this.timeToIdleSeconds;
    }

    public void setTimeToIdleSeconds(int timeToIdleSeconds) {
        this.timeToIdleSeconds = timeToIdleSeconds;
    }

    public boolean isUseDiskCache() {
        return useDiskCache;
    }

    public void setUseDiskCache(boolean useDiskCache) {
        this.useDiskCache = useDiskCache;
    }

    public boolean isUseOffHeapCache() {
        return useOffHeapCache;
    }

    public void setUseOffHeapCache(boolean useOffHeapCache) {
        this.useOffHeapCache = useOffHeapCache;
    }

    public int getChunkSizeBytes() {
        return chunkSizeBytes;
    }

    public void setChunkSizeBytes(int chunkSizeBytes) {
        this.chunkSizeBytes = chunkSizeBytes;
    }

    public int getDiskCacheSize() {
        return diskCacheSize;
    }

    public void setDiskCacheSize(int diskCacheSize) {
        this.diskCacheSize = diskCacheSize;
    }

    public Path getCachDirectory() {
        return cachDirectory;
    }

    public void setCachDirectory(Path cachDirectory) {
        this.cachDirectory = cachDirectory;
    }

    public String getConfigurationPath() {
        return configurationPath;
    }

    public void setConfigurationPath(String configurationPath) {
        this.configurationPath = configurationPath;
    }

    public int getHeapSize() {
        return heapSize;
    }

    public void setHeapSize(int heapSize) {
        this.heapSize = heapSize;
    }
}
