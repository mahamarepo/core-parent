package com.mahama.parent.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class EhCacheUtil {
    public interface EhCacheTask<T> {
        T execute();
    }

    private static CacheManager cacheManager;

    private static CacheManager getManager() {
        if (cacheManager == null) {
            cacheManager = SpringBeanUtil.getBean(CacheManager.class);
        }
        return cacheManager;
    }

    /**
     * 获取缓存
     *
     * @param cacheName 缓存名
     * @param key       key
     */
    public static <T> T getCache(String cacheName, String key) {
        try {
            final Cache cache = getManager().getCache(cacheName);
            Element getGreeting = cache.get(key);
            return (T) getGreeting.getObjectValue();
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T getCache(String cacheName, String key, EhCacheTask<T> cacheTask) {
        T result = getCache(cacheName, key);
        if (result == null) {
            result = cacheTask.execute();
            setCache(cacheName, key, result);
        }
        return result;
    }

    public static <T> T getCache(String group, String cacheName, String key, EhCacheTask<T> cacheTask) {
        key = group + "_" + key;
        return getCache(cacheName, key, cacheTask);
    }

    /**
     * 设置缓存
     *
     * @param cacheName 缓存名
     * @param key       key
     * @param value     值
     */
    public static <T> void setCache(String cacheName, String key, T value) {
        final Cache cache = getManager().getCache(cacheName);
        Element putGreeting = new Element(key, value);
        cache.put(putGreeting);
    }

    /**
     * 移除缓存
     *
     * @param cacheName 缓存名
     * @param key       key
     */
    public static boolean removeCache(String cacheName, String key) {
        final Cache cache = getManager().getCache(cacheName);
        return cache.remove(key);
    }
}
