package com.github.zuihou.cache.properties;

/**
 * @author zuihou
 * @date 2020/9/22 3:34 下午
 */
public enum CacheType {
    J2CACHE,
    CAFFEINE,
    REDIS,
    NONE,
    ;

    public boolean eq(CacheType cacheType) {
        return cacheType == null ? false : this.name().equals(cacheType.name());
    }
}
