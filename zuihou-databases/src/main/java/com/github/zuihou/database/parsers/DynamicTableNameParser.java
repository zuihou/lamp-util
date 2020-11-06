//package com.github.zuihou.database.parsers;
//
//import cn.hutool.core.util.StrUtil;
//import com.baomidou.mybatisplus.core.parser.ISqlParser;
//import com.baomidou.mybatisplus.core.parser.SqlInfo;
//import com.github.zuihou.context.BaseContextHandler;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.experimental.Accessors;
//import org.apache.ibatis.reflection.MetaObject;
//
//
///**
// * 动态表名解析
// *
// * @author zuihou
// * @date 2019/08/20
// */
//@Data
//@Accessors(chain = true)
//@NoArgsConstructor
//public class DynamicTableNameParser implements ISqlParser {
//
//    private String tenantDatabasePrefix;
//
//    public DynamicTableNameParser(String tenantDatabasePrefix) {
//        this.tenantDatabasePrefix = tenantDatabasePrefix;
//    }
//
//    @Override
//    public SqlInfo parser(MetaObject metaObject, String sql) {
//        if (allowProcess(metaObject)) {
//            // 本项目所有服务连接的默认数据库都是zuihou_defaults， 不执行以下代码，将在默认库中执行sql
//
//            // 想要 执行sql时， 不切换到 zuihou_base_{TENANT} 库, 请直接返回null
//            String tenantCode = BaseContextHandler.getTenant();
//            if (StrUtil.isEmpty(tenantCode)) {
//                return null;
//            }
//
//            String schemaName = StrUtil.format("{}_{}", tenantDatabasePrefix, tenantCode);
//            // 想要 执行sql时， 切换到 切换到自己指定的库， 直接修改 setSchemaName
//            String parsedSql = ReplaceSql.replaceSql(schemaName, sql);
//            return SqlInfo.newInstance().setSql(parsedSql);
//        }
//        return null;
//    }
//
//
//    /**
//     * 判断是否允许执行
//     * <p>例如：逻辑删除只解析 delete , update 操作</p>
//     *
//     * @param metaObject 元对象
//     * @return true
//     */
//    private boolean allowProcess(MetaObject metaObject) {
//        return true;
//    }
//}
