package top.tangyh.basic.cache.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.NonNull;

import java.time.Duration;

import static top.tangyh.basic.utils.StrPool.COLON;


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

    public CacheHashKey(@NonNull String key, final @NonNull Object field) {
        super(key);
        this.field = field;
    }

    public CacheHashKey(@NonNull String key, final @NonNull Object field, Duration expire) {
        super(key, expire);
        this.field = field;
    }

    public CacheKey tran() {
        return new CacheKey(StrUtil.join(COLON, getKey(), getField()), getExpire());
    }
}
