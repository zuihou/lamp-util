package top.tangyh.basic.echo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.tangyh.basic.echo.aspect.EchoResultAspect;
import top.tangyh.basic.echo.core.EchoService;
import top.tangyh.basic.echo.properties.EchoProperties;
import top.tangyh.basic.echo.typehandler.RemoteDataTypeHandler;
import top.tangyh.basic.model.LoadService;

import java.util.Map;

/**
 * 关联字段数据注入工具 自动配置类
 *
 * @author zuihou
 * @date 2019/09/20
 */
@Slf4j
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(EchoProperties.class)
public class EchoAutoConfiguration {
    private final EchoProperties remoteProperties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = EchoProperties.PREFIX, name = "aop-enabled", havingValue = "true", matchIfMissing = true)
    public EchoResultAspect getEchoResultAspect(EchoService echoService) {
        return new EchoResultAspect(echoService);
    }

    /**
     * 回显服务
     *
     * @param strategyMap 回显查询实例
     * @return top.tangyh.basic.echo.core.EchoService
     * @author tangyh
     * @date 2021/8/15 10:17 下午
     * @create [2021/8/15 10:17 下午 ] [tangyh] [初始创建]
     */
    @Bean
    @ConditionalOnMissingBean
    public EchoService getEchoService(Map<String, LoadService> strategyMap) {
        return new EchoService(remoteProperties, strategyMap);
    }

    /**
     * Mybatis 类型处理器： 处理 RemoteData 类型的字段
     */
    @Bean
    @ConditionalOnMissingBean
    public RemoteDataTypeHandler getRemoteDataTypeHandler() {
        return new RemoteDataTypeHandler();
    }
}

