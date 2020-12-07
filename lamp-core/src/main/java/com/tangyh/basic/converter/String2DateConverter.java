package com.tangyh.basic.converter;

import com.tangyh.basic.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.tangyh.basic.exception.BaseException.BASE_VALID_PARAM;
import static com.tangyh.basic.utils.DateUtils.DEFAULT_DATE_FORMAT;
import static com.tangyh.basic.utils.DateUtils.DEFAULT_DATE_FORMAT_EN;
import static com.tangyh.basic.utils.DateUtils.DEFAULT_DATE_FORMAT_EN_MATCHES;
import static com.tangyh.basic.utils.DateUtils.DEFAULT_DATE_FORMAT_MATCHES;
import static com.tangyh.basic.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;
import static com.tangyh.basic.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT_EN;
import static com.tangyh.basic.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT_EN_MATCHES;
import static com.tangyh.basic.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT_MATCHES;
import static com.tangyh.basic.utils.DateUtils.DEFAULT_MONTH_FORMAT;
import static com.tangyh.basic.utils.DateUtils.DEFAULT_MONTH_FORMAT_SLASH;
import static com.tangyh.basic.utils.DateUtils.DEFAULT_YEAR_FORMAT;
import static com.tangyh.basic.utils.DateUtils.SLASH_DATE_FORMAT;
import static com.tangyh.basic.utils.DateUtils.SLASH_DATE_FORMAT_MATCHES;
import static com.tangyh.basic.utils.DateUtils.SLASH_DATE_TIME_FORMAT;
import static com.tangyh.basic.utils.DateUtils.SLASH_DATE_TIME_FORMAT_MATCHES;

/**
 * 解决入参为 Date类型
 *
 * @author zuihou
 * @date 2019-04-30
 */
@Slf4j
public class String2DateConverter extends BaseDateConverter<Date> implements Converter<String, Date> {

    protected static final Map<String, String> FORMAT = new LinkedHashMap(15);

    static {
        FORMAT.put(DEFAULT_YEAR_FORMAT, "^\\d{4}");
        FORMAT.put(DEFAULT_MONTH_FORMAT, "^\\d{4}-\\d{1,2}$");
        FORMAT.put(DEFAULT_DATE_FORMAT, DEFAULT_DATE_FORMAT_MATCHES);
        FORMAT.put("yyyy-MM-dd HH", "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}");
        FORMAT.put("yyyy-MM-dd HH:mm", "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$");
        FORMAT.put(DEFAULT_DATE_TIME_FORMAT, DEFAULT_DATE_TIME_FORMAT_MATCHES);
        FORMAT.put(DEFAULT_MONTH_FORMAT_SLASH, "^\\d{4}/\\d{1,2}$");
        FORMAT.put(SLASH_DATE_FORMAT, SLASH_DATE_FORMAT_MATCHES);
        FORMAT.put("yyyy/MM/dd HH", "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}");
        FORMAT.put("yyyy/MM/dd HH:mm", "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}$");
        FORMAT.put(SLASH_DATE_TIME_FORMAT, SLASH_DATE_TIME_FORMAT_MATCHES);
        FORMAT.put(DEFAULT_DATE_FORMAT_EN, DEFAULT_DATE_FORMAT_EN_MATCHES);
        FORMAT.put(DEFAULT_DATE_TIME_FORMAT_EN, DEFAULT_DATE_TIME_FORMAT_EN_MATCHES);
    }

    /**
     * 格式化日期
     *
     * @param dateStr String 字符型日期
     * @param format  String 格式
     * @return Date 日期
     */
    protected static Date parseDate(String dateStr, String format) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            //严格模式
            dateFormat.setLenient(false);
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            log.info("转换日期失败, date={}, format={}", dateStr, format, e);
            throw new BizException(BASE_VALID_PARAM, e.getMessage(), e);
        }
    }

    @Override
    protected Map<String, String> getFormat() {
        return FORMAT;
    }

    @Override
    @Nullable
    public Date convert(String source) {
        return super.convert(source, key -> parseDate(source, key));
    }

}
