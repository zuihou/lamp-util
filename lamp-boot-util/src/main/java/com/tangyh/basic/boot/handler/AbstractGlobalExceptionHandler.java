package com.tangyh.basic.boot.handler;

import cn.hutool.core.util.StrUtil;
import com.tangyh.basic.base.R;
import com.tangyh.basic.exception.BizException;
import com.tangyh.basic.exception.ForbiddenException;
import com.tangyh.basic.exception.UnauthorizedException;
import com.tangyh.basic.exception.code.ExceptionCode;
import com.tangyh.basic.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tangyh.basic.exception.code.ExceptionCode.METHOD_NOT_ALLOWED;
import static com.tangyh.basic.exception.code.ExceptionCode.REQUIRED_FILE_PARAM_EX;


/**
 * 全局异常统一处理
 *
 * @author zuihou
 * @date 2017-12-13 17:04
 */
@SuppressWarnings("AlibabaUndefineMagicConstant")
@Slf4j
public abstract class AbstractGlobalExceptionHandler {
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> bizException(BizException ex) {
        log.warn("BizException:", ex);
        return R.result(ex.getCode(), null, ex.getMessage(), ex.getLocalizedMessage()).setPath(getPath());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<?> forbiddenException(ForbiddenException ex) {
        log.warn("BizException:", ex);
        return R.result(ex.getCode(), null, ex.getMessage(), ex.getLocalizedMessage()).setPath(getPath());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R<?> unauthorizedException(UnauthorizedException ex) {
        log.warn("BizException:", ex);
        return R.result(ex.getCode(), null, ex.getMessage(), ex.getLocalizedMessage()).setPath(getPath());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("HttpMessageNotReadableException:", ex);
        String message = ex.getMessage();
        if (StrUtil.containsAny(message, "Could not read document:")) {
            String msg = String.format("无法正确的解析json类型的参数：%s", StrUtil.subBetween(message, "Could not read document:", " at "));
            return R.result(ExceptionCode.PARAM_EX.getCode(), null, msg, ex.getMessage()).setPath(getPath());
        }
        return R.result(ExceptionCode.PARAM_EX.getCode(), null, ExceptionCode.PARAM_EX.getMsg(), ex.getMessage()).setPath(getPath());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> bindException(BindException ex) {
        log.warn("BindException:", ex);
        try {
            String msg = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
            if (StrUtil.isNotEmpty(msg)) {
                return R.result(ExceptionCode.PARAM_EX.getCode(), null, msg, ex.getMessage()).setPath(getPath());
            }
        } catch (Exception ee) {
            log.debug("获取异常描述失败", ee);
        }
        StringBuilder msg = new StringBuilder();
        List<FieldError> fieldErrors = ex.getFieldErrors();
        fieldErrors.forEach((oe) ->
                msg.append("参数:[").append(oe.getObjectName())
                        .append(".").append(oe.getField())
                        .append("]的传入值:[").append(oe.getRejectedValue()).append("]与预期的字段类型不匹配.")
        );
        return R.result(ExceptionCode.PARAM_EX.getCode(), null, msg.toString(), ex.getMessage()).setPath(getPath());
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("MethodArgumentTypeMismatchException:", ex);
        String msg = "参数：[" + ex.getName() + "]的传入值：[" + ex.getValue() +
                "]与预期的字段类型：[" + Objects.requireNonNull(ex.getRequiredType()).getName() + "]不匹配";
        return R.result(ExceptionCode.PARAM_EX.getCode(), null, msg, ex.getMessage()).setPath(getPath());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> illegalStateException(IllegalStateException ex) {
        log.warn("IllegalStateException:", ex);
        return R.result(ExceptionCode.ILLEGAL_ARGUMENT_EX.getCode(), null, ExceptionCode.ILLEGAL_ARGUMENT_EX.getMsg(), ex.getMessage()).setPath(getPath());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> missingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.warn("MissingServletRequestParameterException:", ex);
        return R.result(ExceptionCode.ILLEGAL_ARGUMENT_EX.getCode(), null, "缺少必须的[" + ex.getParameterType() + "]类型的参数[" + ex.getParameterName() + "]", ex.getMessage()).setPath(getPath());
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> nullPointerException(NullPointerException ex) {
        log.warn("NullPointerException:", ex);
        return R.result(ExceptionCode.NULL_POINT_EX.getCode(), null, ExceptionCode.NULL_POINT_EX.getMsg(), ex.getMessage()).setPath(getPath());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> illegalArgumentException(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException:", ex);
        return R.result(ExceptionCode.ILLEGAL_ARGUMENT_EX.getCode(), null, ExceptionCode.ILLEGAL_ARGUMENT_EX.getMsg(), ex.getMessage()).setPath(getPath());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        log.warn("HttpMediaTypeNotSupportedException:", ex);
        MediaType contentType = ex.getContentType();
        if (contentType != null) {
            return R.result(ExceptionCode.MEDIA_TYPE_EX.getCode(), null, "请求类型(Content-Type)[" + contentType + "] 与实际接口的请求类型不匹配", ex.getMessage()).setPath(getPath());
        }
        return R.result(ExceptionCode.MEDIA_TYPE_EX.getCode(), null, "无效的Content-Type类型", ex.getMessage()).setPath(getPath());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> missingServletRequestPartException(MissingServletRequestPartException ex) {
        log.warn("MissingServletRequestPartException:", ex);
        return R.result(REQUIRED_FILE_PARAM_EX.getCode(), null, REQUIRED_FILE_PARAM_EX.getMsg(), ex.getMessage()).setPath(getPath());
    }

    @ExceptionHandler(ServletException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> servletException(ServletException ex) {
        log.warn("ServletException:", ex);
        String msg = "UT010016: Not a multi part request";
        if (msg.equalsIgnoreCase(ex.getMessage())) {
            return R.result(REQUIRED_FILE_PARAM_EX.getCode(), null, REQUIRED_FILE_PARAM_EX.getMsg(), ex.getMessage());
        }
        return R.result(ExceptionCode.SYSTEM_BUSY.getCode(), null, ex.getMessage(), ex.getMessage()).setPath(getPath());
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> multipartException(MultipartException ex) {
        log.warn("MultipartException:", ex);
        return R.result(REQUIRED_FILE_PARAM_EX.getCode(), null, REQUIRED_FILE_PARAM_EX.getMsg(), ex.getMessage()).setPath(getPath());
    }

    /**
     * jsr 规范中的验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> constraintViolationException(ConstraintViolationException ex) {
        log.warn("ConstraintViolationException:", ex);
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String message = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));

        return R.result(ExceptionCode.BASE_VALID_PARAM.getCode(), null, message, ex.getMessage()).setPath(getPath());
    }

    /**
     * spring 封装的参数验证异常， 在controller中没有写result参数时，会进入
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("MethodArgumentNotValidException:", ex);
        return R.result(ExceptionCode.BASE_VALID_PARAM.getCode(), null, Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage(), ex.getMessage()).setPath(getPath());
    }

    private String getPath() {
        String path = StrPool.EMPTY;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            path = request.getRequestURI();
        }
        return path;
    }

    /**
     * 其他异常
     *
     * @param ex 异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> otherExceptionHandler(Exception ex) {
        log.warn("Exception:", ex);
        if (ex.getCause() instanceof BizException) {
            return this.bizException((BizException) ex.getCause());
        }
        return R.result(ExceptionCode.SYSTEM_BUSY.getCode(), null, ExceptionCode.SYSTEM_BUSY.getMsg(), ex.getMessage()).setPath(getPath());
    }


    /**
     * 返回状态码:405
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("HttpRequestMethodNotSupportedException:", ex);
        return R.result(METHOD_NOT_ALLOWED.getCode(), null, METHOD_NOT_ALLOWED.getMsg(), ex.getMessage()).setPath(getPath());
    }


    @ExceptionHandler(PersistenceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> persistenceException(PersistenceException ex) {
        log.warn("PersistenceException:", ex);
        if (ex.getCause() instanceof BizException) {
            BizException cause = (BizException) ex.getCause();
            return R.result(cause.getCode(), null, cause.getMessage());
        }
        return R.result(ExceptionCode.SQL_EX.getCode(), null, ExceptionCode.SQL_EX.getMsg(), ex.getMessage()).setPath(getPath());
    }

    @ExceptionHandler(MyBatisSystemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> myBatisSystemException(MyBatisSystemException ex) {
        log.warn("PersistenceException:", ex);
        if (ex.getCause() instanceof PersistenceException) {
            return this.persistenceException((PersistenceException) ex.getCause());
        }
        return R.result(ExceptionCode.SQL_EX.getCode(), null, ExceptionCode.SQL_EX.getMsg(), ex.getMessage()).setPath(getPath());
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> sqlException(SQLException ex) {
        log.warn("SQLException:", ex);
        return R.result(ExceptionCode.SQL_EX.getCode(), null, ExceptionCode.SQL_EX.getMsg(), ex.getMessage()).setPath(getPath());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> dataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.warn("DataIntegrityViolationException:", ex);
        return R.result(ExceptionCode.SQL_EX.getCode(), null, ExceptionCode.SQL_EX.getMsg(), ex.getMessage()).setPath(getPath());
    }

}
