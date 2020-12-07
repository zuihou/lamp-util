package com.tangyh.basic.annotation.injection;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 将该注解标记在service方法上， 调用该方法后，返回值中标记了@InjectionField 注解的字段将会自动注入属性
 * <p>
 * 注意，该方法不能写在 Mapper 的方法上。
 *
 * @author zuihou
 * @date 2020年01月19日09:08:40
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface InjectionResult {
    /**
     * 是否使用本地缓存
     * <p>
     * 会有脏数据的可能，对数据敏感的系统请设置成false（或者全局设置成false），然后自行实现数据的缓存和淘汰。
     *
     * @return 是否使用内存缓存
     */
    boolean isUseCache() default true;
}
