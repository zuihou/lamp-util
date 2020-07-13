package com.github.zuihou.injection.annonation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在某个对象的字段上标记该注解，该字段的值将被主动注入
 * <p>
 * 如：
 *
 * @author zuihou
 * @InjectionField(api = "dictionaryItemServiceImpl", method = "findDictionaryItem")
 * private String nation;
 * @InjectionField(api = "dictionaryItemServiceImpl", method = "findDictionaryItem")
 * private RemoteData<String,String>  nation;
 *
 * <p>
 * 强烈建议：不要对象之间互相依赖
 * 如： User 想要注入 File， File也想注入User
 * @create 2020年01月18日17:59:25
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
public @interface InjectionField {
    /**
     * 固定查询值
     *
     * @return
     */
    String key() default "";

    /**
     * 提供自动注入值的 查询类
     * <p/>
     * api()  和 feign() 任选其一,  使用 api时，请填写实现类， 使用feign时，填写接口即可
     * 如： @InjectionField(api="userServiceImpl") 等价于 @InjectionField(feign=UserService.class)
     * 如： @InjectionField(api="userApi") 等价于 @InjectionField(feign=UserApi.class)
     * <p>
     * 注意：若使用feignClient调用， 则一定要加上 @FeignClient(qualifier = "userApi"), 否则会注入失败
     *
     * @return
     */
    String api() default "";

    /**
     * 提供自动注入值的 查询类
     * <p/>
     * api()  和 feign() 任选其一,  使用 api时，请填写实现类， 使用feign时，填写接口即可
     * 如： @InjectionField(api="userServiceImpl") 等价于 @InjectionField(feign=UserService.class)
     * 如： @InjectionField(api="userApi") 等价于 @InjectionField(feign=UserApi.class)
     * <p>
     * 注意：若使用feignClient调用， 则一定要加上 @FeignClient(qualifier = "userApi"), 否则会注入失败
     *
     * @return
     */
    Class<? extends Object> apiClass() default Object.class;

    /**
     * 提供自动注入值的 查询方法
     * <p>
     * 若 找不到 api(feign) + method，则忽略该字段
     * <p>
     * 该方法的入参必须为 Set<Serializable> 类型
     * 该方法的出参必须为 Map<Serializable, Object> 类型
     *
     * @return
     */
    String method() default "findByIds";

    /**
     * 自动注入值的类型， 用于强制转换
     *
     * @return
     */
    Class<? extends Object> beanClass() default Object.class;

    /**
     * 自动注入值是字典时，需要指定该字典的 类型（c_common_dictionary_item 表的 dictionary_type 字段）
     *
     * @return
     */
    String dictType() default "";
}
