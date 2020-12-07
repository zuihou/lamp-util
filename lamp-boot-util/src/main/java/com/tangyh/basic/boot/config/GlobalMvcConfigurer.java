package com.tangyh.basic.boot.config;

import com.tangyh.basic.boot.interceptor.HeaderThreadLocalInterceptor;
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HeaderThreadLocalInterceptor())
                .addPathPatterns("/**")
                .order(-20);
    }
}
