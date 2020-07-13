package com.github.zuihou.swagger2;

import com.github.xiaoymin.knife4j.spring.filter.ProductionSecurityFilter;
import com.github.xiaoymin.knife4j.spring.filter.SecurityBasicAuthFilter;
import com.github.xiaoymin.knife4j.spring.model.MarkdownFiles;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger2 启动类. 参考： Knife4jAutoConfiguration
 * 启动条件：
 * 1，配置文件中： ${SwaggerProperties.PREFIX}.enabled=true
 * 2，配置文件中不存在： ${SwaggerProperties.PREFIX}.enabled 值
 *
 * @author zuihou
 * @date 2018/11/18 9:20
 */
@EnableSwagger2
@ComponentScan(
        basePackages = {
                "com.github.xiaoymin.knife4j.spring.plugin",
                "com.github.xiaoymin.knife4j.spring.web"
        }
)
@ConditionalOnProperty(prefix = SwaggerProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({BeanValidatorPluginsConfiguration.class})
public class Swagger2Configuration {

    @Bean
    @ConditionalOnClass(SwaggerWebMvcConfigurer.class)
    public SwaggerWebMvcConfigurer getSwaggerWebMvcConfigurer() {
        return new SwaggerWebMvcConfigurer();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = SwaggerProperties.PREFIX, name = "production", havingValue = "true")
    public ProductionSecurityFilter productionSecurityFilter(SwaggerProperties swaggerProperties) {
        return new ProductionSecurityFilter(swaggerProperties.getProduction());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = SwaggerProperties.PREFIX, name = "basic.enable", havingValue = "true")
    public SecurityBasicAuthFilter securityBasicAuthFilter(SwaggerProperties swaggerProperties) {
        SwaggerProperties.Basic basic = swaggerProperties.getBasic();
        return new SecurityBasicAuthFilter(basic.getEnable(), basic.getUsername(), basic.getPassword());
    }

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = SwaggerProperties.PREFIX, name = "markdown.enable", havingValue = "true")
    public MarkdownFiles markdownFiles(SwaggerProperties swaggerProperties) {
        return new MarkdownFiles(swaggerProperties.getMarkdown().getBasePath());
    }

}
