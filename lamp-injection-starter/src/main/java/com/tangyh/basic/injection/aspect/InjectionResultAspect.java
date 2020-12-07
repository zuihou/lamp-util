package com.tangyh.basic.injection.aspect;

import com.tangyh.basic.annotation.injection.InjectionResult;
import com.tangyh.basic.injection.core.InjectionCore;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * InjectionResult 注解的 AOP 工具
 *
 * @author zuihou
 * @date 2020年01月19日09:27:41
 */
@Aspect
@AllArgsConstructor
@Slf4j
public class InjectionResultAspect {
    private final InjectionCore injectionCore;


    @Pointcut("@annotation(com.tangyh.basic.annotation.injection.InjectionResult)")
    public void methodPointcut() {
    }


    @Around("methodPointcut()&&@annotation(ir)")
    public Object interceptor(ProceedingJoinPoint pjp, InjectionResult ir) throws Throwable {
        Object proceed = pjp.proceed();
        injectionCore.injection(proceed, ir.isUseCache());
        return proceed;
    }
}
