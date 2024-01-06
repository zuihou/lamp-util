package top.tangyh.basic.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import static top.tangyh.basic.utils.DateUtils.DEFAULT_DATE_FORMAT;
import static top.tangyh.basic.utils.DateUtils.DEFAULT_DATE_FORMAT_EN;
import static top.tangyh.basic.utils.DateUtils.DEFAULT_DATE_FORMAT_EN_MATCHES;
import static top.tangyh.basic.utils.DateUtils.DEFAULT_DATE_FORMAT_MATCHES;
import static top.tangyh.basic.utils.DateUtils.SLASH_DATE_FORMAT;
import static top.tangyh.basic.utils.DateUtils.SLASH_DATE_FORMAT_MATCHES;

/**
 * 解决 @RequestParam 标记的 LocalDate 类型的入参，参数转换问题。
 * <p>
 * yyyy-MM-dd
 * yyyy/MM/dd
 * yyyy年MM月dd日
 *
 * @author zuihou
 * @date 2019-04-30
 */
public class String2LocalDateConverter extends BaseDateConverter<LocalDate> implements Converter<String, LocalDate> {

    protected static final Map<String, String> FORMAT = new LinkedHashMap<>(5);

    static {
        FORMAT.put(DEFAULT_DATE_FORMAT, DEFAULT_DATE_FORMAT_MATCHES);
        FORMAT.put(SLASH_DATE_FORMAT, SLASH_DATE_FORMAT_MATCHES);
        FORMAT.put(DEFAULT_DATE_FORMAT_EN, DEFAULT_DATE_FORMAT_EN_MATCHES);
    }

    @Override
    protected Map<String, String> getFormat() {
        return FORMAT;
    }

    @Override
    public LocalDate convert(String source) {
        return super.convert(source, (key) -> LocalDate.parse(source, DateTimeFormatter.ofPattern(key)));
    }

}

