package com.github.zuihou.security.aspect;

import cn.hutool.core.util.StrUtil;
import com.github.zuihou.exception.BizException;
import com.github.zuihou.exception.code.ExceptionCode;
import com.github.zuihou.security.annotation.PreAuth;
import com.github.zuihou.security.auth.AuthFun;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * AOP 鉴权
 *
 * @author zuihou
 * @date 2020年03月29日21:17:49
 */
@Aspect
@Slf4j
public class AuthAspect implements ApplicationContextAware {

    /**
     * 表达式处理
     */
    private static final ExpressionParser SPEL_PARSER = new SpelExpressionParser();
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
    private final AuthFun authFun;
    private ApplicationContext ac;

    public AuthAspect(AuthFun authFun) {
        this.authFun = authFun;
    }

    /**
     * 获取方法参数信息
     *
     * @param method         方法
     * @param parameterIndex 参数序号
     * @return {MethodParameter}
     */
    public static MethodParameter getMethodParameter(Method method, int parameterIndex) {
        MethodParameter methodParameter = new SynthesizingMethodParameter(method, parameterIndex);
        methodParameter.initParameterNameDiscovery(PARAMETER_NAME_DISCOVERER);
        return methodParameter;
    }

    /**
     * 切 方法 和 类上的 @PreAuth 注解
     *
     * @param point 切点
     * @return Object
     * @throws Throwable 没有权限的异常
     */
    @Around("execution(public * com.github.zuihou.base.controller.*.*(..)) || " +
            "@annotation(com.github.zuihou.security.annotation.PreAuth) || " +
            "@within(com.github.zuihou.security.annotation.PreAuth)"
    )
    public Object preAuth(ProceedingJoinPoint point) throws Throwable {
        if (handleAuth(point)) {
            return point.proceed();
        }
        throw BizException.wrap(ExceptionCode.UNAUTHORIZED);
    }

    /**
     * 处理权限
     *
     * @param point 切点
     */
    private boolean handleAuth(ProceedingJoinPoint point) {
        MethodSignature ms = (MethodSignature) point.getSignature();
        Method method = ms.getMethod();
        // 读取权限注解，优先方法上，没有则读取类
        PreAuth preAuth = null;
        if (point.getSignature() instanceof MethodSignature) {
            method = ((MethodSignature) point.getSignature()).getMethod();
            if (method != null) {
                preAuth = method.getAnnotation(PreAuth.class);
            }
        }
        // 读取目标类上的权限注解
        PreAuth targetClass = point.getTarget().getClass().getAnnotation(PreAuth.class);
        //方法和类上 均无注解
        if (preAuth == null && targetClass == null) {
            log.debug("执行方法[{}]无需校验权限", method.getName());
            return true;
        }

        // 方法上禁用
        if (preAuth != null && !preAuth.enabled()) {
            log.debug("执行方法[{}]无需校验权限", method.getName());
            return true;
        }

        // 类上禁用
        if (targetClass != null && !targetClass.enabled()) {
            log.debug("执行方法[{}]无需校验权限", method.getName());
            return true;
        }

        String condition = null;
        if (preAuth == null) {
            condition = targetClass.value();
        } else {
            // 判断表达式
            condition = preAuth.value();
        }
        if (StrUtil.isBlank(condition)) {
            return true;
        }
        if (condition.contains("{}")) {
            if (targetClass != null && StrUtil.isNotBlank(targetClass.replace())) {
                condition = StrUtil.format(condition, targetClass.replace());
            } else {
                // 子类类上边没有 @PreAuth 注解，证明该方法不需要简要权限
                return true;
            }
        }

        Expression expression = SPEL_PARSER.parseExpression(condition);
        // 方法参数值
        Object[] args = point.getArgs();

        StandardEvaluationContext context = new StandardEvaluationContext(authFun);
        context.setBeanResolver(new BeanFactoryResolver(ac));
        for (int i = 0; i < args.length; i++) {
            MethodParameter mp = getMethodParameter(method, i);
            context.setVariable(mp.getParameterName(), args[i]);
        }

        if (expression.getValue(context, Boolean.class)) {
            return true;
        }
        throw BizException.wrap(ExceptionCode.UNAUTHORIZED.build("执行方法[%s]需要[%s]权限", method.getName(), condition));
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

}
