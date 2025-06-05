package com.xxl.job.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * date util
 *
 * @author xuxueli 2018-08-19 01:24:11
 */
public class DateUtil {

    public static final String CRON_FORMAT = "ss mm HH dd MM ? yyyy";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final ThreadLocal<Map<String, DateFormat>> DATE_FORMAT_THREAD_LOCAL = new ThreadLocal<Map<String, DateFormat>>();
    // ---------------------- format parse ----------------------
    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    private static DateFormat getDateFormat(String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("pattern cannot be empty.");
        }

        Map<String, DateFormat> dateFormatMap = DATE_FORMAT_THREAD_LOCAL.get();
        if (dateFormatMap != null && dateFormatMap.containsKey(pattern)) {
            return dateFormatMap.get(pattern);
        }

        synchronized (DATE_FORMAT_THREAD_LOCAL) {
            if (dateFormatMap == null) {
                dateFormatMap = new HashMap<>();
            }
            dateFormatMap.put(pattern, new SimpleDateFormat(pattern));
            DATE_FORMAT_THREAD_LOCAL.set(dateFormatMap);
        }

        return dateFormatMap.get(pattern);
    }


    /**
     * 转换 Date 为 cron , eg.  "0 07 10 15 1 ? 2016"
     *
     * @param date 时间点
     * @return cron 表达式
     */
    public static String getCron(Date date) {
        return format(date, CRON_FORMAT);
    }

    public static String getCron(String date) {
        return format(parseDateTime(date), CRON_FORMAT);
    }

    /**
     * 转换 LocalDateTime 为 cron , eg.  "0 07 10 15 1 ? 2016"
     *
     * @param date 时间点
     * @return cron 表达式
     */
    public static String getCron(LocalDateTime date) {
        return format(date, CRON_FORMAT);
    }

    /**
     * 格式化日期,返回格式为 yyyy-MM
     *
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime date, String pattern) {
        if (date == null) {
            date = LocalDateTime.now();
        }
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }


    /**
     * format datetime. like "yyyy-MM-dd"
     *
     * @param date 日期
     * @return 字符日期
     */
    public static String formatDate(Date date) {
        return format(date, DATE_FORMAT);
    }

    /**
     * format date. like "yyyy-MM-dd HH:mm:ss"
     *
     * @param date 日期
     * @return 字符日期
     */
    public static String formatDateTime(Date date) {
        return format(date, DATETIME_FORMAT);
    }

    /**
     * format date
     *
     * @param date 日期
     * @param patten 格式
     * @return 字符日期
     */
    public static String format(Date date, String patten) {
        return getDateFormat(patten).format(date);
    }

    /**
     * parse date string, like "yyyy-MM-dd HH:mm:s"
     *
     * @param dateString 字符日期
     * @return 日期
     */
    public static Date parseDate(String dateString) {
        return parse(dateString, DATE_FORMAT);
    }

    /**
     * parse datetime string, like "yyyy-MM-dd HH:mm:ss"
     *
     * @param dateString 字符日期
     * @return 日期
     */
    public static Date parseDateTime(String dateString) {
        return parse(dateString, DATETIME_FORMAT);
    }

    /**
     * parse date
     *
     * @param dateString 字符日期
     * @param pattern 格式
     * @return 日期
     */
    public static Date parse(String dateString, String pattern) {
        try {
            return getDateFormat(pattern).parse(dateString);
        } catch (Exception e) {
            logger.warn("parse date error, dateString = {}, pattern={}; errorMsg = {}", dateString, pattern, e.getMessage());
            return null;
        }
    }


    // ---------------------- add date ----------------------

    public static Date addYears(final Date date, final int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    public static Date addMonths(final Date date, final int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    public static Date addDays(final Date date, final int amount) {
        return add(date, Calendar.DAY_OF_MONTH, amount);
    }

    public static Date addHours(final Date date, final int amount) {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    public static Date addMinutes(final Date date, final int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    private static Date add(final Date date, final int calendarField, final int amount) {
        if (date == null) {
            return null;
        }
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

}
