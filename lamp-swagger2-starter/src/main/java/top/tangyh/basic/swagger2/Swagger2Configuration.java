package top.tangyh.basic.swagger2;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import static top.tangyh.basic.swagger2.Swagger2Configuration.TRUE;

/**
 * Swagger2 启动类. 参考： Knife4jAutoConfiguration
 * 启动条件：
 * 1，配置文件中： ${SwaggerProperties.PREFIX}.enable=true
 * 2，配置文件中不存在： ${SwaggerProperties.PREFIX}.enable 值
 *
 * @author zuihou
 * @date 2018/11/18 9:20
 */
@EnableSwagger2WebMvc
@ConditionalOnProperty(prefix = "knife4j", name = "enable", havingValue = TRUE, matchIfMissing = true)
@Import(BeanValidatorPluginsConfiguration.class)
public class Swagger2Configuration {

    public static final String TRUE = "true";

    @Bean
    @ConditionalOnClass(SwaggerWebMvcConfigurer.class)
    public SwaggerWebMvcConfigurer getSwaggerWebMvcConfigurer() {
        return new SwaggerWebMvcConfigurer();
    }



    /**
     * 升级springboot2.6.6后临时处理，防止swagger报错
     *
     * @author tangyh
     * @date 2022/4/9 7:18 PM
     * @create [2022/4/9 7:18 PM ] [tangyh] [初始创建]
     */
    @Bean
    public BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof WebMvcRequestHandlerProvider) {
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
                }
                return bean;
            }

            private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
                List<T> copy = mappings.stream()
                        .filter(mapping -> mapping.getPatternParser() == null)
                        .collect(Collectors.toList());
                mappings.clear();
                mappings.addAll(copy);
            }

            @SuppressWarnings("unchecked")
            private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
                try {
                    Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                    field.setAccessible(true);
                    return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }
}
