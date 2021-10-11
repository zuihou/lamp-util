package top.tangyh.basic.utils;

import java.util.regex.Pattern;

/**
 * 校验器：利用正则表达式校验邮箱、手机号等
 *
 * @author zuihou
 * @date 2019-07-31 10:17
 */
public final class ValidatorUtil {
    private ValidatorUtil() {
    }

    /**
     * 正则表达式:验证用户名(不包含中文和特殊字符)
     */
    public static final String REGEX_USERNAME = "^[a-zA-Z0-9_]\\w{5,254}$";
    public static final Pattern PATTERN_USERNAME = Pattern.compile(REGEX_USERNAME);
    /**
     * 正则表达式:验证密码(不包含特殊字符)
     */
    public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{5,15}$";
    public static final Pattern PATTERN_PASSWORD = Pattern.compile(REGEX_PASSWORD);
    /**
     * 正则表达式:验证邮箱
     */
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    public static final Pattern PATTERN_EMAIL = Pattern.compile(REGEX_EMAIL);
    /**
     * 正则表达式:验证汉字
     */
    public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5]$";
    public static final Pattern PATTERN_CHINESE = Pattern.compile(REGEX_CHINESE);

    /**
     * 正则表达式:验证身份证
     */
    public static final String REGEX_ID_CARD = "(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])";
    public static final Pattern PATTERN_ID_CARD = Pattern.compile(REGEX_ID_CARD);
    /**
     * 正则表达式:验证URL
     */
    public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
    public static final Pattern PATTERN_URL = Pattern.compile(REGEX_URL);
    /**
     * 正则表达式:验证IP地址
     */
    public static final String REGEX_IP_ADDRESS = "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";
    public static final Pattern PATTERN_IP_ADDRESS = Pattern.compile(REGEX_IP_ADDRESS);
    /**
     * 1开头 总共11位
     */
    public static final String REGEX_MOBILE = "^(1)\\d{10}$";
    public static final Pattern PATTERN_MOBILE = Pattern.compile(REGEX_MOBILE);

    /**
     * 校验用户名
     *
     * @param value 用户名
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUserName(String value) {
        return PATTERN_USERNAME.matcher(value).matches();
    }

    /**
     * 校验邮箱
     *
     * @param value 邮箱
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String value) {
        return PATTERN_EMAIL.matcher(value).matches();
    }

    /**
     * 校验身份证号
     *
     * @param value 身份证号
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isIdCard(String value) {
        return PATTERN_ID_CARD.matcher(value).matches();
    }

    /**
     * 校验密码
     *
     * @param value 密码
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String value) {
        return PATTERN_PASSWORD.matcher(value).matches();
    }

    /**
     * 校验手机号
     *
     * @param value 手机号
     * @return 是否校验成功
     */
    public static boolean isMobile(String value) {
        return PATTERN_MOBILE.matcher(value).matches();
    }


    /**
     * 校验ip 地址
     *
     * @param value ip 地址
     * @return 是否校验成功
     */
    public static boolean isIpAddress(String value) {
        return PATTERN_IP_ADDRESS.matcher(value).matches();
    }

    /**
     * 校验 url 地址
     *
     * @param value url 地址
     * @return 是否校验成功
     */
    public static boolean isUrl(String value) {
        return PATTERN_URL.matcher(value).matches();
    }

    /**
     * 校验 汉字
     *
     * @param value 汉字
     * @return 是否校验成功
     */
    public static boolean isChinese(String value) {
        return PATTERN_CHINESE.matcher(value).matches();
    }
}
