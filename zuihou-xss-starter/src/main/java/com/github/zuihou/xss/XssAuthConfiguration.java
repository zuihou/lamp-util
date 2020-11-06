package com.github.zuihou.xss;

import cn.hutool.core.collection.CollUtil;
import com.github.zuihou.xss.converter.XssStringJsonDeserializer;
import com.github.zuihou.xss.filter.XssFilter;
import com.github.zuihou.xss.properties.XssProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

import static com.github.zuihou.xss.filter.XssFilter.IGNORE_PARAM_VALUE;
import static com.github.zuihou.xss.filter.XssFilter.IGNORE_PATH;

/**
 * XSS 跨站攻击自动配置
 *
 * @author zuihou
 * @date 2019/07/25
 */
@AllArgsConstructor
@EnableConfigurationProperties({XssProperties.class})
public class XssAuthConfiguration {
    private final XssProperties xssProperties;

    /**
     * 配置跨站攻击 反序列化处理器
     *
     * @return
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer2() {
        return builder -> builder.deserializerByType(String.class, new XssStringJsonDeserializer());
    }


    /**
     * 配置跨站攻击过滤器
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean(new XssFilter());
        filterRegistration.setEnabled(xssProperties.getEnabled());
        filterRegistration.addUrlPatterns(xssProperties.getPatterns().stream().toArray(String[]::new));
        filterRegistration.setOrder(xssProperties.getOrder());

        Map<String, String> initParameters = new HashMap<>(2);
        initParameters.put(IGNORE_PATH, CollUtil.join(xssProperties.getIgnorePaths(), ","));
        initParameters.put(IGNORE_PARAM_VALUE, CollUtil.join(xssProperties.getIgnoreParamValues(), ","));
        filterRegistration.setInitParameters(initParameters);
        return filterRegistration;
    }
}
