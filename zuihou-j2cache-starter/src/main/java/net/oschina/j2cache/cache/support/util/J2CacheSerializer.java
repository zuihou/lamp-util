package net.oschina.j2cache.cache.support.util;

import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.util.SerializationUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;


@Slf4j
public class J2CacheSerializer implements RedisSerializer<Object> {

    @Override
    public byte[] serialize(Object t) throws SerializationException {
        try {
            return SerializationUtils.serialize(t);
        } catch (IOException e) {
            log.error("序列化失败", e);
        }
        return null;
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        try {
            return SerializationUtils.deserialize(bytes);
        } catch (IOException e) {
            log.error("反序列化失败", e);
        }
        return null;
    }

}
