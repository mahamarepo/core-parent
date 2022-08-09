package com.mahama.parent.service;

import com.mahama.common.utils.RedisHelp;
import com.mahama.parent.repository.impl.NaturalRepository;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class EntityNaturalService<NR extends NaturalRepository<T, NID, ID>, T extends Serializable, NID, ID> extends EntityService<NR, T, ID> {
    private final String naturalIdKey = "naturalId_";

    private NaturalRepository<T, NID, ID> naturalRepository;

    public EntityNaturalService() {
    }

    public EntityNaturalService(NaturalRepository<T, NID, ID> naturalRepository) {
        this.naturalRepository = naturalRepository;
    }

    public T findBySimpleNaturalId(NID naturalId) {
        return findBySimpleNaturalId(naturalId, 1, TimeUnit.HOURS);
    }

    public T findBySimpleNaturalId(NID naturalId, long timeout, TimeUnit unit) {
        return getRedisHelp().query(naturalIdKey + naturalId,
                () -> {
                    if (naturalRepository != null) {
                        return naturalRepository.findBySimpleNaturalId(naturalId).orElse(null);
                    } else {
                        return this.getJpaRepository().findBySimpleNaturalId(naturalId).orElse(null);
                    }
                },
                timeout, unit);
    }

    public T findByNaturalId(Map<String, Object> naturalIds) {
        return getRedisHelp().query(naturalIdKey + "s_" + naturalIds.hashCode(),
                () -> getJpaRepository().findByNaturalId(naturalIds).orElse(null));
    }

    public <S extends T> S updateBySimpleNaturalId(NID naturalId, S entity) {
        RedisHelp redisHelp = getRedisHelp();
        S result = redisHelp.updateById(null, () -> getJpaRepository().save(entity));
        redisHelp.deleteCacheKey(naturalIdKey + naturalId);
        if (callback != null)
            callback.deleteOtherKey(result);
        return result;
    }

    public <S extends T> S updateBySimpleNaturalId(ID id, NID naturalId, S entity) {
        RedisHelp redisHelp = getRedisHelp();
        S result = redisHelp.updateById(id, () -> getJpaRepository().save(entity));
        redisHelp.deleteCacheKey(naturalIdKey + naturalId);
        if (callback != null)
            callback.deleteOtherKey(result);
        return result;
    }
}
