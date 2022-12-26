package top.tangyh.basic.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.tangyh.basic.mq.properties.MqProperties;

/**
 * rabbit 禁用配置
 * <p>
 * 若自建服务的 包名 跟当前类的包名不同，请在服务的启动类上配置下列注解，否则启动报错
 * \@ComponentScan({
 * "xxx",  // xxx 改成自建服务的 包名
 * "top.tangyh.basic.mq"
 * })
 *
 * @author zuihou
 * @date 2019/09/20
 */
@Configuration
@Import(LampRabbitMqConfiguration.RabbitMqConfiguration.class)
public class LampRabbitMqConfiguration {
    @Slf4j
    @Configuration
    @ConditionalOnProperty(prefix = MqProperties.PREFIX, name = "enabled", havingValue = "false", matchIfMissing = true)
    @EnableAutoConfiguration(exclude = {RabbitAutoConfiguration.class})
    public static class RabbitMqConfiguration {
        public RabbitMqConfiguration() {
            log.warn("检测到lamp.rabbitmq.enabled=false，排除了 RabbitMQ");
        }
    }

}
