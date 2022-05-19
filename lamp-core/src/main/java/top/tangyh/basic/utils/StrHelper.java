package top.tangyh.basic.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.enums.SqlLike;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

/**
 * 字符串帮助类
 *
 * @author zuihou
 * @date 2019-07-16 22:09
 */
@Slf4j
public final class StrHelper {
    private StrHelper() {
    }

    public static String getOrDef(String val, String def) {
        return DefValueHelper.getOrDef(val, def);
    }

    /**
     * 有 任意 一个 Blank
     *
     * @param css CharSequence
     * @return boolean
     */
    public static boolean isAnyBlank(final CharSequence... css) {
        if (ObjectUtil.isEmpty(css)) {
            return true;
        }
        return Stream.of(css).anyMatch(StrUtil::isBlank);
    }

    /**
     * 是否全非 Blank
     *
     * @param css CharSequence
     * @return boolean
     */
    public static boolean isNoneBlank(final CharSequence... css) {
        if (ObjectUtil.isEmpty(css)) {
            return false;
        }
        return Stream.of(css).allMatch(StrUtil::isNotBlank);
    }

    /**
     * mybatis plus like查询转换
     */
    public static String keywordConvert(String value) {
        if (StrUtil.isBlank(value)) {
            return StrPool.EMPTY;
        }
        value = value.replaceAll(StrPool.PERCENT, "\\\\%");
        value = value.replaceAll(StrPool.UNDERSCORE, "\\\\_");
        return value;
    }

    public static Object keywordConvert(Object value) {
        if (value instanceof String) {
            return keywordConvert(String.valueOf(value));
        }
        return value;
    }

    /**
     * 拼接like条件
     *
     * @param value   值
     * @param sqlType 拼接类型
     * @return 拼接后的值
     */
    public static String like(Object value, SqlLike sqlType) {
        return SqlUtils.concatLike(keywordConvert(String.valueOf(value)), sqlType);
    }

    /**
     * 拼接like 模糊条件
     *
     * @param value 值
     * @return 拼接后的值
     */
    public static String fullLike(String value) {
        return like(value, SqlLike.DEFAULT);
    }

}
