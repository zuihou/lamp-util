package top.tangyh.basic.jwt;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import top.tangyh.basic.exception.BizException;
import top.tangyh.basic.jwt.model.AuthInfo;
import top.tangyh.basic.jwt.model.JwtInfo;
import top.tangyh.basic.jwt.model.Token;
import top.tangyh.basic.jwt.properties.JwtProperties;
import top.tangyh.basic.jwt.utils.JwtUtil;
import top.tangyh.basic.utils.DateUtils;
import top.tangyh.basic.utils.StrPool;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import static top.tangyh.basic.context.ContextConstants.JWT_KEY_COMPANY_ID;
import static top.tangyh.basic.context.ContextConstants.JWT_KEY_DEPT_ID;
import static top.tangyh.basic.context.ContextConstants.JWT_KEY_EMPLOYEE_ID;
import static top.tangyh.basic.context.ContextConstants.JWT_KEY_TOP_COMPANY_ID;
import static top.tangyh.basic.context.ContextConstants.JWT_KEY_USER_ID;
import static top.tangyh.basic.context.ContextConstants.JWT_KEY_UUID;

/**
 * 认证工具类
 *
 * @author zuihou
 * @date 2020年03月31日19:03:47
 */
@Slf4j
public class TokenHelper {
    /**
     * 认证服务端使用，如 authority-server
     * 生成和 解析token
     */
    private final JwtProperties jwtProperties;

    public TokenHelper(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        if (StrUtil.isEmpty(jwtProperties.getJwtSignKey())) {
            throw BizException.wrap("请配置 {}.jwtSignKey 参数", JwtProperties.PREFIX);
        }
        JwtUtil.BASE64_SECURITY = Base64.getEncoder().encodeToString(jwtProperties.getJwtSignKey().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 创建认证token
     *
     * @param jwtInfo 用户信息
     * @return token
     */
    public Token buildToken(JwtInfo jwtInfo, Long expireMillis) {
        if (expireMillis == null || expireMillis <= 0) {
            expireMillis = jwtProperties.getExpire();
        }

        //设置jwt参数
        Map<String, String> param = MapUtil.newHashMap();
        param.put(JWT_KEY_USER_ID, Convert.toStr(jwtInfo.getUserId(), StrPool.EMPTY));
        param.put(JWT_KEY_EMPLOYEE_ID, Convert.toStr(jwtInfo.getEmployeeId(), StrPool.EMPTY));
        param.put(JWT_KEY_COMPANY_ID, Convert.toStr(jwtInfo.getCurrentCompanyId(), StrPool.EMPTY));
        param.put(JWT_KEY_TOP_COMPANY_ID, Convert.toStr(jwtInfo.getCurrentTopCompanyId(), StrPool.EMPTY));
        param.put(JWT_KEY_DEPT_ID, Convert.toStr(jwtInfo.getCurrentDeptId(), StrPool.EMPTY));
        param.put(JWT_KEY_UUID, Convert.toStr(jwtInfo.getUuid()));

        Token token = JwtUtil.createJwt(param, expireMillis);
        BeanUtil.copyProperties(jwtInfo, token);
        return token;
    }

    /**
     * 解析token
     *
     * @param tokenStr token
     * @return 用户信息
     */
    public Token parseToken(String tokenStr) {
        Claims claims = JwtUtil.getClaims(tokenStr, jwtProperties.getAllowedClockSkewSeconds());
        Long userId = Convert.toLong(claims.get(JWT_KEY_USER_ID), null);
        Long employeeId = Convert.toLong(claims.get(JWT_KEY_EMPLOYEE_ID), null);
        Long companyId = Convert.toLong(claims.get(JWT_KEY_COMPANY_ID), null);
        Long topCompanyId = Convert.toLong(claims.get(JWT_KEY_TOP_COMPANY_ID), null);
        Long deptId = Convert.toLong(claims.get(JWT_KEY_DEPT_ID), null);
        String uuid = Convert.toStr(claims.get(JWT_KEY_UUID), StrPool.EMPTY);
        Date expiration = claims.getExpiration();
        Token token = new Token(tokenStr, expiration != null ? expiration.getTime() : 0L, DateUtils.date2LocalDateTime(expiration));
        token.setUserId(userId).setEmployeeId(employeeId).setUuid(uuid)
                .setCurrentCompanyId(companyId).setCurrentTopCompanyId(topCompanyId).setCurrentDeptId(deptId);
        return token;
    }

    public Token parseTokenSneaky(String tokenStr) {
        try {
            return parseToken(tokenStr);
        } catch (Exception e) {
            log.warn("解析token失败： {}", tokenStr, e);
            return null;
        }
    }

    /**
     * 解析刷新token
     *
     * @param token 待解析的token
     * @return 认证信息
     */
    @Deprecated
    public AuthInfo parseRefreshToken(String token) {
        Claims claims = JwtUtil.parseJwt(token, jwtProperties.getAllowedClockSkewSeconds());
        Long userId = Convert.toLong(claims.get(JWT_KEY_USER_ID));
        Date expiration = claims.getExpiration();
        return new AuthInfo().setToken(token)
                .setExpire(expiration != null ? expiration.getTime() : 0L)
                .setUserId(userId);
    }
}
