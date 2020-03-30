package com.github.zuihou.boot.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.github.zuihou.context.BaseContextConstants;
import com.github.zuihou.context.BaseContextHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 网关：
 * 获取token，并解析，然后将所有的用户、应用信息封装到请求头
 * <p>
 * 拦截器：
 * 解析请求头数据， 将用户信息、应用信息封装到BaseContextHandler
 * 考虑请求来源是否网关（ip等）
 * <p>
 * 该拦截器要优先于系统中其他的业务拦截器
 * <p>
 *
 * @author zuihou
 * @date 2019-06-20 22:22
 */
@Slf4j
public class ContextHandlerInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            if (!(handler instanceof HandlerMethod)) {
                log.info("该url={}，不是Controller接口，无需封装请求头", request.getRequestURL());
                return super.preHandle(request, response, handler);
            }

            if (!BaseContextHandler.getBoot()) {
                BaseContextHandler.setUserId(getHeader(request, BaseContextConstants.JWT_KEY_USER_ID));
                BaseContextHandler.setAccount(getHeader(request, BaseContextConstants.JWT_KEY_ACCOUNT));
                BaseContextHandler.setName(getHeader(request, BaseContextConstants.JWT_KEY_NAME));
            }
            // cloud
            BaseContextHandler.setGrayVersion(getHeader(request, BaseContextConstants.GRAY_VERSION));
            // databases
            BaseContextHandler.setTenant(getHeader(request, BaseContextConstants.TENANT));

            // log
            String traceId = request.getHeader(BaseContextConstants.TRACE_ID_HEADER);
            MDC.put(BaseContextConstants.LOG_TRACE_ID, StrUtil.isEmpty(traceId) ? StrUtil.EMPTY : traceId);
            MDC.put(BaseContextConstants.TENANT, BaseContextHandler.getTenant());
            MDC.put(BaseContextConstants.JWT_KEY_USER_ID, String.valueOf(BaseContextHandler.getUserId()));
        } catch (Exception e) {
            log.warn("解析token信息时，发生异常. url=" + request.getRequestURI(), e);
        }
        return super.preHandle(request, response, handler);
    }

    private String getHeader(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        return URLUtil.decode(value);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContextHandler.remove();
        super.afterCompletion(request, response, handler, ex);
    }

}
