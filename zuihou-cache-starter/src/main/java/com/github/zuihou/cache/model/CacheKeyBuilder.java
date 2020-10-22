package com.github.zuihou.cache.model;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.github.zuihou.context.BaseContextHandler;
import com.github.zuihou.utils.StrPool;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.ArrayList;

import static com.github.zuihou.utils.StrPool.COLON;


/**
 * cache key
 *
 * @author zuihou
 */
@FunctionalInterface
public interface CacheKeyBuilder {

    /**
     * 租户编码
     * <p>
     * 非租户模式设置成空字符串
     *
     * @return 租户编码
     */
    @NonNull
    default String getTenant() {
        return BaseContextHandler.getTenant();
    }

    /**
     * key 前缀
     *
     * @return key 前缀
     */
    @NonNull
    String getPrefix();

    /**
     * 超时时间
     *
     * @return 超时时间
     */
    @Nullable
    default Duration getExpire() {
        return null;
    }

    /**
     * 构建通用KV模式 的 cache key
     * 兼容j2cache redis caffeine
     *
     * @param suffix 参数
     * @return cache key
     */
    default CacheKey key(Object... suffix) {
        String field = suffix.length > 0 ? Convert.toStr(suffix[0], StrPool.EMPTY) : StrPool.EMPTY;
        return hashKey(field, suffix);
    }

    /**
     * 构建 redis 类型的 hash cache key
     *
     * @param field  field
     * @param suffix 参数
     * @return cache key
     */
    default CacheHashKey hashKey(@NonNull Object field, Object... suffix) {
        ArrayList<String> regionList = new ArrayList<>();
        String tenant = this.getTenant();
        if (StrUtil.isNotEmpty(tenant)) {
            regionList.add(tenant);
        }
        String prefix = this.getPrefix();
        if (StrUtil.isNotEmpty(prefix)) {
            regionList.add(prefix);
        }

        String region = CollUtil.join(regionList, COLON);
        String key = region;
        if (suffix.length > 0) {
            key = StrUtil.join(COLON, region, suffix);
        }

        Assert.hasText(key, "key 不能为空");
        Assert.notNull(field, "field 不能为空");
        return new CacheHashKey(key, field, getExpire());
    }

}
