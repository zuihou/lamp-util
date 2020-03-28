package com.github.zuihou.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.zuihou.security.component.Auth2ExceptionSerializer;
import org.springframework.http.HttpStatus;

/**
 * 方法不允许异常
 *
 * @author zuihou
 * @date 2020年03月25日23:17:02
 */
@JsonSerialize(using = Auth2ExceptionSerializer.class)
public class MethodNotAllowedException extends Auth2Exception {

    public MethodNotAllowedException(String msg, Throwable t) {
        super(msg);
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "method_not_allowed";
    }

    @Override
    public int getHttpErrorCode() {
        return HttpStatus.METHOD_NOT_ALLOWED.value();
    }

}
