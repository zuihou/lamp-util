package com.github.zuihou.security.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.zuihou.base.R;
import com.github.zuihou.security.exception.Auth2Exception;
import lombok.SneakyThrows;

/**
 * OAuth2 异常格式化
 * <p>
 *
 * @author zuihou
 * @date 2020年03月25日21:49:26
 */
public class Auth2ExceptionSerializer extends StdSerializer<Auth2Exception> {
    public Auth2ExceptionSerializer() {
        super(Auth2Exception.class);
    }

    @Override
    @SneakyThrows
    public void serialize(Auth2Exception value, JsonGenerator gen, SerializerProvider provider) {
        gen.writeStartObject();
        gen.writeObjectField("code", R.FAIL_CODE);
        gen.writeStringField("msg", value.getMessage());
        gen.writeStringField("data", value.getErrorCode());
        gen.writeEndObject();
    }
}
