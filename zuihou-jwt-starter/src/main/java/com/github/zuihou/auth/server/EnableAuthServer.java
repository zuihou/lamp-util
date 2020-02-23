package com.github.zuihou.auth.server;

import com.github.zuihou.auth.server.configuration.AuthServerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 认证服务 的服务端配置
 *
 * @author zuihou
 * @createTime 2017-12-13 15:26
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(AuthServerConfiguration.class)
@Documented
@Inherited
public @interface EnableAuthServer {
}
