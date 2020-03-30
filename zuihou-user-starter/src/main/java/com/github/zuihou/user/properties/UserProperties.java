package com.github.zuihou.user.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 属性
 *
 * @author zuihou
 * @date 2020年02月24日10:48:35
 */
@Data
@ConfigurationProperties(prefix = "zuihou.user")
public class UserProperties {
    private UserType type = UserType.FEIGN;
    private Boolean enabled = true;
}
