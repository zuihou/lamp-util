package top.tangyh.basic.jwt.utils;

import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import top.tangyh.basic.exception.BizException;
import top.tangyh.basic.utils.StrPool;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static top.tangyh.basic.exception.code.ExceptionCode.JWT_BASIC_INVALID;

/**
 * 工具类
 *
 * @author zuihou
 */
public class Base64Util {


    /**
     * authorization: base64(clientId:clientSec)
     * 解析请求头中存储的 client 信息
     * <p>
     * Basic clientId:clientSec -截取-> clientId:clientSec后调用 extractClient 解码
     *
     * @param basicHeader Basic clientId:clientSec
     * @return clientId:clientSec
     */
    public static String[] getClient(String basicHeader) {
        if (StrUtil.isEmpty(basicHeader)) {
            throw BizException.wrap(JWT_BASIC_INVALID);
        }

        return extractClient(basicHeader);
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
}
