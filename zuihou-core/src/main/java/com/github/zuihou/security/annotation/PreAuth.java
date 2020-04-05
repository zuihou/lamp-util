package com.github.zuihou.security.annotation;

import java.lang.annotation.*;

/**
 * 权限注解 用于检查权限 规定访问权限
 * <p>
 * 注解优先级：
 * 子类方法上的注解 > 子类类上的注解 > 父类方法上的注解
 * <p>
 * 特别地：必须在子类类上注解上写上 replace 参数用于替换父类上的{}占位符，否则权限验证始终无法通过
 * <p>
 * 如：
 * \@PreAuth(replace="user")
 * public class UserController extends SuperCacheController { }
 * <p>
 * 则：UserController 的所有CRUD方法均分别需要 user:add、user:update、user:delete、user:view 等权限
 *
 * @author zuihou
 * @date 2020年03月29日21:14:20
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PreAuth {

    /**
     * 是否启用 URI权限拦截
     *
     * @return
     */
    boolean enabled() default true;

    /**
     * Spring el
     * 文档地址：https://docs.spring.io/spring/docs/4.3.16.RELEASE/spring-framework-reference/htmlsingle/#expressions-operators-logical
     */
    String value() default "permit()";

    /**
     * 替换父类@PreAuth注解中value的占位符{}
     *
     * @return
     */
    String replace() default "";
}

