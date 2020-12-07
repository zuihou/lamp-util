package com.tangyh.basic.cloud;

import com.netflix.hystrix.HystrixCommand;
import com.tangyh.basic.cloud.feign.DateFormatRegister;
import com.tangyh.basic.cloud.hystrix.ThreadLocalHystrixConcurrencyStrategy;
import com.tangyh.basic.cloud.interceptor.FeignAddHeaderRequestInterceptor;
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
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@AutoConfigureAfter(EnableFeignClients.class)
public class OpenFeignAutoConfiguration {
    /**
     * 在feign调用方配置， 解决入参和出参是以下类型.
     * 1. @RequestParam("date") Date date
     * 2. @RequestParam("date") LocalDateTime date
     * 3. @RequestParam("date") LocalDate date
     * 4. @RequestParam("date") LocalTime date
     */
    @Bean
    public DateFormatRegister dateFormatRegister() {
        return new DateFormatRegister();
    }

    /**
     * feign 支持MultipartFile上传文件
     */
    @Bean
    public Encoder feignFormEncoder() {
        List<HttpMessageConverter<?>> converters = new RestTemplate().getMessageConverters();
        ObjectFactory<HttpMessageConverters> factory = () -> new HttpMessageConverters(converters);
        return new SpringFormEncoder(new SpringEncoder(factory));
    }


    /**
     * 本地线程 Hystrix并发策略
     */
    @Bean
    public ThreadLocalHystrixConcurrencyStrategy getThreadLocalHystrixConcurrencyStrategy() {
        return new ThreadLocalHystrixConcurrencyStrategy();
    }


    @Configuration("hystrixFeignConfiguration")
    @ConditionalOnClass({HystrixCommand.class, HystrixFeign.class})
    protected static class HystrixFeignConfiguration {

        /**
         * 覆盖了 org.springframework.cloud.openfeign.FeignClientsConfiguration 的配置
         */
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
         */
        @ConditionalOnMissingBean
        @Bean
        public FeignAddHeaderRequestInterceptor getClientTokenInterceptor() {
            return new FeignAddHeaderRequestInterceptor();
        }
    }

}
