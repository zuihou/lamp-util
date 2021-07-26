package top.tangyh.basic.echo.core;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import top.tangyh.basic.annotation.echo.Echo;
import top.tangyh.basic.echo.manager.CacheLoadKeys;
import top.tangyh.basic.echo.manager.ClassManager;
import top.tangyh.basic.echo.manager.FieldParam;
import top.tangyh.basic.echo.manager.LoadKey;
import top.tangyh.basic.echo.properties.EchoProperties;
import top.tangyh.basic.jackson.JsonUtil;
import top.tangyh.basic.model.EchoVO;
import top.tangyh.basic.model.LoadService;
import top.tangyh.basic.model.RemoteData;
import top.tangyh.basic.utils.StrPool;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static top.tangyh.basic.utils.StrPool.EMPTY;


/**
 * 字典数据回显工具类
 * 1. 通过反射将obj的字段上标记了@Echo注解的字段解析出来
 * 2. 依次查询待回显的数据
 * 3. 将查询出来结果回显到obj的 @Echo注解的字段中
 *
 * @author zuihou
 * @date 2019/11/13
 */
@Slf4j
public class EchoService {
    private static final int DEF_MAP_SIZE = 20;
    private static final String[] BASE_TYPES = {
            "java.lang.Integer", "java.lang.Byte", "java.lang.Long", "java.lang.Double",
            "java.lang.Float", "java.lang.Character", "java.lang.Short", "java.lang.Boolean", StrPool.STRING_TYPE_NAME,
            "top.tangyh.basic.model.RemoteData"
    };
    private static final Map<String, FieldParam> CACHE = new HashMap<>();
    private final Map<String, LoadService> strategyMap = new ConcurrentHashMap<>();
    /**
     * 动态配置参数
     */
    private final EchoProperties ips;
    /**
     * 内存缓存
     */
    private LoadingCache<CacheLoadKeys, Map<Serializable, Object>> caches;

    public EchoService(EchoProperties ips, Map<String, LoadService> strategyMap) {
        strategyMap.forEach(this.strategyMap::put);
        this.ips = ips;
        EchoProperties.GuavaCache guavaCache = ips.getGuavaCache();
        if (guavaCache.getEnabled()) {
            this.caches = CacheBuilder.newBuilder()
                    .maximumSize(guavaCache.getMaximumSize())
                    .refreshAfterWrite(guavaCache.getRefreshWriteTime(), TimeUnit.MINUTES)
                    .build(new DefCacheLoader(guavaCache));
        }

    }

    public void action(Object obj, String... ignoreFields) {
        this.action(obj, false, ignoreFields);
    }

    /**
     * 回显数据的3个步骤：（出现回显失败时，认真debug该方法）
     * <p>
     * 1. parse: 通过反射将obj的字段上标记了 @Echo 注解的字段解析出来, 封装到typeMap中
     * 2. load: 依次查询待回显的数据
     * 3. write: 将查询出来的结果 反射或put 到obj的 字段或echoMap 中
     * <p>
     * 注意：若对象中需要回显的字段之间出现循环引用，很可能发生异常，所以请保证不要出现循环引用！！！
     *
     * @param obj          需要回显的参数，支持 自定义对象(User)、集合(List<User>、Set<User>)、IPage
     * @param isUseCache   是否使用内存缓存
     * @param ignoreFields 忽略字段
     */
    public void action(Object obj, boolean isUseCache, String... ignoreFields) {
        try {
            /*
             LoadKey 为远程查询的类+方法
             Map<Serializable, Object> 为 待查询的数据
             Serializable 为待查询数据的唯一标示（可以是id、code等唯一健）
             Object 为查询后的值
             */
            Map<LoadKey, Map<Serializable, Object>> typeMap = new ConcurrentHashMap<>(DEF_MAP_SIZE);

            long parseStart = System.currentTimeMillis();

            //1. 通过反射将obj的字段上标记了@Echo注解的字段解析出来
            this.parse(obj, typeMap, 1, ignoreFields);

            long parseEnd = System.currentTimeMillis();

            if (typeMap.isEmpty()) {
                return;
            }

            // 2. 依次查询待回显的数据
            this.load(typeMap, isUseCache);

            long echoStart = System.currentTimeMillis();

            // 3. 将查询出来结果回显到obj的 @Echo注解的字段中
            this.write(obj, typeMap, 1);

            long echoEnd = System.currentTimeMillis();

            log.info("解析耗时={} ms", (parseEnd - parseStart));
            log.info("批量查询耗时={} ms", (echoStart - parseEnd));
            log.info("回显耗时={} ms", (echoEnd - echoStart));
        } catch (Exception e) {
            log.warn("回显失败", e);
        }
    }

    /**
     * 1，遍历字段，解析出那些字段上标记了@Echo注解
     *
     * @param obj          对象
     * @param typeMap      数据
     * @param depth        当前递归深度
     * @param ignoreFields 忽略回显的字段
     */
    private void parse(Object obj, Map<LoadKey, Map<Serializable, Object>> typeMap, int depth, String... ignoreFields) {
        if (obj == null) {
            return;
        }
        if (depth > ips.getMaxDepth()) {
            log.info("出现循环依赖，最多执行 {} 次， 已执行 {} 次，已为您跳出循环", ips.getMaxDepth(), depth);
            return;
        }

        if (obj instanceof IPage) {
            List<?> records = ((IPage<?>) obj).getRecords();
            parseList(records, typeMap, depth, ignoreFields);
            return;
        }

        if (obj instanceof Collection) {
            parseList((Collection<?>) obj, typeMap, depth, ignoreFields);
            return;
        }

        //解析方法上的注解，计算出obj对象中所有需要查询的数据
        List<Field> fields = ClassManager.getFields(obj.getClass());

        for (Field field : fields) {
            FieldParam fieldParam = getFieldParam(obj, field, typeMap,
                    innerTypeMap -> parse(ReflectUtil.getFieldValue(obj, field), innerTypeMap, depth + 1, ignoreFields),
                    ignoreFields
            );
            if (fieldParam == null) {
                continue;
            }

            LoadKey type = fieldParam.getLoadKey();
            Map<Serializable, Object> valueMap = typeMap.getOrDefault(type, new ConcurrentHashMap<>(DEF_MAP_SIZE));
            valueMap.put(fieldParam.getActualValue(), Collections.emptyMap());
            typeMap.put(type, valueMap);
        }
    }

    /**
     * 解析 list
     *
     * @param list         数据集合
     * @param typeMap      待查询的参数
     * @param ignoreFields 忽略回显的字段
     */
    private void parseList(Collection<?> list, Map<LoadKey, Map<Serializable, Object>> typeMap, int depth, String... ignoreFields) {
        for (Object item : list) {
            parse(item, typeMap, depth, ignoreFields);
        }
    }

    /**
     * 加载数据
     * <p>
     * 注意： 需要自行实现LoadService的2个方法
     *
     * @param typeMap
     */
    @SneakyThrows
    private void load(Map<LoadKey, Map<Serializable, Object>> typeMap, boolean isUseCache) {
        for (Map.Entry<LoadKey, Map<Serializable, Object>> entries : typeMap.entrySet()) {
            LoadKey type = entries.getKey();
            Map<Serializable, Object> valueMap = entries.getValue();
            Set<Serializable> keys = valueMap.keySet();

            LoadService loadService = strategyMap.get(type.getApi());
            if (loadService == null) {
                log.warn("处理字段的回显数据时，没有找到 @Echo 中的api：[{}]实例。" +
                        "请确保你自定义的接口实现了 LoadService 中的 findByIds 方法。" +
                        "若api指定的是ServiceImpl，请确保在同一个服务内。", type.getApi());
                continue;
            }

            CacheLoadKeys lk = new CacheLoadKeys(type, loadService, keys);
            Map<Serializable, Object> value = ips.getGuavaCache().getEnabled() && isUseCache ? caches.get(lk) : lk.loadMap();

            typeMap.put(type, value);
        }
    }

    /**
     * 向obj对象的字段中回显值
     *
     * @param obj          当前对象
     * @param typeMap      数据
     * @param depth        当前递归深度
     * @param ignoreFields 忽略回显的字段
     */
    @SneakyThrows
    private void write(Object obj, Map<LoadKey, Map<Serializable, Object>> typeMap, int depth, String... ignoreFields) {
        if (obj == null) {
            return;
        }
        if (depth > ips.getMaxDepth()) {
            log.info("出现循环依赖，最多执行 {} 次， 已执行 {} 次，已为您跳出循环", ips.getMaxDepth(), depth);
            return;
        }

        if (obj instanceof IPage) {
            List<?> records = ((IPage<?>) obj).getRecords();
            writeList(records, typeMap, ignoreFields);
            return;
        }
        if (obj instanceof Collection) {
            writeList((Collection<?>) obj, typeMap, ignoreFields);
            return;
        }

        iterationWrite(obj, typeMap, depth, ignoreFields);
    }

    private void iterationWrite(Object obj, Map<LoadKey, Map<Serializable, Object>> typeMap, int depth, String... ignoreFields) {
        //解析方法上的注解，计算出obj对象中所有需要查询的数据
        List<Field> fields = ClassManager.getFields(obj.getClass());
        for (Field field : fields) {
            FieldParam fieldParam = getFieldParam(obj, field, typeMap,
                    innerTypeMap -> write(ReflectUtil.getFieldValue(obj, field), innerTypeMap, depth + 1, ignoreFields),
                    ignoreFields);
            if (fieldParam == null) {
                continue;
            }
            Echo inField = fieldParam.getEcho();
            Object actualValue = fieldParam.getActualValue();
            Object originalValue = fieldParam.getOriginalValue();
            String fieldName = fieldParam.getFieldName();
            String ref = inField.ref();
            LoadKey loadKey = fieldParam.getLoadKey();

            Object echoValue = getEchoValue(inField, actualValue, originalValue, loadKey, typeMap);
            if (echoValue == null) {
                continue;
            }
            if (echoValue instanceof Map && ((Map<?, ?>) echoValue).isEmpty()) {
                continue;
            }

            // feign 接口序列化 丢失类型
            if (echoValue instanceof Map && !Object.class.equals(inField.beanClass())) {
                echoValue = JsonUtil.parse(JsonUtil.toJson(echoValue), inField.beanClass());
            }

            if (StrUtil.isNotEmpty(ref)) {
                ReflectUtil.setFieldValue(obj, ref, echoValue);
            }

            // 将新的值 反射 到指定字段
            if (obj instanceof EchoVO) {
                EchoVO vo = (EchoVO) obj;
                vo.getEchoMap().put(fieldName, echoValue);
            } else if (originalValue instanceof RemoteData) {
                RemoteData remoteData = (RemoteData) originalValue;
                remoteData.setData(echoValue);
            } else {
                ReflectUtil.setFieldValue(obj, field, echoValue);
            }
        }
    }

    /**
     * 从 valueMap
     *
     * @param actualValue 处理后的查询值
     * @param typeMap     已查询后的集合
     * @return 已查询后的值
     */
    private Object getEchoValue(Echo echo, Object actualValue, Object originalValue, LoadKey loadKey, Map<LoadKey, Map<Serializable, Object>> typeMap) {
        if (ObjectUtil.isEmpty(actualValue)) {
            return null;
        }
        Map<Serializable, Object> valueMap = typeMap.get(loadKey);

        if (MapUtil.isEmpty(valueMap)) {
            return null;
        }

        Object newVal = valueMap.get(actualValue);
        // 可能由于序列化原因导致 get 失败，重新尝试get
        if (ObjectUtil.isNotNull(newVal)) {
            return newVal;
        }

        newVal = valueMap.get(actualValue.toString());
        // 可能由于是多key原因导致get失败
        if (ObjectUtil.isNull(newVal) && StrUtil.isNotEmpty(echo.dictType())) {
            List<String> codes = StrUtil.split(originalValue.toString(), ips.getDictItemSeparator());

            newVal = codes.stream().map(item -> {
                String val = valueMap.getOrDefault(echo.dictType() + ips.getDictSeparator() + item, EMPTY).toString();
                return val == null ? EMPTY : val;
            }).collect(Collectors.joining(ips.getDictItemSeparator()));
        }
        return newVal;
    }

    /**
     * 回显 集合
     *
     * @param list         数据集合
     * @param typeMap      待查询的参数
     * @param ignoreFields 忽略回显的字段
     */
    private void writeList(Collection<?> list, Map<LoadKey, Map<Serializable, Object>> typeMap, String... ignoreFields) {
        for (Object item : list) {
            write(item, typeMap, 1, ignoreFields);
        }
    }

    /**
     * 提取参数
     *
     * @param obj          当前对象
     * @param field        当前字段
     * @param typeMap      待查询的集合
     * @param consumer     字段为复杂类型时的回调处理
     * @param ignoreFields 忽略回显的字段
     * @return 字段参数
     */
    private FieldParam getFieldParam(Object obj, Field field, Map<LoadKey, Map<Serializable, Object>> typeMap,
                                     Consumer<Map<LoadKey, Map<Serializable, Object>>> consumer, String... ignoreFields) {
        String key = obj.getClass().getName() + "###" + field.getName();
        FieldParam fieldParam;
        // 是否排除
        if (ArrayUtil.contains(ignoreFields, field.getName())) {
            log.debug("已经忽略{}字段的解析", field.getName());
            return null;
        }
        // 类型
        if (isNotBaseType(field)) {
            consumer.accept(typeMap);
            return null;
        }

        if (CACHE.containsKey(key)) {
            fieldParam = CACHE.get(key);
        } else {
            // 是否标记@Echo注解
            Echo echo = field.getDeclaredAnnotation(Echo.class);

            LoadKey loadKey = new LoadKey(echo);
            fieldParam = FieldParam.builder()
                    .echo(echo)
                    .loadKey(loadKey)
                    .fieldName(field.getName())
                    .build();
            CACHE.put(key, fieldParam);
        }

        field.setAccessible(true);
        Object originalValue = ReflectUtil.getFieldValue(obj, field);
        if (originalValue == null) {
            log.debug("字段[{}]为空,跳过", field.getName());
            return null;
        }

        Serializable actualValue = getActualValue(fieldParam.getEcho(), originalValue);
        if (ObjectUtil.isEmpty(actualValue)) {
            return null;
        }
        fieldParam.setOriginalValue(originalValue);
        fieldParam.setActualValue(actualValue);
        return fieldParam;
    }


    /**
     * 获取查询用的key
     *
     * @param echo          当前字段标记的注解
     * @param originalValue 当前字段的具体值
     * @return 从当前字段的值构造出，调用api#method方法的参数
     */
    private Serializable getActualValue(Echo echo, Object originalValue) {
        Serializable actualValue;
        if (originalValue instanceof RemoteData) {
            RemoteData remoteData = (RemoteData) originalValue;
            actualValue = (Serializable) remoteData.getKey();
        } else {
            actualValue = (Serializable) originalValue;
        }

        String dictType = echo.dictType();
        if (StrUtil.isNotEmpty(dictType)) {
            actualValue = dictType;
        }
        return actualValue;
    }


    /**
     * 判断字段是否不为基本类型
     *
     * @param field 字段
     * @return 是基本类型返回false
     */
    private boolean isNotBaseType(Field field) {
        return !isBaseType(field);
    }

    /**
     * 判断字段是否为基本类型
     *
     * @param field 字段
     * @return 是基本类型返回true
     */
    private boolean isBaseType(Field field) {
        String typeName = field.getType().getName();

        for (String baseType : BASE_TYPES) {
            if (baseType.equals(typeName)) {
                return true;
            }
        }
        return false;
    }
}
