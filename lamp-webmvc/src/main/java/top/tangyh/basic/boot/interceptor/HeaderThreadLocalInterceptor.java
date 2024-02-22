package top.tangyh.basic.boot.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import top.tangyh.basic.context.ContextConstants;
import top.tangyh.basic.context.ContextUtil;

import java.util.Map;

import static top.tangyh.basic.boot.utils.WebUtils.getHeader;

/**
 * 拦截器：
 * 将请求头数据，封装到BaseContextHandler(ThreadLocal)
 * <p>
 * 该拦截器要优先于系统中其他的业务拦截器
 * <p>
 * lamp-cloud 项目启用该拦截器， 通过 lamp.webmvc.header = true
 * lamp-boot 项目禁用该拦截器， 通过 lamp.webmvc.header = false
 * <p>
 *
 * @author zuihou
 * @date 2020/10/31 9:49 下午
 */
@Slf4j
public class HeaderThreadLocalInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        ContextUtil.setPath(getHeader(request, ContextConstants.PATH_HEADER));

        ContextUtil.setUserId(getHeader(request, ContextConstants.USER_ID_HEADER));
        ContextUtil.setEmployeeId(getHeader(request, ContextConstants.EMPLOYEE_ID_HEADER));
        ContextUtil.setApplicationId(getHeader(request, ContextConstants.APPLICATION_ID_HEADER));
        ContextUtil.setCurrentCompanyId(getHeader(request, ContextConstants.CURRENT_COMPANY_ID_HEADER));
        ContextUtil.setCurrentTopCompanyId(getHeader(request, ContextConstants.CURRENT_TOP_COMPANY_ID_HEADER));
        ContextUtil.setCurrentDeptId(getHeader(request, ContextConstants.CURRENT_DEPT_ID_HEADER));
        ContextUtil.setClientId(getHeader(request, ContextConstants.CLIENT_ID_HEADER));
        ContextUtil.setLogTraceId(getHeader(request, ContextConstants.TRACE_ID_HEADER));
        ContextUtil.setToken(getHeader(request, ContextConstants.TOKEN_HEADER));
        Map<String, String> localMap = ContextUtil.getLocalMap();
        localMap.forEach(MDC::put);

        ContextUtil.setGrayVersion(getHeader(request, ContextConstants.GRAY_VERSION));
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ContextUtil.remove();
    }
}
