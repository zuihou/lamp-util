package top.tangyh.basic.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import static top.tangyh.basic.utils.DateUtils.DEFAULT_DATE_FORMAT;
import static top.tangyh.basic.utils.DateUtils.DEFAULT_DATE_FORMAT_EN;
import static top.tangyh.basic.utils.DateUtils.DEFAULT_DATE_FORMAT_EN_MATCHES;
import static top.tangyh.basic.utils.DateUtils.DEFAULT_DATE_FORMAT_MATCHES;
import static top.tangyh.basic.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;
import static top.tangyh.basic.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT_EN;
import static top.tangyh.basic.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT_EN_MATCHES;
import static top.tangyh.basic.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT_MATCHES;
import static top.tangyh.basic.utils.DateUtils.SLASH_DATE_FORMAT;
import static top.tangyh.basic.utils.DateUtils.SLASH_DATE_FORMAT_MATCHES;
import static top.tangyh.basic.utils.DateUtils.SLASH_DATE_TIME_FORMAT;
import static top.tangyh.basic.utils.DateUtils.SLASH_DATE_TIME_FORMAT_MATCHES;

/**
 * 解决 @RequestParam 标记的 LocalDateTime 类型的入参，参数转换问题。
 * <p>
 * yyyy-MM-dd HH:mm:ss
 * yyyy/MM/dd HH:mm:ss
 * yyyy年MM月dd日HH时mm分ss秒
 *
 * @author zuihou
 * @date 2019-04-30
 */
public class String2LocalDateTimeConverter extends BaseDateConverter<LocalDateTime> implements Converter<String, LocalDateTime> {

    protected static final Map<String, String> FORMAT = new LinkedHashMap<>(10);

    static {
        FORMAT.put(DEFAULT_DATE_TIME_FORMAT, DEFAULT_DATE_TIME_FORMAT_MATCHES);
        FORMAT.put(SLASH_DATE_TIME_FORMAT, SLASH_DATE_TIME_FORMAT_MATCHES);
        FORMAT.put(DEFAULT_DATE_TIME_FORMAT_EN, DEFAULT_DATE_TIME_FORMAT_EN_MATCHES);

        FORMAT.put(DEFAULT_DATE_FORMAT, DEFAULT_DATE_FORMAT_MATCHES);
        FORMAT.put(SLASH_DATE_FORMAT, SLASH_DATE_FORMAT_MATCHES);
        FORMAT.put(DEFAULT_DATE_FORMAT_EN, DEFAULT_DATE_FORMAT_EN_MATCHES);
    }

    @Override
    protected Map<String, String> getFormat() {
        return FORMAT;
    }

    @Override
    public LocalDateTime convert(String source) {
        return super.convert(source, key -> {
            if (source.matches(DEFAULT_DATE_FORMAT_MATCHES)
                    || source.matches(DEFAULT_DATE_FORMAT_EN_MATCHES)
                    || source.matches(SLASH_DATE_FORMAT_MATCHES)
            ) {
                return LocalDateTime.of(LocalDate.parse(source, DateTimeFormatter.ofPattern(key)), LocalTime.MIN);
            }
            return LocalDateTime.parse(source, DateTimeFormatter.ofPattern(key));
        });
    }
}
