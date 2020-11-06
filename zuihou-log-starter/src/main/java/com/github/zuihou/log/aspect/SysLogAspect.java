package com.github.zuihou.log.aspect;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.github.zuihou.base.R;
import com.github.zuihou.context.BaseContextConstants;
import com.github.zuihou.context.BaseContextHandler;
import com.github.zuihou.context.ThreadLocalParam;
import com.github.zuihou.jackson.JsonUtil;
import com.github.zuihou.log.annotation.SysLog;
import com.github.zuihou.log.entity.OptLogDTO;
import com.github.zuihou.log.event.SysLogEvent;
import com.github.zuihou.log.util.LogUtil;
import com.github.zuihou.utils.SpringUtils;
import com.github.zuihou.utils.StrPool;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 操作日志使用spring event异步入库
 *
 * @author zuihou
 * @date 2019-07-01 15:15
 */
@Slf4j
@Aspect
public class SysLogAspect {

    public static final int MAX_LENGTH = 65535;
    private static final ThreadLocal<OptLogDTO> THREAD_LOCAL = new ThreadLocal<>();
    private final static String FORM_DATA_CONTENT_TYPE = "multipart/form-data";
    /**
     * 用于SpEL表达式解析.
     */
    private final SpelExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();

    /***
     * 定义controller切入点拦截规则：拦截标记SysLog注解和指定包下的方法
     * 2个表达式加起来才能拦截所有Controller 或者继承了BaseController的方法
     *
     * execution(public * com.github.zuihou.base.controller.*.*(..)) 解释：
     * 第一个* 任意返回类型
     * 第二个* com.github.zuihou.base.controller包下的所有类
     * 第三个* 类下的所有方法
     * ()中间的.. 任意参数
     *
     * \@annotation(com.github.zuihou.log.annotation.SysLog) 解释：
     * 标记了@SysLog 注解的方法
     */
    @Pointcut("execution(public * com.github.zuihou.base.controller.*.*(..)) || @annotation(com.github.zuihou.log.annotation.SysLog)")
    public void sysLogAspect() {

    }

    /**
     * 用于获取方法参数定义名字.
     */
    private final DefaultParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    /**
     * 返回通知
     *
     * @param ret       返回值
     * @param joinPoint 端点
     */
    @AfterReturning(returning = "ret", pointcut = "sysLogAspect()")
    public void doAfterReturning(JoinPoint joinPoint, Object ret) {
        tryCatch((aaa) -> {
            SysLog sysLog = LogUtil.getTargetAnno(joinPoint);
            if (check(joinPoint, sysLog)) {
                return;
            }

            R r = Convert.convert(R.class, ret);
            OptLogDTO sysLogDTO = get();
            if (r == null) {
                sysLogDTO.setType("OPT");
                if (sysLog.response()) {
                    sysLogDTO.setResult(getText(String.valueOf(ret == null ? StrPool.EMPTY : ret)));
                }
            } else {
                if (r.getIsSuccess()) {
                    sysLogDTO.setType("OPT");
                } else {
                    sysLogDTO.setType("EX");
                    sysLogDTO.setExDetail(r.getMsg());
                }
                if (sysLog.response()) {
                    sysLogDTO.setResult(getText(r.toString()));
                }
            }

            publishEvent(sysLogDTO);
        });

    }

    /**
     * 异常通知
     *
     * @param joinPoint 端点
     * @param e         异常
     */
    @AfterThrowing(pointcut = "sysLogAspect()", throwing = "e")
    public void doAfterThrowable(JoinPoint joinPoint, Throwable e) {
        tryCatch((aaa) -> {
            SysLog sysLog = LogUtil.getTargetAnno(joinPoint);
            if (check(joinPoint, sysLog)) {
                return;
            }

            OptLogDTO optLogDTO = get();
            optLogDTO.setType("EX");

            // 遇到错误时，请求参数若为空，则记录
            if (!sysLog.request() && sysLog.requestByError() && StrUtil.isEmpty(optLogDTO.getParams())) {
                Object[] args = joinPoint.getArgs();
                HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                String strArgs = getArgs(args, request);
                optLogDTO.setParams(getText(strArgs));
            }

            // 异常对象
            optLogDTO.setExDetail(ExceptionUtil.stacktraceToString(e, MAX_LENGTH));
            // 异常信息
            optLogDTO.setExDesc(ExceptionUtil.stacktraceToString(e, MAX_LENGTH));

            publishEvent(optLogDTO);
        });
    }

    /**
     * 执行方法之前
     *
     * @param joinPoint 端点
     */
    @Before(value = "sysLogAspect()")
    public void doBefore(JoinPoint joinPoint) {
        tryCatch((val) -> {
            SysLog sysLog = LogUtil.getTargetAnno(joinPoint);
            if (check(joinPoint, sysLog)) {
                return;
            }

            // 开始时间
            OptLogDTO optLogDTO = get();
            optLogDTO.setCreateUser(BaseContextHandler.getUserId());
            optLogDTO.setUserName(BaseContextHandler.getName());
            String controllerDescription = "";
            Api api = joinPoint.getTarget().getClass().getAnnotation(Api.class);
            if (api != null) {
                String[] tags = api.tags();
                if (ArrayUtil.isNotEmpty(tags)) {
                    controllerDescription = tags[0];
                }
            }

            String controllerMethodDescription = LogUtil.getDescribe(sysLog);

            if (StrUtil.isNotEmpty(controllerMethodDescription) && StrUtil.contains(controllerMethodDescription, StrPool.HASH)) {
                //获取方法参数值
                Object[] args = joinPoint.getArgs();

                MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
                controllerMethodDescription = getValBySpEl(controllerMethodDescription, methodSignature, args);
            }

            if (StrUtil.isEmpty(controllerDescription)) {
                optLogDTO.setDescription(controllerMethodDescription);
            } else {
                if (sysLog.controllerApiValue()) {
                    optLogDTO.setDescription(controllerDescription + "-" + controllerMethodDescription);
                } else {
                    optLogDTO.setDescription(controllerMethodDescription);
                }
            }

            // 类名
            optLogDTO.setClassPath(joinPoint.getTarget().getClass().getName());
            //获取执行的方法名
            optLogDTO.setActionMethod(joinPoint.getSignature().getName());

            // 参数
            Object[] args = joinPoint.getArgs();

            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes(), "只能在Spring Web环境使用@SysLog记录日志")).getRequest();
            if (sysLog.request()) {
                String strArgs = getArgs(args, request);
                optLogDTO.setParams(getText(strArgs));
            }

            optLogDTO.setTrace(MDC.get(BaseContextConstants.LOG_TRACE_ID));

            optLogDTO.setRequestIp(ServletUtil.getClientIP(request));
            optLogDTO.setRequestUri(URLUtil.getPath(request.getRequestURI()));
            optLogDTO.setHttpMethod(request.getMethod());
            optLogDTO.setUa(StrUtil.sub(request.getHeader("user-agent"), 0, 500));
            if (BaseContextHandler.getBoot()) {
                optLogDTO.setTenantCode(BaseContextHandler.getTenant());
            } else {
                optLogDTO.setTenantCode(request.getHeader(BaseContextConstants.JWT_KEY_TENANT));
            }
            if (StrUtil.isEmpty(optLogDTO.getTrace())) {
                optLogDTO.setTrace(request.getHeader(BaseContextConstants.TRACE_ID_HEADER));
            }
            optLogDTO.setStartTime(LocalDateTime.now());

            THREAD_LOCAL.set(optLogDTO);
        });
    }


    private OptLogDTO get() {
        OptLogDTO sysLog = THREAD_LOCAL.get();
        if (sysLog == null) {
            return new OptLogDTO();
        }
        return sysLog;
    }

    private void tryCatch(Consumer<String> consumer) {
        try {
            consumer.accept("");
        } catch (Exception e) {
            log.warn("记录操作日志异常", e);
            THREAD_LOCAL.remove();
        }
    }

    private void publishEvent(OptLogDTO sysLog) {
        sysLog.setFinishTime(LocalDateTime.now());
        sysLog.setConsumingTime(sysLog.getStartTime().until(sysLog.getFinishTime(), ChronoUnit.MILLIS));
        SpringUtils.publishEvent(new SysLogEvent(sysLog));
        THREAD_LOCAL.remove();
    }

    /**
     * 监测是否需要记录日志
     *
     * @param joinPoint 端点
     * @param sysLog    操作日志
     * @return true 表示需要记录日志
     */
    private boolean check(JoinPoint joinPoint, SysLog sysLog) {
        if (sysLog == null || !sysLog.enabled()) {
            return true;
        }
        // 读取目标类上的注解
        SysLog targetClass = joinPoint.getTarget().getClass().getAnnotation(SysLog.class);
        // 加上 sysLog == null 会导致父类上的方法永远需要记录日志
        return targetClass != null && !targetClass.enabled();
    }

    /**
     * 截取指定长度的字符串
     *
     * @param val 参数
     * @return 截取文本
     */
    private String getText(String val) {
        return StrUtil.sub(val, 0, 65535);
    }

    private String getArgs(Object[] args, HttpServletRequest request) {
        String strArgs = StrPool.EMPTY;

        try {
            if (!request.getContentType().contains(FORM_DATA_CONTENT_TYPE)) {
                strArgs = JsonUtil.toJson(args);
            }
        } catch (Exception e) {
            try {
                strArgs = Arrays.toString(args);
            } catch (Exception ex) {
                log.warn("解析参数异常", ex);
            }
        }
        return strArgs;
    }

    /**
     * 解析spEL表达式
     */
    private String getValBySpEl(String spEl, MethodSignature methodSignature, Object[] args) {
        try {
            //获取方法形参名数组
            String[] paramNames = NAME_DISCOVERER.getParameterNames(methodSignature.getMethod());
            if (paramNames != null && paramNames.length > 0) {
                Expression expression = SPEL_EXPRESSION_PARSER.parseExpression(spEl);
                // spring的表达式上下文对象
                EvaluationContext context = new StandardEvaluationContext();
                // 给上下文赋值
                for (int i = 0; i < args.length; i++) {
                    context.setVariable(paramNames[i], args[i]);
                    context.setVariable("p" + i, args[i]);
                }
                ThreadLocalParam tlp = new ThreadLocalParam();
                BeanUtil.fillBeanWithMap(BaseContextHandler.getLocalMap(), tlp, true);
                context.setVariable("threadLocal", tlp);
                Object value = expression.getValue(context);
                return value == null ? spEl : value.toString();
            }
        } catch (Exception e) {
            log.warn("解析操作日志的el表达式出错", e);
        }
        return spEl;
    }


}
