package com.github.zuihou.utils;

import cn.hutool.core.util.StrUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

/**
 * 字符集工具类
 *
 * @author zuihou
 * @date 2020年03月31日20:42:11
 */
public class Charsets {

    /**
     * 字符集ISO-8859-1
     */
    public static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;
    public static final String ISO_8859_1_NAME = ISO_8859_1.name();

    /**
     * 字符集GBK
     */
    public static final Charset GBK = Charset.forName(StrPool.GBK);
    public static final String GBK_NAME = GBK.name();

    /**
     * 字符集utf-8
     */
    public static final Charset UTF_8 = StandardCharsets.UTF_8;
    public static final String UTF_8_NAME = UTF_8.name();

    /**
     * 转换为Charset对象
     *
     * @param charsetName 字符集，为空则返回默认字符集
     * @return Charsets
     * @throws UnsupportedCharsetException 编码不支持
     */
    public static Charset charset(String charsetName) throws UnsupportedCharsetException {
        return StrUtil.isBlank(charsetName) ? Charset.defaultCharset() : Charset.forName(charsetName);
    }

}
