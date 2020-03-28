package com.github.zuihou.security.annotation;

import java.lang.annotation.*;

/**
 * 服务调用不鉴权注解
 * <p>
 *
 * @author zuihou
 * @date 2020年03月25日17:51:40
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inner {

    /**
     * 是否AOP统一处理
     *
     * @return false, true
     */
    boolean value() default true;

    /**
     * 需要特殊判空的字段(预留)
     *
     * @return {}
     */
    String[] field() default {};
}
