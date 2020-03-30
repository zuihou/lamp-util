package com.github.zuihou.boot.properties;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 无需 token 的url
 *
 * @author zuihou
 */
@Data
@ConfigurationProperties("zuihou.context")
public class ContextProperties {

    /**
     * 拦截器排除的路径
     */
    private List<String> excludePatterns = CollUtil.newArrayList(
            "/error", "/login", "/v2/api-docs", "/v2/api-docs-ext", "/swagger-resources/**"
            , "/webjars/**", "/swagger-ui.html**", "/doc.html**", "/csrf"
    );

    private String pathPatterns = "/**";

    private int order = -10;
}
