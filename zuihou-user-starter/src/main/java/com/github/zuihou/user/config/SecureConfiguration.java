package com.github.zuihou.user.config;

import com.github.zuihou.user.aspect.AuthAspect;
import com.github.zuihou.user.auth.AuthFun;
import com.github.zuihou.user.feign.UserResolverService;
import com.github.zuihou.user.properties.UserProperties;
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
@EnableConfigurationProperties({UserProperties.class})
public class SecureConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "zuihou.user", name = "enabled", havingValue = "true", matchIfMissing = true)
    public AuthAspect authAspect(AuthFun authFun) {
        return new AuthAspect(authFun);
    }

    @Bean("fun")
    public AuthFun getAuthFun(UserResolverService userResolverService) {
        return new AuthFun(userResolverService);
    }

}
