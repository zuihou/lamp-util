package com.github.zuihou.jwt.utils;

import cn.hutool.core.util.StrUtil;
import com.github.zuihou.context.BaseContextConstants;
import com.github.zuihou.exception.BizException;
import com.github.zuihou.exception.code.ExceptionCode;
import com.github.zuihou.jwt.model.Token;
import com.github.zuihou.utils.Charsets;
import com.github.zuihou.utils.StrPool;
import io.jsonwebtoken.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import static com.github.zuihou.context.BaseContextConstants.BASIC_HEADER_PREFIX;
import static com.github.zuihou.exception.code.ExceptionCode.JWT_BASIC_INVALID;
import static com.github.zuihou.exception.code.ExceptionCode.JWT_PARSER_TOKEN_FAIL;

/**
 * Secure工具类
 *
 * @author zuihou
 */
@Slf4j
public class JwtUtil {

    /**
     * 将 签名（JWT_SIGN_KEY） 编译成BASE64编码
     */
    private static String BASE64_SECURITY = Base64.getEncoder().encodeToString(BaseContextConstants.JWT_SIGN_KEY.getBytes(Charsets.UTF_8));


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
     * @param header
     * @return
     */
    @SneakyThrows
    public static String base64Decoder(String header) {
        byte[] decoded = Base64.getDecoder().decode(header.getBytes(Charsets.UTF_8_NAME));
        return new String(decoded, Charsets.UTF_8_NAME);
    }

    /**
     * 创建令牌
     *
     * @param user   user
     * @param expire 过期时间（秒)
     * @return jwt
     */
    public static Token createJWT(Map<String, String> user, long expire) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //生成签名密钥
        byte[] apiKeySecretBytes = Base64.getDecoder().decode(BASE64_SECURITY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //添加构成JWT的类
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JsonWebToken")
                .signWith(signatureAlgorithm, signingKey);

        //设置JWT参数
        user.forEach(builder::claim);

        //添加Token过期时间
        long expMillis = nowMillis + expire * 1000;
        Date exp = new Date(expMillis);
        builder.setExpiration(exp).setNotBefore(now);

        // 组装Token信息
        Token tokenInfo = new Token();
        tokenInfo.setToken(builder.compact());
        tokenInfo.setExpire(expire);
        tokenInfo.setExpiration(exp);
        return tokenInfo;
    }

    /**
     * 解析jwt
     *
     * @param jsonWebToken jsonWebToken
     * @return zuihou
     */
    public static Claims parseJWT(String jsonWebToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(Base64.getDecoder().decode(BASE64_SECURITY))
                    .parseClaimsJws(jsonWebToken).getBody();
        } catch (ExpiredJwtException ex) {
            //过期
            throw new BizException(ExceptionCode.JWT_TOKEN_EXPIRED.getCode(), ExceptionCode.JWT_TOKEN_EXPIRED.getMsg());
        } catch (SignatureException ex) {
            //签名错误
            throw new BizException(ExceptionCode.JWT_SIGNATURE.getCode(), ExceptionCode.JWT_SIGNATURE.getMsg());
        } catch (IllegalArgumentException ex) {
            //token 为空
            throw new BizException(ExceptionCode.JWT_ILLEGAL_ARGUMENT.getCode(), ExceptionCode.JWT_ILLEGAL_ARGUMENT.getMsg());
        } catch (Exception e) {
            log.error("errcode:{}, message:{}", JWT_PARSER_TOKEN_FAIL.getCode(), e.getMessage());
            throw new BizException(JWT_PARSER_TOKEN_FAIL.getCode(), JWT_PARSER_TOKEN_FAIL.getMsg());
        }
    }

    public static String getToken(String token) {
        if (token == null) {
            throw BizException.wrap(JWT_PARSER_TOKEN_FAIL);
        }
        if (token.startsWith(BaseContextConstants.BEARER_HEADER_PREFIX)) {
            return StrUtil.subAfter(token, BaseContextConstants.BEARER_HEADER_PREFIX, false);
        }
        throw BizException.wrap(JWT_PARSER_TOKEN_FAIL);
    }

    /**
     * 获取Claims
     *
     * @param token
     * @return zuihou
     */
    public static Claims getClaims(String token) {
        if (token == null) {
            throw BizException.wrap(JWT_PARSER_TOKEN_FAIL);
        }
        if (token.startsWith(BaseContextConstants.BEARER_HEADER_PREFIX)) {
            String headStr = StrUtil.subAfter(token, BaseContextConstants.BEARER_HEADER_PREFIX, false);
            return parseJWT(headStr);
        }
        throw BizException.wrap(JWT_PARSER_TOKEN_FAIL);
    }

}
