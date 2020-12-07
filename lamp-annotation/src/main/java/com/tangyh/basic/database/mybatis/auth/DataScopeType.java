package com.tangyh.basic.database.mybatis.auth;

import com.tangyh.basic.base.BaseEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * <p>
 * 实体注释中生成的类型枚举
 * 角色
 * </p>
 *
 * @author zuihou
 * @date 2019-07-21
 */
@Getter
@AllArgsConstructor
@ApiModel(value = "DataScopeType", description = "数据权限类型-枚举")
public enum DataScopeType implements BaseEnum {

    /**
     * ALL=5全部
     */
    ALL(5, "全部"),
    /**
     * THIS_LEVEL=4本级
     */
    THIS_LEVEL(4, "本级"),
    /**
     * THIS_LEVEL_CHILDREN=3本级以及子级
     */
    THIS_LEVEL_CHILDREN(3, "本级以及子级"),
    /**
     * CUSTOMIZE=2自定义
     */
    CUSTOMIZE(2, "自定义"),
    /**
     * SELF=1个人
     */
    SELF(1, "个人"),
    ;

    @ApiModelProperty(value = "描述")
    private final int val;

    private final String desc;


    public static DataScopeType match(String val, DataScopeType def) {
        return Stream.of(values()).parallel().filter((item) -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }

    public static DataScopeType match(Integer val, DataScopeType def) {
        return Stream.of(values()).parallel().filter((item) -> val != null && item.getVal() == val).findAny().orElse(def);
    }

    public static DataScopeType get(String val) {
        return match(val, null);
    }

    public static DataScopeType get(Integer val) {
        return match(val, null);
    }

    public boolean eq(final DataScopeType val) {
        return val != null && eq(val.name());
    }

    @Override
    @ApiModelProperty(value = "编码", allowableValues = "ALL,THIS_LEVEL,THIS_LEVEL_CHILDREN,CUSTOMIZE,SELF", example = "ALL")
    public String getCode() {
        return this.name();
    }

}
