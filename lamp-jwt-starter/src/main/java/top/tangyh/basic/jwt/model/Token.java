package top.tangyh.basic.jwt.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * token 工具类返回值
 *
 * @author zuihou
 * @date 2017-12-15 11:22
 */
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class Token extends JwtInfo implements Serializable {
    private static final long serialVersionUID = -8482946147572784305L;
    /**
     * token
     */
    @Schema(description = "token")
    private String token;
    /**
     * 有效时间：单位：秒
     */
    @Schema(description = "有效期")
    private Long expire;


    @Schema(description = "到期时间")
    private LocalDateTime expiration;

    public Token(String token, Long expire, LocalDateTime expiration) {
        this.token = token;
        this.expire = expire;
        this.expiration = expiration;
    }

}
