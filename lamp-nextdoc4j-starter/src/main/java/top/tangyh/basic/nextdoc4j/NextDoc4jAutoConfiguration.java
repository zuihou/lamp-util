package top.tangyh.basic.nextdoc4j;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import top.tangyh.basic.nextdoc4j.properties.NextDoc4jProperties;

/**
 * NextDoc4j 在线文档自动配置
 *
 * @author zuihou
 * @date 2026/10/08
 */
@ConditionalOnProperty(prefix = "nextdoc4j", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(NextDoc4jProperties.class)
public class NextDoc4jAutoConfiguration {
    private final NextDoc4jProperties nextDoc4jProperties;

    public NextDoc4jAutoConfiguration(NextDoc4jProperties nextDoc4jProperties) {
        this.nextDoc4jProperties = nextDoc4jProperties;
    }

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title(nextDoc4jProperties.getTitle())
                        .description(nextDoc4jProperties.getDescription())
                        .version(nextDoc4jProperties.getVersion())
                        .termsOfService(nextDoc4jProperties.getTermsOfServiceUrl())
                        .contact(new Contact().name(nextDoc4jProperties.getContact().getName())
                                .url(nextDoc4jProperties.getContact().getUrl())
                                .email(nextDoc4jProperties.getContact().getEmail()))
                        .license(new License().name(nextDoc4jProperties.getLicense()).url(nextDoc4jProperties.getLicenseUrl())));
    }

}
