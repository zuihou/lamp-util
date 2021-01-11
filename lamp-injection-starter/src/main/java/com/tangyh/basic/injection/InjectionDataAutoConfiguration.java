package com.tangyh.basic.injection;

import com.tangyh.basic.injection.aspect.InjectionResultAspect;
import com.tangyh.basic.injection.core.InjectionCore;
import com.tangyh.basic.injection.mybatis.typehandler.RemoteDataTypeHandler;
import com.tangyh.basic.injection.properties.InjectionProperties;
import com.tangyh.basic.utils.SpringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 关联字段数据注入工具 自动配置类
 *
 * @author zuihou
 * @date 2019/09/20
 */
@Slf4j
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(InjectionProperties.class)
public class InjectionDataAutoConfiguration {
    private final InjectionProperties remoteProperties;

    /**
     * Spring 工具类
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = InjectionProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public SpringUtils beanFactoryUtils(ApplicationContext applicationContext) {
        SpringUtils instance = SpringUtils.getInstance();
        SpringUtils.setApplicationContext(applicationContext);
        return instance;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = InjectionProperties.PREFIX, name = "aop-enabled", havingValue = "true", matchIfMissing = true)
    public InjectionResultAspect getRemoteAspect(InjectionCore injectionCore) {
        return new InjectionResultAspect(injectionCore);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = InjectionProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public InjectionCore getInjectionCore() {
        return new InjectionCore(remoteProperties);
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

