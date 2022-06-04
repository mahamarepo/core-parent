package com.mahama.parent.service;

import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisHashService<HK, HV> {
    private final String hashKey;
    private final BoundHashOperations<String, HK, HV> hashOperations;
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisHashService(RedisTemplate<String, Object> redisTemplate, String hashKey, long timeout, TimeUnit unit) {
        this.redisTemplate = redisTemplate;
        this.hashKey = hashKey;
        this.redisTemplate.boundHashOps(hashKey).expire(timeout, unit);
        hashOperations = this.redisTemplate.boundHashOps(hashKey);
    }

    public void put(HK key, HV value) {
        hashOperations.put(key, value);
    }

    public void putAll(HashMap<HK, HV> hashMap) {
        hashOperations.putAll(hashMap);
    }

    public Set<HK> keys() {
        return hashOperations.keys();
    }

    public HV get(HK key) {
        return hashOperations.get(key);
    }

    public Map<HK, HV> entries() {
        return hashOperations.entries();
    }

    @SafeVarargs
    public final void delete(HK... keys) {
        hashOperations.delete((Object) keys);
    }

    public void deleteHashKey() {
        redisTemplate.delete(this.hashKey);
    }
}
