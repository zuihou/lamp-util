package com.tangyh.basic.converter;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.tangyh.basic.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import static com.tangyh.basic.converter.EnumSerializer.ALL_ENUM_KEY_FIELD;

/**
 * enum反序列化工具
 * <p>
 * 字段类型是枚举类型时，可以按照以下2种格式反序列化：
 * 1. 字符串形式：字段名： "XX"
 * 2. 对象形式： 字段名： {
 * "code": "XX"
 * }
 *
 * @author zuihou
 * @date 2019-07-25 22:15
 */
@Slf4j
public class EnumDeserializer extends StdDeserializer<Enum<?>> {
    public static final EnumDeserializer INSTANCE = new EnumDeserializer();


    public EnumDeserializer() {
        super(Enum.class);
    }

    @Override
    public Enum<?> deserialize(JsonParser jp, DeserializationContext context) {
        try {
            // 读取
            JsonNode node = jp.getCodec().readTree(jp);
            // 当前字段
            String currentName = jp.currentName();
            // 当前对象
            Object currentValue = jp.getCurrentValue();
            // 在对象中找到改字段
            Class propertyType = BeanUtils.findPropertyType(currentName, currentValue.getClass());
            JsonNode code = node.get(ALL_ENUM_KEY_FIELD);
            String val = code != null ? code.asText() : node.asText();
            if (StrUtil.isBlank(val) || StrPool.NULL.equals(val)) {
                return null;
            }
            return Enum.valueOf(propertyType, val);
        } catch (Exception e) {
            log.warn("解析枚举失败", e);
            return null;
        }
    }


}
