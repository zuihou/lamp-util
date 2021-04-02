package com.tangyh.basic.echo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置类
 *
 * @author zuihou
 * @date 2020年01月19日09:11:19
 */
@Data
@ConfigurationProperties(EchoProperties.PREFIX)
public class EchoProperties {
    public static final String PREFIX = "lamp.echo";
    /**
     * 是否启用远程查询
     */
    private Boolean enabled = true;
    /**
     * 是否启用aop注解方式
     */
    private Boolean aopEnabled = true;

    /**
     * 字典类型 和 code 的分隔符
     */
    private String dictSeparator = "###";
    /**
     * 多个字典code 之间的的分隔符
     */
    private String dictItemSeparator = ",";

    /**
     * 递归最大深度
     */
    private Integer maxDepth = 3;

}
