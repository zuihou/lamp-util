package com.github.zuihou.jwt;


import cn.hutool.core.convert.Convert;
import com.github.zuihou.jwt.model.AuthInfo;
import com.github.zuihou.jwt.model.JwtUserInfo;
import com.github.zuihou.jwt.model.Token;
import com.github.zuihou.jwt.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.github.zuihou.context.BaseContextConstants.BEARER_HEADER_KEY;
import static com.github.zuihou.context.BaseContextConstants.JWT_KEY_ACCOUNT;
import static com.github.zuihou.context.BaseContextConstants.JWT_KEY_NAME;
import static com.github.zuihou.context.BaseContextConstants.JWT_KEY_TOKEN_TYPE;
import static com.github.zuihou.context.BaseContextConstants.JWT_KEY_USER_ID;
import static com.github.zuihou.context.BaseContextConstants.REFRESH_TOKEN_KEY;

/**
 * 认证工具类
 *
 * @author zuihou
 * @date 2020年03月31日19:03:47
 */
@AllArgsConstructor
public class TokenUtil {
    /**
     * 认证服务端使用，如 authority-server
     * 生成和 解析token
     */
    private JwtProperties authServerProperties;


    /**
     * 创建认证token
     *
     * @param userInfo 用户信息
     * @return token
     */
    public AuthInfo createAuthInfo(JwtUserInfo userInfo, Long expireMillis) {
        if (expireMillis == null || expireMillis <= 0) {
            expireMillis = authServerProperties.getExpire();
        }

        //设置jwt参数
        Map<String, String> param = new HashMap<>(16);
        param.put(JWT_KEY_TOKEN_TYPE, BEARER_HEADER_KEY);
        param.put(JWT_KEY_USER_ID, Convert.toStr(userInfo.getUserId(), "0"));
        param.put(JWT_KEY_ACCOUNT, userInfo.getAccount());
        param.put(JWT_KEY_NAME, userInfo.getName());

        Token token = JwtUtil.createJWT(param, expireMillis);

        AuthInfo authInfo = new AuthInfo();
        authInfo.setAccount(userInfo.getAccount());
        authInfo.setName(userInfo.getName());
        authInfo.setUserId(userInfo.getUserId());
        authInfo.setTokenType(BEARER_HEADER_KEY);
        authInfo.setToken(token.getToken());
        authInfo.setExpire(token.getExpire());
        authInfo.setExpiration(token.getExpiration());
        authInfo.setRefreshToken(createRefreshToken(userInfo).getToken());
        return authInfo;
    }

    /**
     * 创建refreshToken
     *
     * @param userInfo 用户信息
     * @return refreshToken
     */
    private Token createRefreshToken(JwtUserInfo userInfo) {
        Map<String, String> param = new HashMap<>(16);
        param.put(JWT_KEY_TOKEN_TYPE, REFRESH_TOKEN_KEY);
        param.put(JWT_KEY_USER_ID, Convert.toStr(userInfo.getUserId(), "0"));
        return JwtUtil.createJWT(param, authServerProperties.getRefreshExpire());
    }

    /**
     * 解析token
     *
     * @param token token
     * @return 用户信息
     */
    public AuthInfo getAuthInfo(String token) {
        Claims claims = JwtUtil.getClaims(token);
        String tokenType = Convert.toStr(claims.get(JWT_KEY_TOKEN_TYPE));
        Long userId = Convert.toLong(claims.get(JWT_KEY_USER_ID));
        String account = Convert.toStr(claims.get(JWT_KEY_ACCOUNT));
        String name = Convert.toStr(claims.get(JWT_KEY_NAME));
        Date expiration = claims.getExpiration();
        return new AuthInfo().setToken(token)
                .setExpire(expiration != null ? expiration.getTime() : 0L)
                .setTokenType(tokenType).setUserId(userId)
                .setAccount(account).setName(name);
    }

    /**
     * 解析刷新token
     *
     * @param token
     * @return
     */
    public AuthInfo parseRefreshToken(String token) {
        Claims claims = JwtUtil.parseJWT(token);
        String tokenType = Convert.toStr(claims.get(JWT_KEY_TOKEN_TYPE));
        Long userId = Convert.toLong(claims.get(JWT_KEY_USER_ID));
        Date expiration = claims.getExpiration();
        return new AuthInfo().setToken(token)
                .setExpire(expiration != null ? expiration.getTime() : 0L)
                .setTokenType(tokenType).setUserId(userId);
    }
}
