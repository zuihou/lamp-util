//package com.github.zuihou.database.servlet;
//
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * 多租户配置
// *
// * @author zuihou
// * @date 2019/10/23
// */
//public class TenantWebMvcConfigurer implements WebMvcConfigurer {
//
//    /**
//     * 注册 拦截器
//     *
//     * @param registry
//     */
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        if (getHandlerInterceptor() != null) {
//            registry.addInterceptor(getHandlerInterceptor())
//                    .addPathPatterns("/**")
//                    .order(-19);
//        }
//    }
//
//    protected HandlerInterceptor getHandlerInterceptor() {
//        return new TenantContextHandlerInterceptor();
//    }
//
//}
