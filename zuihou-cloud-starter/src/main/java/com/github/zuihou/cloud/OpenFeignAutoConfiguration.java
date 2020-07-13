package com.github.zuihou.cloud;

import com.github.zuihou.cloud.feign.DateFormatRegister;
import com.github.zuihou.cloud.hystrix.ThreadLocalHystrixConcurrencyStrategy;
import com.github.zuihou.cloud.interceptor.FeignAddHeaderRequestInterceptor;
import com.netflix.hystrix.HystrixCommand;
import feign.Feign;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import feign.hystrix.HystrixFeign;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.MyFeignClientsRegistrar;
import org.springframework.cloud.openfeign.MyHystrixTargeter;
import org.springframework.cloud.openfeign.Targeter;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * OpenFeign 配置
 *
 * @author zuihou
 * @date 2019/07/25
 */
@ConditionalOnClass(Feign.class)
@Import(MyFeignClientsRegistrar.class)
@AutoConfigureAfter(EnableFeignClients.class)
public class OpenFeignAutoConfiguration {
    /**
     * 在feign调用方配置， 解决入参和出参是以下类型.
     * 1. @RequestParam("date") Date date
     * 2. @RequestParam("date") LocalDateTime date
     * 3. @RequestParam("date") LocalDate date
     * 4. @RequestParam("date") LocalTime date
     *
     * @return
     */
    @Bean
    public DateFormatRegister dateFormatRegister() {
        return new DateFormatRegister();
    }

    /**
     * feign 支持MultipartFile上传文件
     *
     * @return
     */
    @Bean
    public Encoder feignFormEncoder() {
        List<HttpMessageConverter<?>> converters = new RestTemplate().getMessageConverters();
        ObjectFactory<HttpMessageConverters> factory = () -> new HttpMessageConverters(converters);
        return new SpringFormEncoder(new SpringEncoder(factory));
    }


    /**
     * 本地线程 Hystrix并发策略
     *
     * @return
     */
    @Bean
    public ThreadLocalHystrixConcurrencyStrategy getThreadLocalHystrixConcurrencyStrategy() {
        return new ThreadLocalHystrixConcurrencyStrategy();
    }


    /**
     * 扩展spring-cloud-openfeign-core 包的 ：HystrixTargeter 类
     * <p>
     * 为@FeignClient注解中没有添加fallback和fallbackFactory属性的API， 添加默认的 MyFallbackFactory
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(Targeter.class)
    public Targeter myFeignTargeter() {
        return new MyHystrixTargeter();
    }


    @Configuration("hystrixFeignConfiguration")
    @ConditionalOnClass({HystrixCommand.class, HystrixFeign.class})
    protected static class HystrixFeignConfiguration {
        @Bean
        @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        @ConditionalOnProperty("feign.hystrix.enabled")
        public Feign.Builder feignHystrixBuilder(RequestInterceptor requestInterceptor) {
            return HystrixFeign.builder()
                    .decode404()
                    .requestInterceptor(requestInterceptor);
        }

        /**
         * feign client 请求头传播
         *
         * @return
         */
        @Bean
        public FeignAddHeaderRequestInterceptor getClientTokenInterceptor() {
            return new FeignAddHeaderRequestInterceptor();
        }
    }

}
