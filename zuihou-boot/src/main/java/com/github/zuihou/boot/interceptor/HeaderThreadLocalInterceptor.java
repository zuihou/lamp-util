package com.github.zuihou.boot.interceptor;

import cn.hutool.core.util.StrUtil;
import com.github.zuihou.context.BaseContextConstants;
import com.github.zuihou.context.BaseContextHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.github.zuihou.boot.utils.WebUtils.getHeader;

/**
 * 拦截器：
 * 将请求头数据，封装到BaseContextHandler(ThreadLocal)
 * <p>
 * 该拦截器要优先于系统中其他的业务拦截器
 * <p>
 *
 * @author zuihou
 * @date 2020/10/31 9:49 下午
 */
@Slf4j
public class HeaderThreadLocalInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        }

        if (!BaseContextHandler.getBoot()) {
            BaseContextHandler.setUserId(getHeader(request, BaseContextConstants.JWT_KEY_USER_ID));
            BaseContextHandler.setAccount(getHeader(request, BaseContextConstants.JWT_KEY_ACCOUNT));
            BaseContextHandler.setName(getHeader(request, BaseContextConstants.JWT_KEY_NAME));
            BaseContextHandler.setTenant(getHeader(request, BaseContextConstants.JWT_KEY_TENANT));

            String traceId = request.getHeader(BaseContextConstants.TRACE_ID_HEADER);
            MDC.put(BaseContextConstants.LOG_TRACE_ID, StrUtil.isEmpty(traceId) ? StrUtil.EMPTY : traceId);
            MDC.put(BaseContextConstants.JWT_KEY_TENANT, getHeader(request, BaseContextConstants.JWT_KEY_TENANT));
            MDC.put(BaseContextConstants.JWT_KEY_USER_ID, getHeader(request, BaseContextConstants.JWT_KEY_USER_ID));
        }
        // cloud
        BaseContextHandler.setGrayVersion(getHeader(request, BaseContextConstants.GRAY_VERSION));
        return super.preHandle(request, response, handler);
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContextHandler.remove();
        super.afterCompletion(request, response, handler, ex);
    }
}
