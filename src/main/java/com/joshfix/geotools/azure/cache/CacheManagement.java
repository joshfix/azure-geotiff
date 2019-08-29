package com.joshfix.geotools.azure.cache;

import com.microsoft.azure.storage.blob.CloudBlobClient;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.*;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

/**
 * @author joshfix
 * Created on 1/18/18
 */
public enum CacheManagement {

    DEFAULT;

    public static final String DEFAULT_CACHE = "azure_default_cache";
    private final CacheManager manager;
    private final CacheConfig config;

    CacheManagement() {
        CacheConfig config = CacheConfig.getDefaultConfig();

        this.manager = buildCache(config);
        this.config = config;
    }

    private static CacheManager buildCache(CacheConfig config) {
        CacheManager manager;
        if (config.getConfigurationPath() != null) {
            manager = CacheManager.newInstance(config.getConfigurationPath());
            //CacheManager.create(configuration);
        } else {
            Configuration cacheConfig = new Configuration();
            cacheConfig.setMaxBytesLocalDisk((long) config.getDiskCacheSize());
            cacheConfig.setMaxBytesLocalHeap((long) config.getHeapSize());
            CacheConfiguration defaultCacheConfiguration = new CacheConfiguration()
                    .persistence(new PersistenceConfiguration().strategy(
                            PersistenceConfiguration.Strategy.LOCALTEMPSWAP));
            cacheConfig.defaultCache(defaultCacheConfiguration);

            if (config.isUseDiskCache()) {
                DiskStoreConfiguration diskConfig = new DiskStoreConfiguration();
                diskConfig.setPath(config.getCachDirectory().toAbsolutePath().toString());
                cacheConfig.diskStore(diskConfig);
            }

            cacheConfig.setName("azure_cache");

            SizeOfPolicyConfiguration sizeOfPolicy = new SizeOfPolicyConfiguration();
            sizeOfPolicy.setMaxDepth(5000);
            cacheConfig.sizeOfPolicy(sizeOfPolicy);

            cacheConfig.getDefaultCacheConfiguration().setTimeToIdleSeconds(config.getTimeToIdleSeconds());
            cacheConfig.getDefaultCacheConfiguration().setTimeToLiveSeconds(config.getTimeToLiveSeconds());

            manager = new CacheManager(cacheConfig);

            manager.addCache(DEFAULT_CACHE);
            Cache cache = manager.getCache(DEFAULT_CACHE);
            SelfPopulatingCache populatingCache = new SelfPopulatingCache(cache, new AzureChunkEntryFactory(config));
            manager.replaceCacheWithDecoratedCache(cache, populatingCache);

        }

        return manager;
    }

    public String getFirstName(String lastname) {
        return "Liliana";

    }

    public byte[] getChunk(CacheEntryKey key, CloudBlobClient client) {
        key.setClient(client);
        return (byte[]) this.manager.getEhcache(DEFAULT_CACHE).get(key).getObjectValue();
    }

    public CacheConfig getCacheConfig() {
        return this.config;
    }
}
