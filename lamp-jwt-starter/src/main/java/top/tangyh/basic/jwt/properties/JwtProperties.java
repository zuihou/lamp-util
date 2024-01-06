package top.tangyh.basic.jwt.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import top.tangyh.basic.constant.Constants;

import static top.tangyh.basic.jwt.properties.JwtProperties.PREFIX;

/**
 * 认证服务端 属性
 *
 * @author zuihou
 * @date 2018/11/20
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = PREFIX)
public class JwtProperties {
    public static final String PREFIX = Constants.PROJECT_PREFIX + ".authentication";

    /**
     * 过期时间 2h
     * 单位：s
     * <p>
     * 在 lamp-oauth-server.yml 配置即可
     */
    private Long expire = 7200L;
    /**
     * 刷新token的过期时间 8h
     * 单位：s
     */
    private Long refreshExpire = 28800L;
    /**
     * 设置解析token时，允许的误差
     * 单位：s
     * 使用场景1：多台服务器集群部署时，服务器时间戳可能不一致
     * 使用场景2：？
     * <p>
     * 在 lamp-gateway-server.yml 配置即可
     */
    private Long allowedClockSkewSeconds = 60L;
    /**
     * jwt 签名，长度至少32位。
     * 建议每个公司都修改一下这个字符串！
     * 必须在 lamp-oauth-server.yml 和 lamp-gateway-server.yml 中同时配置，且配置值必须一致
     */
    private String jwtSignKey;
}
