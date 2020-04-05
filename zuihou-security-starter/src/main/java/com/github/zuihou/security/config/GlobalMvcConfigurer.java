package com.github.zuihou.security.config;

import com.github.zuihou.security.interceptor.ContextHandlerInterceptor;
import com.github.zuihou.security.properties.ContextProperties;
import lombok.AllArgsConstructor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 公共配置类, 一些公共工具配置
 *
 * @author zuihou
 * @date 2018/8/25
 */
@AllArgsConstructor
public class GlobalMvcConfigurer implements WebMvcConfigurer {

    private ContextProperties contextProperties;

    /**
     * 注册 拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ContextHandlerInterceptor())
                .addPathPatterns(contextProperties.getPathPatterns())
                .order(contextProperties.getOrder())
                .excludePathPatterns(contextProperties.getExcludePatterns());
    }
}
