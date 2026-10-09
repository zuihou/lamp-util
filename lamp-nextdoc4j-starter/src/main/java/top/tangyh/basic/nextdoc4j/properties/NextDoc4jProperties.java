package top.tangyh.basic.nextdoc4j.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import top.tangyh.basic.constant.Constants;

import static top.tangyh.basic.nextdoc4j.properties.NextDoc4jProperties.PREFIX;

/**
 * nextdoc4j 在线文档属性配置
 * 必须配置 prefix ，才能有提示
 *
 * @author zuihou
 * @date 2026/10/08
 */
@Data
@ConfigurationProperties(prefix = PREFIX)
public class NextDoc4jProperties {
    public static final String PREFIX = Constants.PROJECT_PREFIX + ".nextdoc4j";

    /**
     * 标题
     **/
    private String title = "在线文档";
    /**
     * 描述
     **/
    private String description = "lamp-cloud 在线文档";
    /**
     * 版本
     **/
    private String version = "1.0";
    /**
     * 许可证
     **/
    private String license = "";
    /**
     * 许可证URL
     **/
    private String licenseUrl = "";
    /**
     * 服务条款URL
     **/
    private String termsOfServiceUrl = "";

    private Contact contact = new Contact();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Contact {
        /**
         * 联系人
         **/
        private String name = "";
        /**
         * 联系人url
         **/
        private String url = "";
        /**
         * 联系人email
         **/
        private String email = "";
    }

}
