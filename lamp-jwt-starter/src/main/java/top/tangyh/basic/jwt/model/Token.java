package top.tangyh.basic.jwt.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zuihou
 * @date 2017-12-15 11:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token implements Serializable {
    private static final long serialVersionUID = -8482946147572784305L;
    /**
     * token
     */
    @ApiModelProperty(value = "token")
    private String token;
    /**
     * 有效时间：单位：秒
     */
    @ApiModelProperty(value = "有效期")
    private Long expire;


    @ApiModelProperty(value = "到期时间")
    private LocalDateTime expiration;

}
