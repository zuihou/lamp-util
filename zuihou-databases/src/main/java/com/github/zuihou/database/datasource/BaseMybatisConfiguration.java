package com.github.zuihou.database.datasource;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.parsers.BlockAttackSqlParser;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import com.github.zuihou.context.BaseContextHandler;
import com.github.zuihou.database.injector.MySqlInjector;
import com.github.zuihou.database.mybatis.WriteInterceptor;
import com.github.zuihou.database.mybatis.typehandler.FullLikeTypeHandler;
import com.github.zuihou.database.mybatis.typehandler.LeftLikeTypeHandler;
import com.github.zuihou.database.mybatis.typehandler.RightLikeTypeHandler;
import com.github.zuihou.database.parsers.DynamicTableNameParser;
import com.github.zuihou.database.properties.DatabaseProperties;
import com.github.zuihou.database.properties.MultiTenantType;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Mybatis 常用重用拦截器
 * <p>
 * 拦截器执行一定是：
 * WriteInterceptor > DataScopeInterceptor > PaginationInterceptor
 *
 * @author zuihou
 * @date 2018/10/24
 */
@Slf4j
public abstract class BaseMybatisConfiguration {
    protected final DatabaseProperties databaseProperties;

    public BaseMybatisConfiguration(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    /**
     * 演示环境权限拦截器
     *
     * @return
     */
    @Bean
    @Order(15)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "zuihou.database.isNotWrite", havingValue = "true")
    public WriteInterceptor getWriteInterceptor() {
        return new WriteInterceptor();
    }


    /**
     * 分页插件，自动识别数据库类型
     * 多租户，请参考官网【插件扩展】
     */
    @Order(5)
    @Bean
    @ConditionalOnMissingBean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        List<ISqlParser> sqlParserList = new ArrayList<>();

        if (this.databaseProperties.getIsBlockAttack()) {
            // 攻击 SQL 阻断解析器 加入解析链
            sqlParserList.add(new BlockAttackSqlParser());
        }

        log.info("已为您开启{}租户模式", databaseProperties.getMultiTenantType().getDescribe());
        //动态"表名" 插件 来实现 租户schema切换 加入解析链
        if (MultiTenantType.SCHEMA.eq(this.databaseProperties.getMultiTenantType())) {
            DynamicTableNameParser dynamicTableNameParser = new DynamicTableNameParser(databaseProperties.getBizDatabase());
            sqlParserList.add(dynamicTableNameParser);
        } else if (MultiTenantType.COLUMN.eq(this.databaseProperties.getMultiTenantType())) {
            TenantSqlParser tenantSqlParser = new TenantSqlParser();
            tenantSqlParser.setTenantHandler(new TenantHandler() {
                @Override
                public Expression getTenantId(boolean where) {
                    // 该 where 条件 3.2.0 版本开始添加的，用于分区是否为在 where 条件中使用
                    // 如果是in/between之类的多个tenantId的情况，参考下方示例
                    return new StringValue(BaseContextHandler.getTenant());
                }

                @Override
                public String getTenantIdColumn() {
                    return databaseProperties.getTenantIdColumn();
                }

                @Override
                public boolean doTableFilter(String tableName) {
                    // 这里可以判断是否过滤表
                    return false;
                }
            });
            sqlParserList.add(tenantSqlParser);
        }

        paginationInterceptor.setSqlParserList(sqlParserList);
        return paginationInterceptor;
    }


    /**
     * Mybatis Plus 注入器
     *
     * @return
     */
    @Bean("myMetaObjectHandler")
    @ConditionalOnMissingBean
    public MetaObjectHandler getMyMetaObjectHandler() {
        DatabaseProperties.Id id = databaseProperties.getId();
        return new MyMetaObjectHandler(id.getWorkerId(), id.getDataCenterId());
    }

//    /**
//     * 租户信息拦截器
//     *
//     * @return
//     */
//    @Bean
//    @ConditionalOnMissingBean
//    @ConditionalOnProperty(name = "zuihou.database.isMultiTenant", havingValue = "true", matchIfMissing = true)
//    public TenantWebMvcConfigurer getTenantWebMvcConfigurer() {
//        return new TenantWebMvcConfigurer(null, null);
//    }

    /**
     * Mybatis 自定义的类型处理器： 处理XML中  #{name,typeHandler=leftLike} 类型的参数
     * 用于左模糊查询时使用
     * <p>
     * eg：
     * and name like #{name,typeHandler=leftLike}
     *
     * @return
     */
    @Bean
    public LeftLikeTypeHandler getLeftLikeTypeHandler() {
        return new LeftLikeTypeHandler();
    }

    /**
     * Mybatis 自定义的类型处理器： 处理XML中  #{name,typeHandler=rightLike} 类型的参数
     * 用于右模糊查询时使用
     * <p>
     * eg：
     * and name like #{name,typeHandler=rightLike}
     *
     * @return
     */
    @Bean
    public RightLikeTypeHandler getRightLikeTypeHandler() {
        return new RightLikeTypeHandler();
    }

    /**
     * Mybatis 自定义的类型处理器： 处理XML中  #{name,typeHandler=fullLike} 类型的参数
     * 用于全模糊查询时使用
     * <p>
     * eg：
     * and name like #{name,typeHandler=fullLike}
     *
     * @return
     */
    @Bean
    public FullLikeTypeHandler getFullLikeTypeHandler() {
        return new FullLikeTypeHandler();
    }


    @Bean
    @ConditionalOnMissingBean
    public MySqlInjector getZuihouSqlInjector() {
        return new MySqlInjector();
    }
}
