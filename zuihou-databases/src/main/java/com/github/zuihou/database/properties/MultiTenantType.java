package com.github.zuihou.database.properties;

import lombok.Getter;

/**
 * 多租户类型
 *
 * @author zuihou
 * @date 2018/11/20
 */
@Getter
public enum MultiTenantType {
    NONE("非租户模式"),
    /**
     * 字段模式
     * 在sql中拼接 tenant_code 字段
     */
    COLUMN("字段模式"),
    /**
     * 独立schema模式
     * 在sql中拼接 数据库 schema
     */
    SCHEMA("独立schema模式"),
    /**
     * 独立数据源模式
     * 研究中...
     */
    DATASOURCE("独立数据源模式"),
    ;
    String describe;


    MultiTenantType(String describe) {
        this.describe = describe;
    }

    public boolean eq(String val) {
        return this.name().equalsIgnoreCase(val);
    }

    public boolean eq(MultiTenantType val) {
        if (val == null) {
            return false;
        }
        return eq(val.name());
    }
}
