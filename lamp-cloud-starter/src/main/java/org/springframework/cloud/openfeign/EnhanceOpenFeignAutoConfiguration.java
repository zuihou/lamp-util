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

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 增强了 org.springframework.cloud.openfeign.FeignClientsConfiguration 配置类
 *
 * @author Dave Syer
 * @author Venil Noronha
 * @author Darren Foong
 * @author zuihou
 * @date 2020/8/9 上午10:58
 */
public class EnhanceOpenFeignAutoConfiguration {

    /**
     * 扩展spring-cloud-openfeign-core 包的 ：HystrixTargeter 类
     * <p>
     * 为@FeignClient注解中没有添加fallback和fallbackFactory属性的API， 添加默认的 MyFallbackFactory
     * <p>
     * 覆盖org.springframework.cloud.openfeign.FeignAutoConfiguration 中的Targeter
     */
    @Bean
    @ConditionalOnClass(name = "feign.hystrix.HystrixFeign")
    @ConditionalOnMissingBean(Targeter.class)
    public Targeter feignTargeter() {
        return new GlobalHystrixTargeter();
    }

}
