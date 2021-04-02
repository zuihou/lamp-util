/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.openfeign;

import feign.Feign.Builder;
import feign.Target;
import feign.hystrix.FallbackFactory;
import feign.hystrix.HystrixFeign;
import feign.hystrix.SetterFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.util.StringUtils;

/**
 * 扩展spring-cloud-openfeign-core 包的 ：HystrixTargeter 类
 * <p>
 * 为@FeignClient注解中没有添加fallback和fallbackFactory属性的API， 添加默认的 MyFallbackFactory
 *
 * @author Spencer Gibb
 * @author Erik Kringen
 * @author zuihou
 */
@SuppressWarnings("unchecked")
public class GlobalHystrixTargeter implements Targeter {

    @Override
    public <T> T target(FeignClientFactoryBean factory, Builder feign, FeignContext context, Target.HardCodedTarget<T> target) {
        if (!(feign instanceof feign.hystrix.HystrixFeign.Builder)) {
            return feign.target(target);
        }
        feign.hystrix.HystrixFeign.Builder builder = (feign.hystrix.HystrixFeign.Builder) feign;
        String name = StringUtils.isEmpty(factory.getContextId()) ? factory.getName()
                : factory.getContextId();
        SetterFactory setterFactory = getOptional(name, context, SetterFactory.class);
        if (setterFactory != null) {
            builder.setterFactory(setterFactory);
        }
        Class<?> fallback = factory.getFallback();
        if (fallback != void.class) {
            return targetWithFallback(name, context, target, builder, fallback);
        }
        Class<?> fallbackFactory = factory.getFallbackFactory();
        if (fallbackFactory != void.class) {
            return targetWithFallbackFactory(name, context, target, builder,
                    fallbackFactory);
        }
        // 上面的代码引用至org.springframework.cloud.openfeign.HystrixTargeter，以下代码是对它的增强，通过代理方式，给未设置fallback或fallbackFactory的类增加了全局的 fallbackFactory
        FallbackFactory myFallbackFactory = cause -> {
            final Class<T> targetType = target.type();
            final String targetName = target.name();
            Enhancer e = new Enhancer();
            e.setUseCache(true);
            e.setCallback(new GlobalFallbackFactoryProxy<>(targetType, targetName, cause));
            e.setSuperclass(targetType);
            // 为targetType类动态生成 fallbackFactory 实现类
            return (T) e.create();
        };
        return (T) builder.target(target, myFallbackFactory);
    }

    private <T> T targetWithFallbackFactory(String feignClientName, FeignContext context,
                                            Target.HardCodedTarget<T> target, HystrixFeign.Builder builder,
                                            Class<?> fallbackFactoryClass) {
        FallbackFactory<? extends T> fallbackFactory = (FallbackFactory<? extends T>) getFromContext(
                "fallbackFactory", feignClientName, context, fallbackFactoryClass,
                FallbackFactory.class);
        return builder.target(target, fallbackFactory);
    }

    private <T> T targetWithFallback(String feignClientName, FeignContext context,
                                     Target.HardCodedTarget<T> target, HystrixFeign.Builder builder,
                                     Class<?> fallback) {
        T fallbackInstance = getFromContext("fallback", feignClientName, context,
                fallback, target.type());
        return builder.target(target, fallbackInstance);
    }

    private <T> T getFromContext(String fallbackMechanism, String feignClientName,
                                 FeignContext context, Class<?> beanType, Class<T> targetType) {
        Object fallbackInstance = context.getInstance(feignClientName, beanType);
        if (fallbackInstance == null) {
            throw new IllegalStateException(String.format(
                    "No " + fallbackMechanism
                            + " instance of type %s found for feign client %s",
                    beanType, feignClientName));
        }

        if (!targetType.isAssignableFrom(beanType)) {
            throw new IllegalStateException(String.format("Incompatible "
                            + fallbackMechanism
                            + " instance. Fallback/fallbackFactory of type %s is not assignable to %s for feign client %s",
                    beanType, targetType, feignClientName));
        }
        return (T) fallbackInstance;
    }

    private <T> T getOptional(String feignClientName, FeignContext context, Class<T> beanType) {
        return context.getInstance(feignClientName, beanType);
    }
}
