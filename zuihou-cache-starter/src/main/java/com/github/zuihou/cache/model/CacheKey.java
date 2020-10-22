package com.github.zuihou.cache.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * 缓存 key 封装
 *
 * @author zuihou
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheKey {
    /**
     * redis key
     */
    @NonNull
    protected String key;
    /**
     * 超时时间 秒
     */
    @Nullable
    protected Duration expire;

    public CacheKey(String key) {
        this.key = key;
    }

}
