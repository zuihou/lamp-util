package org.springframework.cloud.openfeign;

import com.fasterxml.jackson.databind.JsonNode;
import com.tangyh.basic.base.R;
import com.tangyh.basic.jackson.JsonUtil;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 全局@FeignClient的 fallBackFactory 属性的代理类
 * <p>
 * 参考： https://www.jianshu.com/p/4a451e491560
 *
 * @author zuihou
 */
@Slf4j
@EqualsAndHashCode(of = {"targetType"})
@AllArgsConstructor
public class GlobalFallbackFactoryProxy<T> implements MethodInterceptor {
    private static final String R_CODE = "code";
    private final Class<T> targetType;
    private final String targetName;
    private final Throwable cause;

    /**
     * 1. 返回类型是集合类的，直接返回空集合
     * 2. 返回类型不是R的，直接返回null
     * 3. 返回类型是R的，并且异常属于FeignException，则将 FeignException 异常中的存放的异常信息解析成R类型的json格式返回
     *
     * @param o           代理对象
     * @param method      方法
     * @param args        参数
     * @param methodProxy 方法代理
     */
    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) {
        String errorMessage = cause.getMessage();
        log.error("服务:{}, 方法{}#{} 调用出错", targetName, targetType.getName(), method.getName());
        log.error("异常堆栈:", cause);
        Class<?> returnType = method.getReturnType();
        if (Map.class == returnType) {
            return Collections.emptyMap();
        }
        if (Set.class == returnType) {
            return Collections.emptySet();
        }
        if (List.class == returnType || Collection.class == returnType) {
            return Collections.emptyList();
        }
        if (R.class != returnType) {
            return null;
        }
        if (!(cause instanceof FeignException)) {
            return R.fail(R.TIMEOUT_CODE, errorMessage);
        }
        FeignException exception = (FeignException) cause;
        Optional<ByteBuffer> byteBuffer = exception.responseBody();
        if (!byteBuffer.isPresent()) {
            return R.fail(R.TIMEOUT_CODE, errorMessage);
        }
        byte[] c = byteBuffer.get().array();

        JsonNode resultNode = JsonUtil.readTree(c);
        if (resultNode.has(R_CODE)) {
            return JsonUtil.toPojo(resultNode, R.class);
        }
        return R.fail(resultNode.toString());
    }
}
