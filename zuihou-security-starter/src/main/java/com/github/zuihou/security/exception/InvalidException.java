package com.github.zuihou.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.zuihou.security.component.Auth2ExceptionSerializer;

/**
 * 无效的异常
 *
 * @author zuihou
 * @date 2020年03月25日23:15:34
 */
@JsonSerialize(using = Auth2ExceptionSerializer.class)
public class InvalidException extends Auth2Exception {

    public InvalidException(String msg, Throwable t) {
        super(msg);
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "invalid_exception";
    }

    @Override
    public int getHttpErrorCode() {
        return 426;
    }

}
