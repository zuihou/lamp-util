package com.github.zuihou.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.zuihou.security.component.Auth2ExceptionSerializer;
import org.springframework.http.HttpStatus;

/**
 * 未经授权异常
 *
 * @author zuihou
 * @date 2020年03月25日23:14:25
 */
@JsonSerialize(using = Auth2ExceptionSerializer.class)
public class UnauthorizedException extends Auth2Exception {

    public UnauthorizedException(String msg, Throwable t) {
        super(msg);
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "unauthorized";
    }

    @Override
    public int getHttpErrorCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }

}
