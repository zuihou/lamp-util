package com.github.zuihou.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.zuihou.security.component.Auth2ExceptionSerializer;
import org.springframework.http.HttpStatus;

/**
 * 服务错误异常
 *
 * @author zuihou
 * @date 2020年03月25日23:16:37
 */
@JsonSerialize(using = Auth2ExceptionSerializer.class)
public class ServerErrorException extends Auth2Exception {

    public ServerErrorException(String msg, Throwable t) {
        super(msg);
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "server_error";
    }

    @Override
    public int getHttpErrorCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

}
