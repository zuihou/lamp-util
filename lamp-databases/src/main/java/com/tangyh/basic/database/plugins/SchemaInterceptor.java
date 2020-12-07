package com.tangyh.basic.database.plugins;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.tangyh.basic.context.ContextUtil;
import com.tangyh.basic.database.parsers.ReplaceSql;

/**
 * SCHEMA模式插件
 *
 * @author zuihou
 * @date 2020/8/26 上午10:00
 */
public class SchemaInterceptor extends DynamicTableNameInnerInterceptor {

    private final String tenantDatabasePrefix;

    public SchemaInterceptor(String tenantDatabasePrefix) {
        this.tenantDatabasePrefix = tenantDatabasePrefix;
    }


    @Override
    protected String changeTable(String sql) {
        // 想要 执行sql时， 不切换到 lamp_base_{TENANT} 库, 请直接返回null
        String tenantCode = ContextUtil.getTenant();
        if (StrUtil.isEmpty(tenantCode)) {
            return sql;
        }

        String schemaName = StrUtil.format("{}_{}", tenantDatabasePrefix, tenantCode);
        // 想要 执行sql时， 切换到 切换到自己指定的库， 直接修改 setSchemaName
        return ReplaceSql.replaceSql(schemaName, sql);
    }

}
