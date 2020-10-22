package com.github.zuihou.cache.repository.impl;

import com.github.zuihou.cache.model.CacheHashKey;
import com.github.zuihou.cache.model.CacheKey;
import com.github.zuihou.cache.redis.RedisOps;
import com.github.zuihou.cache.repository.CacheOps;
import com.github.zuihou.cache.repository.CachePlusOps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Redis Repository
 * redis 基本操作 可扩展,基本够用了
 *
 * @author zuihou
 * @date 2019-08-06 10:42
 */
@Slf4j
public class RedisOpsImpl implements CacheOps, CachePlusOps {

    /**
     * Spring Redis Template
     */
    private RedisOps redisOps;

    public RedisOpsImpl(RedisOps redisOps) {
        this.redisOps = redisOps;
    }

    /**
     * 获取 RedisTemplate对象
     */
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisOps.getRedisTemplate();
    }

    @Override
    public Long del(CacheKey... keys) {
        return redisOps.del(keys);
    }

    @Override
    public Boolean exists(CacheKey key) {
        return redisOps.exists(key.getKey());
    }

    @Override
    public void set(CacheKey key, Object value, boolean... cacheNullValues) {
        redisOps.set(key, value, cacheNullValues);
    }

    @Override
    public <T> T get(CacheKey key, boolean... cacheNullValues) {
        return redisOps.get(key, cacheNullValues);
    }

    @Override
    public <T> List<T> find(Collection<CacheKey> keys) {
        return redisOps.mGetByCacheKey(keys);
    }

    @Override
    public <T> T get(CacheKey key, Function<CacheKey, ? extends T> loader, boolean... cacheNullValues) {
        return redisOps.get(key, loader, cacheNullValues);
    }

    /**
     * 清空redis存储的数据
     *
     * @return the string
     */
    @Override
    public void flushDb() {
        redisOps.getRedisTemplate().execute((RedisCallback<String>) connection -> {
            connection.flushDb();
            return "ok";
        });
    }

    @Override
    public Long incr(CacheKey key) {
        return redisOps.incr(key.getKey());
    }

    @Override
    public Long incrBy(CacheKey key, long increment) {
        return redisOps.incrBy(key.getKey(), increment);
    }

    @Override
    public Double incrByFloat(CacheKey key, double increment) {
        return redisOps.incrByFloat(key.getKey(), increment);
    }

    @Override
    public Long decr(CacheKey key) {
        return redisOps.decr(key.getKey());
    }

    @Override
    public Long decrBy(CacheKey key, long decrement) {
        return redisOps.decrBy(key.getKey(), decrement);
    }

    @Override
    public Set<String> keys(String pattern) {
        return redisOps.keys(pattern);
    }

    @Override
    public Boolean expire(CacheKey key) {
        return redisOps.expire(key.getKey(), key.getExpire());
    }

    @Override
    public Boolean persist(CacheKey key) {
        return redisOps.persist(key.getKey());
    }

    @Override
    public String type(CacheKey key) {
        return redisOps.type(key.getKey());
    }

    @Override
    public Long ttl(CacheKey key) {
        return redisOps.ttl(key.getKey());
    }

    @Override
    public Long pTtl(CacheKey key) {
        return redisOps.pTtl(key.getKey());
    }

    @Override
    public void hSet(CacheHashKey key, Object value, boolean... cacheNullValues) {
        redisOps.hSet(key, value, cacheNullValues);
    }

    @Override
    public <T> T hGet(CacheHashKey key, boolean... cacheNullValues) {
        return redisOps.hGet(key, cacheNullValues);
    }

    @Override
    public <T> T hGet(CacheHashKey key, Function<CacheHashKey, T> loader, boolean... cacheNullValues) {
        return redisOps.hGet(key, loader, cacheNullValues);
    }

    @Override
    public Boolean hExists(CacheHashKey cacheHashKey) {
        return redisOps.hExists(cacheHashKey);
    }

    @Override
    public Long hDel(String key, Object... fields) {
        return redisOps.hDel(key, fields);
    }

    @Override
    public Long hDel(CacheHashKey cacheHashKey) {
        return redisOps.hDel(cacheHashKey.getKey(), cacheHashKey.getField());
    }

    @Override
    public Long hLen(CacheHashKey key) {
        return redisOps.hLen(key.getKey());
    }

    @Override
    public Long hIncrBy(CacheHashKey key, long increment) {
        return redisOps.hIncrBy(key.getKey(), key.getField(), increment);
    }

    @Override
    public Double hIncrBy(CacheHashKey key, double increment) {
        return redisOps.hIncrByFloat(key.getKey(), key.getField(), increment);
    }

    @Override
    public Set<Object> hKeys(CacheHashKey key) {
        return redisOps.hKeys(key.getKey());
    }

    @Override
    public List<Object> hVals(CacheHashKey key) {
        return redisOps.hVals(key.getKey());
    }

    @Override
    public Map<Object, Object> hGetAll(CacheHashKey key) {
        return redisOps.hGetAll(key.getKey());
    }
}
