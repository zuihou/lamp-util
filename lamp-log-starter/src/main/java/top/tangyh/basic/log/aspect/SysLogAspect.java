package top.tangyh.basic.log.aspect;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import top.tangyh.basic.annotation.log.SysLog;
import top.tangyh.basic.base.R;
import top.tangyh.basic.context.ContextConstants;
import top.tangyh.basic.context.ContextUtil;
import top.tangyh.basic.context.ThreadLocalParam;
import top.tangyh.basic.jackson.JsonUtil;
import top.tangyh.basic.model.log.OptLogDTO;
import top.tangyh.basic.log.event.SysLogEvent;
import top.tangyh.basic.log.util.LogUtil;
import top.tangyh.basic.utils.SpringUtils;
import top.tangyh.basic.utils.StrPool;
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
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
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
    private static final String FORM_DATA_CONTENT_TYPE = "multipart/form-data";
    /**
     * 用于SpEL表达式解析.
     */
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    /***
     * 定义controller切入点拦截规则：拦截标记SysLog注解和指定包下的方法
     * 2个表达式加起来才能拦截所有Controller 或者继承了BaseController的方法
     *
     * execution(public * top.tangyh.basic.base.controller.*.*(..)) 解释：
     * 第一个* 任意返回类型
     * 第二个* top.tangyh.basic.base.controller包下的所有类
     * 第三个* 类下的所有方法
     * ()中间的.. 任意参数
     *
     * \@annotation(top.tangyh.basic.annotation.log.SysLog) 解释：
     * 标记了@SysLog 注解的方法
     */
    @Pointcut("execution(public * top.tangyh.basic.base.controller.*.*(..)) || @annotation(top.tangyh.basic.annotation.log.SysLog)")
    public void sysLogAspect() {

    }

    /**
     * 用于获取方法参数定义名字.
     */
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 返回通知
     *
     * @param ret       返回值
     * @param joinPoint 端点
     */
    @AfterReturning(returning = "ret", pointcut = "sysLogAspect()")
    public void doAfterReturning(JoinPoint joinPoint, Object ret) {
        tryCatch(p -> {
            SysLog sysLog = LogUtil.getTargetAnnotation(joinPoint);
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
            SysLog sysLog = LogUtil.getTargetAnnotation(joinPoint);
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
        tryCatch(val -> {
            SysLog sysLog = LogUtil.getTargetAnnotation(joinPoint);
            if (check(joinPoint, sysLog)) {
                return;
            }
            OptLogDTO optLogDTO = buildOptLogDTO(joinPoint, sysLog);
            THREAD_LOCAL.set(optLogDTO);
        });
    }

    @NonNull
    private OptLogDTO buildOptLogDTO(JoinPoint joinPoint, SysLog sysLog) {
        // 开始时间
        OptLogDTO optLogDTO = get();
        optLogDTO.setCreatedBy(ContextUtil.getUserId());
        optLogDTO.setUserName(ContextUtil.getName());
        setDescription(joinPoint, sysLog, optLogDTO);
        // 类名
        optLogDTO.setClassPath(joinPoint.getTarget().getClass().getName());
        //获取执行的方法名
        optLogDTO.setActionMethod(joinPoint.getSignature().getName());

        HttpServletRequest request = setParams(joinPoint, sysLog, optLogDTO);
        optLogDTO.setRequestIp(JakartaServletUtil.getClientIP(request));
        optLogDTO.setRequestUri(URLUtil.getPath(request.getRequestURI()));
        optLogDTO.setHttpMethod(request.getMethod());
        optLogDTO.setUa(StrUtil.sub(request.getHeader("user-agent"), 0, 500));
        if (ContextUtil.getBoot()) {
            optLogDTO.setTenantCode(ContextUtil.getTenant());
            optLogDTO.setSubTenantCode(ContextUtil.getSubTenant());
        } else {
            optLogDTO.setTenantCode(request.getHeader(ContextConstants.JWT_KEY_TENANT));
            optLogDTO.setSubTenantCode(request.getHeader(ContextConstants.JWT_KEY_SUB_TENANT));
        }
        optLogDTO.setTrace(MDC.get(ContextConstants.LOG_TRACE_ID));
        if (StrUtil.isEmpty(optLogDTO.getTrace())) {
            optLogDTO.setTrace(request.getHeader(ContextConstants.TRACE_ID_HEADER));
        }
        optLogDTO.setStartTime(LocalDateTime.now());
        return optLogDTO;
    }

    @NonNull
    private HttpServletRequest setParams(JoinPoint joinPoint, SysLog sysLog, OptLogDTO optLogDTO) {
        // 参数
        Object[] args = joinPoint.getArgs();

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes(), "只能在Spring Web环境使用@SysLog记录日志")).getRequest();
        if (sysLog.request()) {
            String strArgs = getArgs(args, request);
            optLogDTO.setParams(getText(strArgs));
        }
        return request;
    }

    private void setDescription(JoinPoint joinPoint, SysLog sysLog, OptLogDTO optLogDTO) {
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
     * @return true 表示不需要记录日志
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
        Object[] params = Arrays.stream(args).filter(item -> !(item instanceof ServletRequest || item instanceof ServletResponse)).toArray();

        try {
            if (!request.getContentType().contains(FORM_DATA_CONTENT_TYPE)) {
                strArgs = JsonUtil.toJson(params);
            }
        } catch (Exception e) {
            try {
                strArgs = Arrays.toString(params);
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
            String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
            if (paramNames != null && paramNames.length > 0) {
                Expression expression = spelExpressionParser.parseExpression(spEl);
                // spring的表达式上下文对象
                EvaluationContext context = new StandardEvaluationContext();
                // 给上下文赋值
                for (int i = 0; i < args.length; i++) {
                    context.setVariable(paramNames[i], args[i]);
                    context.setVariable("p" + i, args[i]);
                }
                ThreadLocalParam tlp = new ThreadLocalParam();
                BeanUtil.fillBeanWithMap(ContextUtil.getLocalMap(), tlp, true);
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
