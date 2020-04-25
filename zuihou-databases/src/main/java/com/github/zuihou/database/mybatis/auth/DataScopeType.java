package com.github.zuihou.database.mybatis.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.zuihou.base.BaseEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
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
    private int val;

    private String desc;


    public static DataScopeType match(String val, DataScopeType def) {
        for (DataScopeType enm : DataScopeType.values()) {
            if (enm.name().equalsIgnoreCase(val)) {
                return enm;
            }
        }
        return def;
    }

    public static DataScopeType match(Integer val, DataScopeType def) {
        if (val == null) {
            return def;
        }
        for (DataScopeType enm : DataScopeType.values()) {
            if (val.equals(enm.getVal())) {
                return enm;
            }
        }
        return def;
    }

    public static DataScopeType get(String val) {
        return match(val, null);
    }

    public static DataScopeType get(Integer val) {
        return match(val, null);
    }

    public boolean eq(String val) {
        return this.name().equalsIgnoreCase(val);
    }

    public boolean eq(DataScopeType val) {
        if (val == null) {
            return false;
        }
        return eq(val.name());
    }

    @Override
    @ApiModelProperty(value = "编码", allowableValues = "ALL,THIS_LEVEL,THIS_LEVEL_CHILDREN,CUSTOMIZE,SELF", example = "ALL")
    public String getCode() {
        return this.name();
    }

}
