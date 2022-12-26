package top.tangyh.basic.boot.handler;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import top.tangyh.basic.annotation.base.IgnoreResponseBodyAdvice;
import top.tangyh.basic.base.R;

/**
 * 全局响应体包装
 *
 * @author zuihou
 * @date 2020/12/24 8:09 下午
 */
public class AbstractGlobalResponseBodyAdvice implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        // 类上如果被 IgnoreResponseBodyAdvice 标识就不拦截
        if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseBodyAdvice.class)) {
            return false;
        }

        // 方法上被标注也不拦截
        if (methodParameter.getMethod().isAnnotationPresent(IgnoreResponseBodyAdvice.class)) {
            return false;
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (o == null) {
            return null;
        }
        if (o instanceof R) {
            return o;
        }

        return R.success(o);
    }
}
