//package com.github.zuihou.log.interceptor;
//
//import lombok.AllArgsConstructor;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * 公共配置类, 一些公共工具配置
// *
// * @author zuihou
// * @date 2018/8/25
// */
//@AllArgsConstructor
//public class MdcMvcConfigurer implements WebMvcConfigurer {
//
//    /**
//     * 注册 拦截器
//     *
//     * @param registry
//     */
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new MdcHandlerInterceptor())
//                .addPathPatterns("/**")
//                .order(-18);
//    }
//}
