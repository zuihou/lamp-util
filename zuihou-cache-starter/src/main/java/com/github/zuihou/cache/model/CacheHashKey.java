package com.github.zuihou.cache.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.NonNull;

import java.time.Duration;

import static com.github.zuihou.utils.StrPool.COLON;


/**
 * hash 缓存 key 封装
 *
 * @author zuihou
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CacheHashKey extends CacheKey {
    /**
     * redis hash field
     */
    @NonNull
    private Object field;

    public CacheHashKey(String key, Object field) {
        this.key = key;
        this.field = field;
    }

    public CacheHashKey(String key, Object field, Duration expire) {
        this.key = key;
        this.field = field;
        this.expire = expire;
    }

    public CacheKey tran() {
        return new CacheKey(StrUtil.join(COLON, this.key, this.field), expire);
    }
}
