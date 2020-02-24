package com.github.zuihou.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * zipkin 客服端自定义配置
 * <p>
 * 若自建服务的 包名 跟当前类的包名不同，请在服务的启动类上配置下列注解，否则启动报错
 *
 * @author zuihou
 * @date 2019/09/20
 */
@Configuration
@Import(ZuihouRabbitMqConfiguration.RabbitMqConfiguration.class)
public class ZuihouRabbitMqConfiguration {
    @Slf4j
    @Configuration
    @ConditionalOnProperty(name = {
            "zuihou.rabbitmq.enabled"
    }, havingValue = "false", matchIfMissing = true)
    @EnableAutoConfiguration(exclude = {RabbitAutoConfiguration.class})
    public static class RabbitMqConfiguration {

        @Bean("zxcvbnm")
        public TestB getB() {
            log.warn("检测到zuihou.rabbitmq.enabled=false，排除了 RabbitMQ");
            return new TestB();
        }
    }

}
