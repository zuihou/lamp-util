package top.tangyh.basic.log;


import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import top.tangyh.basic.jackson.JsonUtil;
import top.tangyh.basic.log.aspect.SysLogAspect;
import top.tangyh.basic.log.event.SysLogListener;
import top.tangyh.basic.log.monitor.PointUtil;
import top.tangyh.basic.log.properties.OptLogProperties;

/**
 * 日志自动配置
 * <p>
 * 启动条件：
 * 1，存在web环境
 * 2，配置文件中lamp.log.enabled=true 或者 配置文件中不存在：lamp.log.enabled 值
 *
 * @author zuihou
 * @date 2019/2/1
 */
@EnableAsync
@Configuration
@AllArgsConstructor
@ConditionalOnWebApplication
@EnableConfigurationProperties(OptLogProperties.class)
@ConditionalOnProperty(prefix = OptLogProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class LogAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SysLogAspect sysLogAspect() {
        return new SysLogAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = OptLogProperties.PREFIX, name = "type", havingValue = "LOGGER", matchIfMissing = true)
    public SysLogListener sysLogListener() {
        return new SysLogListener(log -> PointUtil.debug("0", "OPT_LOG", JsonUtil.toJson(log)));
    }
}
