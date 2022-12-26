package top.tangyh.basic.jwt.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * AuthInfo
 *
 * @author zuihou
 * @date 2020年03月31日21:43:31
 */
@Data
@Accessors(chain = true)
@Schema(description = "认证信息")
public class AuthInfo {
    @Schema(description = "令牌")
    private String token;
    @Schema(description = "令牌类型")
    private String tokenType;
    @Schema(description = "刷新令牌")
    private String refreshToken;
    @Schema(description = "用户名")
    private String name;
    @Schema(description = "账号名")
    private String account;
    @Schema(description = "头像")
    private Long avatarId;
    @Schema(description = "工作描述")
    private String workDescribe;
    @Schema(description = "用户id")
    private Long userId;
    @Schema(description = "过期时间（秒）")
    private long expire;
    @Schema(description = "到期时间")
    private LocalDateTime expiration;
    @Schema(description = "有效期")
    private Long expireMillis;
}
