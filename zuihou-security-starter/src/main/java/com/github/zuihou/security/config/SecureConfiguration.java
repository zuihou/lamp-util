package com.github.zuihou.security.config;

import com.github.zuihou.security.aspect.AuthAspect;
import com.github.zuihou.security.auth.AuthFun;
import com.github.zuihou.security.feign.UserResolverService;
import com.github.zuihou.security.properties.ContextProperties;
import com.github.zuihou.security.properties.UserProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

/**
 * 权限认证配置类
 *
 * @author zuihou
 * @date 2020年03月29日22:34:45
 */
@Order
@AllArgsConstructor
@EnableConfigurationProperties({UserProperties.class, ContextProperties.class})
public class SecureConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = UserProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public AuthAspect authAspect(AuthFun authFun) {
        return new AuthAspect(authFun);
    }

    @Bean("fun")
    public AuthFun getAuthFun(UserResolverService userResolverService) {
        return new AuthFun(userResolverService);
    }

    @Bean
    public GlobalMvcConfigurer getGlobalMvcConfigurer(ContextProperties contextProperties) {
        return new GlobalMvcConfigurer(contextProperties);
    }

}
