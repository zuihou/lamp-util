package top.tangyh.basic.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import static top.tangyh.basic.utils.DateUtils.DEFAULT_TIME_EN_FORMAT;
import static top.tangyh.basic.utils.DateUtils.DEFAULT_TIME_FORMAT;

/**
 * 解决 @RequestParam LocalTime Date 类型的入参，参数转换问题。
 * <p>
 * HH:mm:ss
 * HH时mm分ss秒
 *
 * @author zuihou
 * @date 2019-04-30
 */
public class String2LocalTimeConverter extends BaseDateConverter<LocalTime> implements Converter<String, LocalTime> {

    protected static final Map<String, String> FORMAT = new LinkedHashMap<>(5);

    static {
        FORMAT.put(DEFAULT_TIME_FORMAT, "^\\d{1,2}:\\d{1,2}:\\d{1,2}$");
        FORMAT.put(DEFAULT_TIME_EN_FORMAT, "^\\d{1,2}时\\d{1,2}分\\d{1,2}秒$");
    }

    @Override
    protected Map<String, String> getFormat() {
        return FORMAT;
    }

    @Override
    public LocalTime convert(String source) {
        return super.convert(source, (key) -> LocalTime.parse(source, DateTimeFormatter.ofPattern(key)));
    }
}
