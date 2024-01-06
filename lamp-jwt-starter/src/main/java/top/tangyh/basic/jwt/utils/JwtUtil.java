package top.tangyh.basic.jwt.utils;

import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import top.tangyh.basic.exception.UnauthorizedException;
import top.tangyh.basic.exception.code.ExceptionCode;
import top.tangyh.basic.jwt.model.Token;
import top.tangyh.basic.utils.DateUtils;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import static top.tangyh.basic.exception.code.ExceptionCode.JWT_PARSER_TOKEN_FAIL;

/**
 * Secure工具类
 *
 * @author zuihou
 */
@Slf4j
public final class JwtUtil {
    /**
     * 将 签名（JWT_SIGN_KEY） 编译成BASE64编码
     */
    public static String BASE64_SECURITY;


    private JwtUtil() {
    }


    /**
     * 创建令牌
     *
     * @param user   user
     * @param expire 过期时间（秒)
     * @return jwt
     */
    public static Token createJwt(Map<String, String> user, long expire) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //生成签名密钥
        byte[] apiKeySecretBytes = Base64.getDecoder().decode(BASE64_SECURITY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //添加构成JWT的类
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JsonWebToken");

        //设置JWT参数
        user.forEach(builder::claim);

        //添加Token过期时间
        //allowedClockSkewMillis
        long expMillis = nowMillis + expire * 1000;
        Date exp = new Date(expMillis);
        builder
                // 发布时间
                .setIssuedAt(now)
                // token从时间什么开始生效
                .setNotBefore(now)
                // token从什么时间截止生效
                .setExpiration(exp)
                // 签名
                .signWith(signingKey, signatureAlgorithm);

        // 组装Token信息
        Token tokenInfo = new Token();
        tokenInfo.setToken(builder.compact());
        tokenInfo.setExpire(expire);
        tokenInfo.setExpiration(DateUtils.date2LocalDateTime(exp));
        return tokenInfo;
    }

    /**
     * 解析jwt
     *
     * @param jsonWebToken            待解析token
     * @param allowedClockSkewSeconds 允许的时间差
     * @return Claims
     */
    public static Claims parseJwt(String jsonWebToken, long allowedClockSkewSeconds) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Base64.getDecoder().decode(BASE64_SECURITY))
                    .setAllowedClockSkewSeconds(allowedClockSkewSeconds)
                    .build()
                    .parseClaimsJws(jsonWebToken)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            log.error("token=[{}], 过期", jsonWebToken, ex);
            //过期
            throw new UnauthorizedException(ExceptionCode.JWT_TOKEN_EXPIRED.getCode(), ExceptionCode.JWT_TOKEN_EXPIRED.getMsg(), ex);
        } catch (SignatureException ex) {
            log.error("token=[{}] 签名错误", jsonWebToken, ex);
            //签名错误
            throw new UnauthorizedException(ExceptionCode.JWT_SIGNATURE.getCode(), ExceptionCode.JWT_SIGNATURE.getMsg(), ex);
        } catch (IllegalArgumentException ex) {
            log.error("token=[{}] 为空", jsonWebToken, ex);
            //token 为空
            throw new UnauthorizedException(ExceptionCode.JWT_ILLEGAL_ARGUMENT.getCode(), ExceptionCode.JWT_ILLEGAL_ARGUMENT.getMsg(), ex);
        } catch (Exception e) {
            log.error("token=[{}] errCode:{}, message:{}", jsonWebToken, JWT_PARSER_TOKEN_FAIL.getCode(), e.getMessage(), e);
            throw new UnauthorizedException(JWT_PARSER_TOKEN_FAIL.getCode(), JWT_PARSER_TOKEN_FAIL.getMsg(), e);
        }
    }


    /**
     * 获取Claims
     *
     * @param token                   待解析token
     * @param allowedClockSkewSeconds 允许存在的时间差
     */
    public static Claims getClaims(String token, long allowedClockSkewSeconds) {
        if (StrUtil.isEmpty(token)) {
            throw UnauthorizedException.wrap(JWT_PARSER_TOKEN_FAIL.getMsg());
        }

        return parseJwt(token, allowedClockSkewSeconds);
    }

}
