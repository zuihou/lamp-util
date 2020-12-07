package com.tangyh.basic.cache.model;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.tangyh.basic.context.ContextUtil;
import com.tangyh.basic.utils.BizAssert;
import com.tangyh.basic.utils.StrPool;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.ArrayList;

import static com.tangyh.basic.utils.StrPool.COLON;


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
        return ContextUtil.getTenant();
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
     * 兼容 redis caffeine
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
        BizAssert.notEmpty(prefix, "缓存前缀不能为空");
        regionList.add(prefix);

        for (Object s : suffix) {
            if (ObjectUtil.isNotEmpty(s)) {
                regionList.add(String.valueOf(s));
            }
        }
        String key = CollUtil.join(regionList, COLON);

        BizAssert.notEmpty(key, "key 不能为空");
        BizAssert.notNull(field, "field 不能为空");
        return new CacheHashKey(key, field, getExpire());
    }

}
