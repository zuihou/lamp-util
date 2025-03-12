package top.tangyh.basic.swagger2;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import top.tangyh.basic.swagger2.properties.SwaggerProperties;

import java.util.List;

/**
 * swagger 包扫描配置
 *
 * @author zuihou
 * @date 2018/11/18 9:22
 */
@Import({
        Swagger2Configuration.class
})
@ConditionalOnProperty(prefix = "knife4j", name = "enable", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration {
    private final SwaggerProperties swaggerProperties;

    public SwaggerAutoConfiguration(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    /**
     * 根据@Tag 上的排序，写入x-order
     *
     * @return the global open api customizer
     */
    @Bean
    public GlobalOpenApiCustomizer orderGlobalOpenApiCustomizer() {
        return openApi -> {
//            if (openApi.getTags() != null) {
//                openApi.getTags().forEach(tag -> {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("x-order", RandomUtil.randomInt(0, 100));
//                    tag.setExtensions(map);
//                });
//            }
//            if (openApi.getPaths() != null) {
//                openApi.addExtension("x-test123", "333");
//                openApi.getPaths().addExtension("x-abb", RandomUtil.randomInt(1, 100));
//            }
        };

    }


    @Bean
    public GlobalOperationCustomizer customize2() {
        return (operation, handlerMethod) -> {
            List<SwaggerProperties.GlobalOperationParameter> globalOperationParameters = swaggerProperties.getGlobalOperationParameters();

            if (CollUtil.isEmpty(globalOperationParameters)) {
                return operation;
            }
            for (SwaggerProperties.GlobalOperationParameter globalOperationParameter : globalOperationParameters) {
                operation = operation.addParametersItem(
                        new Parameter()
                                .in(globalOperationParameter.getParameterType())
                                .required(globalOperationParameter.getRequired())
                                .schema(new StringSchema())
                                .example(globalOperationParameter.getDefaultValue())
                                .description(globalOperationParameter.getDescription())
                                .allowEmptyValue(globalOperationParameter.getAllowEmptyValue())
                                .name(globalOperationParameter.getName())
                );
            }
            return operation;
        };
    }

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title(swaggerProperties.getTitle())
                        .description(swaggerProperties.getDescription())
                        .version(swaggerProperties.getVersion())
                        .termsOfService(swaggerProperties.getTermsOfServiceUrl())
                        .contact(new Contact().name(swaggerProperties.getContact().getName())
                                .url(swaggerProperties.getContact().getUrl())
                                .email(swaggerProperties.getContact().getEmail()))
                        .license(new License().name(swaggerProperties.getLicense()).url(swaggerProperties.getLicenseUrl())));
    }


}
