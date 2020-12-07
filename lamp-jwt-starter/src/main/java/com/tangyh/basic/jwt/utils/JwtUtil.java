package com.tangyh.basic.jwt.utils;

import cn.hutool.core.util.StrUtil;
import com.tangyh.basic.context.ContextConstants;
import com.tangyh.basic.exception.BizException;
import com.tangyh.basic.exception.code.ExceptionCode;
import com.tangyh.basic.jwt.model.Token;
import com.tangyh.basic.utils.DateUtils;
import com.tangyh.basic.utils.StrPool;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import static com.tangyh.basic.context.ContextConstants.BASIC_HEADER_PREFIX;
import static com.tangyh.basic.exception.code.ExceptionCode.JWT_BASIC_INVALID;
import static com.tangyh.basic.exception.code.ExceptionCode.JWT_PARSER_TOKEN_FAIL;

/**
 * Secure工具类
 *
 * @author zuihou
 */
@Slf4j
public final class JwtUtil {
    private JwtUtil() {
    }

    /**
     * 将 签名（JWT_SIGN_KEY） 编译成BASE64编码
     */
    private static final String BASE64_SECURITY = Base64.getEncoder().encodeToString(ContextConstants.JWT_SIGN_KEY.getBytes(StandardCharsets.UTF_8));


    /**
     * authorization: Basic clientId:clientSec
     * 解析请求头中存储的 client 信息
     * <p>
     * Basic clientId:clientSec -截取-> clientId:clientSec后调用 extractClient 解码
     *
     * @param basicHeader Basic clientId:clientSec
     * @return clientId:clientSec
     */
    public static String[] getClient(String basicHeader) {
        if (StrUtil.isEmpty(basicHeader) || !basicHeader.startsWith(BASIC_HEADER_PREFIX)) {
            throw BizException.wrap(JWT_BASIC_INVALID);
        }

        String decodeBasic = StrUtil.subAfter(basicHeader, BASIC_HEADER_PREFIX, false);
        return extractClient(decodeBasic);
    }

    /**
     * 解析请求头中存储的 client 信息
     * clientId:clientSec 解码
     */
    public static String[] extractClient(String client) {
        String token = base64Decoder(client);
        int index = token.indexOf(StrPool.COLON);
        if (index == -1) {
            throw BizException.wrap(JWT_BASIC_INVALID);
        } else {
            return new String[]{token.substring(0, index), token.substring(index + 1)};
        }
    }

    /**
     * 使用 Base64 解码
     *
     * @param val 参数
     * @return 解码后的值
     */
    @SneakyThrows
    public static String base64Decoder(String val) {
        byte[] decoded = Base64.getDecoder().decode(val.getBytes(StandardCharsets.UTF_8));
        return new String(decoded, StandardCharsets.UTF_8);
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
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JsonWebToken")
                .signWith(signingKey, signatureAlgorithm);

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
                .setExpiration(exp);

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
            log.error("token 过期", ex);
            //过期
            throw new BizException(ExceptionCode.JWT_TOKEN_EXPIRED.getCode(), ExceptionCode.JWT_TOKEN_EXPIRED.getMsg(), ex);
        } catch (SignatureException ex) {
            log.error("token 签名错误", ex);
            //签名错误
            throw new BizException(ExceptionCode.JWT_SIGNATURE.getCode(), ExceptionCode.JWT_SIGNATURE.getMsg(), ex);
        } catch (IllegalArgumentException ex) {
            log.error("token 为空", ex);
            //token 为空
            throw new BizException(ExceptionCode.JWT_ILLEGAL_ARGUMENT.getCode(), ExceptionCode.JWT_ILLEGAL_ARGUMENT.getMsg(), ex);
        } catch (Exception e) {
            log.error("errCode:{}, message:{}", JWT_PARSER_TOKEN_FAIL.getCode(), e.getMessage(), e);
            throw new BizException(JWT_PARSER_TOKEN_FAIL.getCode(), JWT_PARSER_TOKEN_FAIL.getMsg(), e);
        }
    }

    public static String getToken(String token) {
        if (token == null) {
            throw BizException.wrap(JWT_PARSER_TOKEN_FAIL);
        }
        if (token.startsWith(ContextConstants.BEARER_HEADER_PREFIX)) {
            return StrUtil.subAfter(token, ContextConstants.BEARER_HEADER_PREFIX, false);
        }
        throw BizException.wrap(JWT_PARSER_TOKEN_FAIL);
    }

    /**
     * 获取Claims
     *
     * @param token                   待解析token
     * @param allowedClockSkewSeconds 允许存在的时间差
     */
    public static Claims getClaims(String token, long allowedClockSkewSeconds) {
        if (token == null) {
            throw BizException.wrap(JWT_PARSER_TOKEN_FAIL);
        }
        if (token.startsWith(ContextConstants.BEARER_HEADER_PREFIX)) {
            String headStr = StrUtil.subAfter(token, ContextConstants.BEARER_HEADER_PREFIX, false);
            return parseJwt(headStr, allowedClockSkewSeconds);
        }
        throw BizException.wrap(JWT_PARSER_TOKEN_FAIL);
    }

}
