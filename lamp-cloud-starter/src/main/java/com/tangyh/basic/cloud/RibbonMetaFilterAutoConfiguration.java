package com.tangyh.basic.cloud;


import com.tangyh.basic.cloud.ribbon.GrayRule;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * 配置ribbon 相关
 *
 * @author zuihou
 * @date 2020年02月15日14:00:16
 */
@AutoConfigureBefore(RibbonClientConfiguration.class)
public class RibbonMetaFilterAutoConfiguration {

    /**
     * 灰度发布 规则
     */
    @Bean
    @ConditionalOnMissingBean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public GrayRule metadataAwareRule() {
        return new GrayRule();
    }
}
