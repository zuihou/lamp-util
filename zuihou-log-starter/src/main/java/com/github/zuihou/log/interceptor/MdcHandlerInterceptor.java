//package com.github.zuihou.log.interceptor;
//
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.core.util.URLUtil;
//import com.github.zuihou.context.BaseContextConstants;
//import com.github.zuihou.context.BaseContextHandler;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.MDC;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * 网关：
// * 获取 token 并解析出用户身份，然后将所有的用户、应用信息封装到请求头
// * <p>
// * 拦截器：
// * 解析请求头数据， 将用户信息、应用信息封装到 MDC ，实现打印日志时，能打印租户ID和用户ID
// * <p>
// * 该拦截器要优先于系统中其他的业务拦截器
// * <p>
// *
// * @author zuihou
// * @date 2019-06-20 22:22
// */
//@Slf4j
//public class MdcHandlerInterceptor extends HandlerInterceptorAdapter {
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        if (!(handler instanceof HandlerMethod)) {
//            return super.preHandle(request, response, handler);
//        }
//
//        if (!BaseContextHandler.getBoot()) {
//            String traceId = request.getHeader(BaseContextConstants.TRACE_ID_HEADER);
//            MDC.put(BaseContextConstants.LOG_TRACE_ID, StrUtil.isEmpty(traceId) ? StrUtil.EMPTY : traceId);
//            MDC.put(BaseContextConstants.JWT_KEY_TENANT, getHeader(request, BaseContextConstants.JWT_KEY_TENANT));
//            MDC.put(BaseContextConstants.JWT_KEY_USER_ID, getHeader(request, BaseContextConstants.JWT_KEY_USER_ID));
//        }
//        return super.preHandle(request, response, handler);
//    }
//
//    private String getHeader(HttpServletRequest request, String name) {
//        String value = request.getHeader(name);
//        if (StrUtil.isEmpty(value)) {
//            return "";
//        }
//        return URLUtil.decode(value);
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        BaseContextHandler.remove();
//        super.afterCompletion(request, response, handler, ex);
//    }
//
//}
