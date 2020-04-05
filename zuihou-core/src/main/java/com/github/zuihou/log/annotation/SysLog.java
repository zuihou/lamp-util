package com.github.zuihou.log.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author zuihou
 * @date 2019/2/1
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {
    /**
     * 是否启用 操作日志
     *
     * @return
     */
    boolean enabled() default true;

    /**
     * 描述
     *
     * @return {String}
     */
    String value() default "";

    /**
     * 记录执行参数
     *
     * @return
     */
    boolean request() default true;

    /**
     * 当 request = false时， 需要方法报错是否记录请求参数
     *
     * @return
     */
    boolean requestByError() default true;

    /**
     * 记录返回参数
     *
     * @return
     */
    boolean response() default true;
}
