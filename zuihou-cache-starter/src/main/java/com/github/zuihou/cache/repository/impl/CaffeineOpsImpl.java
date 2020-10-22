package com.github.zuihou.cache.repository.impl;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.zuihou.cache.model.CacheHashKey;
import com.github.zuihou.cache.model.CacheKey;
import com.github.zuihou.cache.repository.CacheOps;
import com.github.zuihou.cache.repository.CachePlusOps;
import com.github.zuihou.utils.StrPool;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 基于 Caffeine 实现的内存缓存， 主要用于开发、测试、演示环境
 * 生产环境慎用！
 *
 * @author zuihou
 * @date 2019/08/07
 */
public class CaffeineOpsImpl implements CacheOps, CachePlusOps {
    /**
     * 最大数量
     */
    long DEF_MAX_SIZE = 1_000;

    /**
     * 为什么不直接用 Cache<String, Object> ？
     * 因为想针对每一个key单独设置过期时间
     */
    private final Cache<String, Cache<String, Object>> cacheMap = Caffeine.newBuilder()
            .maximumSize(DEF_MAX_SIZE)
            .build();

    @Override
    public Long del(CacheKey... keys) {
        for (CacheKey key : keys) {
            cacheMap.invalidate(key.getKey());
        }
        return Long.valueOf(keys.length);
    }

    @Override
    public void set(CacheKey key, Object value, boolean... cacheNullValues) {
        if (value == null) {
            return;
        }
        Caffeine<Object, Object> builder = Caffeine.newBuilder()
                .maximumSize(DEF_MAX_SIZE);
        if (key.getExpire() != null) {
            builder.expireAfterWrite(key.getExpire());
        }
        Cache<String, Object> cache = builder.build();
        cache.put(key.getKey(), value);
        cacheMap.put(key.getKey(), cache);
    }

    @Override
    public <T> T get(CacheKey key, boolean... cacheNullValues) {
        Cache<String, Object> ifPresent = cacheMap.getIfPresent(key.getKey());
        if (ifPresent == null) {
            return null;
        }
        return (T) ifPresent.getIfPresent(key);
    }

    @Override
    public <T> List<T> find(Collection<CacheKey> keys) {
        return keys.stream().map(k -> (T) get(k, false)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public <T> T get(CacheKey key, Function<CacheKey, ? extends T> loader, boolean... cacheNullValues) {
        Cache<String, Object> cache = cacheMap.get(key.getKey(), (k) -> {
            Caffeine<Object, Object> builder = Caffeine.newBuilder()
                    .maximumSize(DEF_MAX_SIZE);
            if (key.getExpire() != null) {
                builder.expireAfterWrite(key.getExpire());
            }
            Cache<String, Object> newCache = builder.build();
            newCache.get(k, (tk) -> loader.apply(new CacheKey(tk)));
            return newCache;
        });

        return (T) cache.getIfPresent(key.getKey());
    }

    @Override
    public void flushDb() {
        cacheMap.invalidateAll();
    }

    @Override
    public Boolean exists(final CacheKey key) {
        Cache<String, Object> cache = cacheMap.getIfPresent(key.getKey());
        if (cache == null) {
            return false;
        }
        cache.cleanUp();
        return cache.estimatedSize() > 0;
    }

    @Override
    public Long incr(CacheKey key) {
        Long old = get(key, k -> 0L);
        Long newVal = old + 1;
        set(key, newVal);
        return newVal;
    }

    @Override
    public Long incrBy(CacheKey key, long increment) {
        Long old = get(key, k -> 0L);
        Long newVal = old + increment;
        set(key, newVal);
        return newVal;
    }

    @Override
    public Double incrByFloat(CacheKey key, double increment) {
        Double old = get(key, k -> 0D);
        Double newVal = old + increment;
        set(key, newVal);
        return newVal;
    }

    @Override
    public Long decr(CacheKey key) {
        Long old = get(key, k -> 0L);
        Long newVal = old - 1;
        set(key, newVal);
        return newVal;
    }

    @Override
    public Long decrBy(CacheKey key, long decrement) {
        Long old = get(key, k -> 0L);
        Long newVal = old - decrement;
        set(key, newVal);
        return newVal;
    }
    // ---- 以下接口可能有问题，仅支持在开发环境使用

    /**
     * KEYS * 匹配数据库中所有 key 。
     * KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
     * KEYS h*llo 匹配 hllo 和 heeeeello 等。
     * KEYS h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo
     *
     * @param pattern 表达式
     * @return
     */
    @Override
    public Set<String> keys(String pattern) {
        if (StrUtil.isEmpty(pattern)) {
            return Collections.emptySet();
        }
        ConcurrentMap<String, Cache<String, Object>> map = cacheMap.asMap();
        Set<String> list = Collections.emptySet();
        map.forEach((k, val) -> {
            // *
            if (StrPool.ASTERISK.equals(pattern)) {
                list.add(k);
                return;
            }
            // h?llo
            if (pattern.contains(StrPool.QUESTION_MARK)) {
                //待实现
                return;
            }
            // h*llo
            if (pattern.contains(StrPool.ASTERISK)) {
                //待实现
                return;
            }
            // h[ae]llo
            if (pattern.contains(StrPool.LEFT_SQ_BRACKET) && pattern.contains(StrPool.RIGHT_SQ_BRACKET)) {
                //待实现
                return;
            }
        });
        return list;
    }

    @Override
    public Boolean expire(CacheKey key) {
        return true;
    }

    @Override
    public Boolean persist(CacheKey key) {
        return true;
    }

    @Override
    public String type(CacheKey key) {
        return "caffeine";
    }

    @Override
    public Long ttl(CacheKey key) {
        return -1L;
    }

    @Override
    public Long pTtl(CacheKey key) {
        return -1L;
    }

    @Override
    public void hSet(CacheHashKey key, Object value, boolean... cacheNullValues) {
        this.set(key.tran(), value, cacheNullValues);
    }

    @Override
    public <T> T hGet(CacheHashKey key, boolean... cacheNullValues) {
        return get(key.tran(), cacheNullValues);
    }

    @Override
    public <T> T hGet(CacheHashKey key, Function<CacheHashKey, T> loader, boolean... cacheNullValues) {
        Function<CacheKey, T> ckLoader = k -> loader.apply(key);
        return get(key.tran(), ckLoader, cacheNullValues);
    }

    @Override
    public Boolean hExists(CacheHashKey cacheHashKey) {
        return exists(cacheHashKey.tran());
    }

    @Override
    public Long hDel(String key, Object... fields) {
        for (Object field : fields) {
            cacheMap.invalidate(StrUtil.join(StrUtil.COLON, key, field));
        }
        return Long.valueOf(fields.length);
    }

    @Override
    public Long hDel(CacheHashKey cacheHashKey) {
        cacheMap.invalidate(cacheHashKey.tran().getKey());
        return 1L;
    }

    @Override
    public Long hLen(CacheHashKey key) {
        return 1L;
    }

    @Override
    public Long hIncrBy(CacheHashKey key, long increment) {
        return incrBy(key.tran(), increment);
    }

    @Override
    public Double hIncrBy(CacheHashKey key, double increment) {
        return incrByFloat(key.tran(), increment);
    }

    @Override
    public Set<Object> hKeys(CacheHashKey key) {
        return Collections.emptySet();
    }

    @Override
    public List<Object> hVals(CacheHashKey key) {
        return Collections.emptyList();
    }

    @Override
    public Map<Object, Object> hGetAll(CacheHashKey key) {
        return Collections.emptyMap();
    }
}
