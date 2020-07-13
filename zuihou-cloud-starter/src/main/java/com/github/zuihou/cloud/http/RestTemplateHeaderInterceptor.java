package com.github.zuihou.cloud.http;

import cn.hutool.core.util.ObjectUtil;
import com.github.zuihou.context.BaseContextHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.github.zuihou.cloud.interceptor.FeignAddHeaderRequestInterceptor.HEADER_NAME_LIST;

/**
 * RestTemplateHeaderInterceptor 传递Request header
 *
 * @author zuihou
 */
@AllArgsConstructor
@Slf4j
public class RestTemplateHeaderInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] bytes,
            ClientHttpRequestExecution execution) throws IOException {

        HttpHeaders httpHeaders = request.getHeaders();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            HEADER_NAME_LIST.forEach((headerName) -> {
                if (ObjectUtil.isNotEmpty(BaseContextHandler.get(headerName))) {
                    httpHeaders.add(headerName, BaseContextHandler.get(headerName));
                }
            });
            return execution.execute(request, bytes);
        }

        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        if (request == null) {
            log.warn("path={}, 在FeignClient API接口未配置FeignConfiguration类， 故而无法在远程调用时获取请求头中的参数!", httpServletRequest.getRequestURI());
            return execution.execute(request, bytes);
        }
        HEADER_NAME_LIST.forEach((headerName) -> {
            if (ObjectUtil.isNotEmpty(httpServletRequest.getHeader(headerName))) {
                httpHeaders.add(headerName, httpServletRequest.getHeader(headerName));
            }
        });

        return execution.execute(request, bytes);
    }
}
