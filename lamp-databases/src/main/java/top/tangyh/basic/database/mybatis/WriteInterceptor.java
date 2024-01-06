package top.tangyh.basic.database.mybatis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.tangyh.basic.context.ContextUtil;
import top.tangyh.basic.database.properties.DatabaseProperties;
import top.tangyh.basic.exception.BizException;
import top.tangyh.basic.utils.SpringUtils;

import java.sql.Connection;
import java.util.Properties;

import static org.apache.ibatis.mapping.SqlCommandType.DELETE;
import static org.apache.ibatis.mapping.SqlCommandType.INSERT;
import static org.apache.ibatis.mapping.SqlCommandType.UPDATE;


/**
 * 演示环境写权限控制 拦截器
 * 该拦截器只用于演示环境， 开发和生产都不需要
 * <p>
 *
 * @author zuihou
 * @date 2019/2/1
 */
@SuppressWarnings("AlibabaUndefineMagicConstant")
@Slf4j
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class WriteInterceptor implements Interceptor {

    private final DatabaseProperties databaseProperties;

    public WriteInterceptor(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    @Override
    @SneakyThrows
    public Object intercept(Invocation invocation) {
        // 为什么在拦截器里使用 @RefreshScope 无效？
        if (SpringUtils.getApplicationContext() == null) {
            return invocation.proceed();
        }
        if (!SpringUtils.getApplicationContext().getEnvironment().getProperty(DatabaseProperties.PREFIX + ".isNotWrite", Boolean.class, false)) {
            return invocation.proceed();
        }

        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
//        sqlParser(metaObject);
        // 读操作 放行
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType())) {
            return invocation.proceed();
        }
        // 记录日志相关的 放行
        if (StrUtil.containsAnyIgnoreCase(mappedStatement.getId(), "uid", "resetPassErrorNum", "updateLastLoginTime")) {
            return invocation.proceed();
        }

        Long userId = ContextUtil.getUserId();
        log.info("mapper id={}, userId={}", mappedStatement.getId(), userId);

        // IP 放行
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            String ip = JakartaServletUtil.getClientIP(request);
            if (CollUtil.contains(databaseProperties.getWriteWhiteList(), ip)) {
                return invocation.proceed();
            }
        } else {
            log.info("requestAttributes is null, ignore.");
        }

        // 原来让superAdmin可以操作是方便维护演示环境数据，但总有刁民利用此账号，频繁删除演示环境数据。特意删除。
        if (DELETE.equals(mappedStatement.getSqlCommandType())) {
            boolean not = StrUtil.containsAnyIgnoreCase(mappedStatement.getId(), ".application.Def", ".system.Def",
                    ".tenant.Def", ".BaseEmployee", "BaseRole");
            boolean exclude = StrUtil.containsAnyIgnoreCase(mappedStatement.getId(), "RelMapper");
            boolean isStop = ContextUtil.isStop();
            if ((not && !exclude) || isStop) {
                throw new BizException(-1, "演示环境禁止新增、修改、删除系统级数据！请创建其他租户账号后测试全部功能");
            }
        } else if (UPDATE.equals(mappedStatement.getSqlCommandType())) {
            boolean not = StrUtil.containsAnyIgnoreCase(mappedStatement.getId(), "DefResource");
            boolean isStop = ContextUtil.isStop();
            if (not || isStop) {
                throw new BizException(-1, "演示环境禁止新增、修改、删除系统级数据！请创建其他租户账号后测试全部功能");
            }
        } else if (INSERT.equals(mappedStatement.getSqlCommandType())) {
            boolean not = StrUtil.containsAnyIgnoreCase(mappedStatement.getId(), "DefResource");
            if (not) {
                throw new BizException(-1, "演示环境禁止新增、修改、删除系统级数据！请创建其他租户账号后测试全部功能");
            }
        }

        //放行
        return invocation.proceed();
    }

    /**
     * 生成拦截对象的代理
     *
     * @param target 目标对象
     * @return 代理对象
     */
    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    /**
     * mybatis配置的属性
     *
     * @param properties mybatis配置的属性
     */
    @Override
    public void setProperties(Properties properties) {

    }

}
