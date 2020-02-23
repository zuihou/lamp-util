package com.github.zuihou.auth.client;

import com.github.zuihou.auth.client.configuration.AuthClientConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用授权client
 *
 * @author zuihou
 * @createTime 2017-12-13 15:26
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({AuthClientConfiguration.class})
@Documented
@Inherited
public @interface EnableAuthClient {
}
