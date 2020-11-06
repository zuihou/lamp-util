package com.github.zuihou.injection.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.zuihou.context.BaseContextHandler;
import com.github.zuihou.injection.annonation.InjectionField;
import com.github.zuihou.injection.properties.InjectionProperties;
import com.github.zuihou.jackson.JsonUtil;
import com.github.zuihou.model.RemoteData;
import com.github.zuihou.utils.SpringUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.github.zuihou.utils.StrPool.EMPTY;


/**
 * 字典数据注入工具类
 * 1. 通过反射将obj的字段上标记了@InjectionFiled注解的字段解析出来
 * 2. 依次查询待注入的数据
 * 3. 将查询出来结果注入到obj的 @InjectionFiled注解的字段中
 *
 * @author zuihou
 * @date 2019/11/13
 */
@Slf4j
public class InjectionCore {
    private final static int DEF_MAP_SIZE = 20;
    /**
     * 动态配置参数
     */
    private final InjectionProperties ips;
    /**
     * 侦听执行器服务
     */
    private ListeningExecutorService backgroundRefreshPools;
    /**
     * 内存缓存
     */
    private LoadingCache<InjectionFieldExtPo, Map<Serializable, Object>> caches;

    public InjectionCore(InjectionProperties ips) {
        this.ips = ips;
        InjectionProperties.GuavaCache guavaCache = ips.getGuavaCache();
        if (guavaCache.getEnabled()) {
            this.backgroundRefreshPools = MoreExecutors.listeningDecorator(
                    new ThreadPoolExecutor(guavaCache.getRefreshThreadPoolSize(), guavaCache.getRefreshThreadPoolSize(),
                            0L, TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<>())
            );

            CacheLoader<InjectionFieldExtPo, Map<Serializable, Object>> cacheLoader = new CacheLoader<InjectionFieldExtPo, Map<Serializable, Object>>() {
                /**
                 * 内存缓存不存在时， 调用时触发加载数据
                 *
                 * @param type 扩展参数
                 * @return 加载后的数据
                 */
                @Override
                public Map<Serializable, Object> load(InjectionFieldExtPo type) {
                    log.info("首次读取缓存: " + type);
                    return loadMap(type);
                }

                /**
                 * 重新载入数据
                 *
                 * @param key      扩展参数
                 * @param oldValue 原来的值
                 * @return 重新加载后的数据
                 */
                @Override
                public ListenableFuture<Map<Serializable, Object>> reload(InjectionFieldExtPo key, Map<Serializable, Object> oldValue) {
                    return backgroundRefreshPools.submit(() -> {
                        BaseContextHandler.setTenant(key.getTenant());
                        return load(key);
                    });
                }
            };
            this.caches = CacheBuilder.newBuilder()
                    .maximumSize(guavaCache.getMaximumSize())
                    .refreshAfterWrite(guavaCache.getRefreshWriteTime(), TimeUnit.MINUTES)
                    .build(cacheLoader);
        }
    }

    /**
     * 加载数据
     *
     * @param type 扩展参数
     * @return 查询指定接口后得到的值
     */
    private Map<Serializable, Object> loadMap(InjectionFieldExtPo type) {
        Object bean;
        if (StrUtil.isNotEmpty(type.getApi())) {
            bean = SpringUtils.getBean(type.getApi());
            log.info("建议在方法： [{}.{}]，上加入缓存，加速查询", type.getApi(), type.getMethod());
        } else {
            bean = SpringUtils.getBean(type.getApiClass());
            log.info("建议在方法： [{}.{}]，上加入缓存，加速查询", type.getApiClass().toString(), type.getMethod());
        }
        return ReflectUtil.invoke(bean, type.getMethod(), type.getKeys());
    }

    /**
     * 手动注入数据的3个步骤：（出现注入失败时，认真debug该方法）
     * <p>
     * 1. 通过反射将obj的字段上标记了 @InjectionField 注解的字段解析出来
     * 2. 依次查询待注入的数据
     * 3. 将查询出来结果注入到obj的 @InjectionField 注解的字段中
     * <p>
     * 注意：若对象中需要注入的字段之间出现循环引用，很可能发生异常，所以请保证不要出现循环引用！！！
     *
     * @param obj          需要注入的对象、集合、IPage
     * @param isUseCache   是否使用guava缓存
     * @param ignoreFields 忽略字段
     */
    public void injection(Object obj, boolean isUseCache, String... ignoreFields) {
        try {
             /*
             InjectionFieldPo 为远程查询的对象
             Map<Serializable, Object> 为 待查询的数据
             Serializable 为待查询数据的唯一标示（可以是id、code等唯一健）
             Object 为查询后的值
             */
            Map<InjectionFieldPo, Map<Serializable, Object>> typeMap = new ConcurrentHashMap<>(DEF_MAP_SIZE);

            long parseStart = System.currentTimeMillis();
            //1. 通过反射将obj的字段上标记了@InjectionFiled注解的字段解析出来
            parse(obj, typeMap, 1, ignoreFields);
            long parseEnd = System.currentTimeMillis();

            log.info("解析耗时={} ms", (parseEnd - parseStart));
            if (typeMap.isEmpty()) {
                return;
            }

            // 2. 依次查询待注入的数据
            for (Map.Entry<InjectionFieldPo, Map<Serializable, Object>> entries : typeMap.entrySet()) {
                InjectionFieldPo type = entries.getKey();
                Map<Serializable, Object> valueMap = entries.getValue();
                Set<Serializable> keys = valueMap.keySet();
                try {
                    InjectionFieldExtPo extPo = new InjectionFieldExtPo(type, keys);
                    // 根据是否启用guava缓存 决定从那里调用
                    Map<Serializable, Object> value = ips.getGuavaCache().getEnabled() && isUseCache ? caches.get(extPo) : loadMap(extPo);
                    typeMap.put(type, value);
                } catch (Exception e) {
                    log.error("远程调用方法 [{}({}).{}] 失败， 请确保系统存在该方法", type.getApi(), type.getApiClass().toString(), type.getMethod(), e);
                }
            }

            long injectionStart = System.currentTimeMillis();
            log.info("批量查询耗时={} ms", (injectionStart - parseEnd));

            // 3. 将查询出来结果注入到obj的 @InjectionFiled注解的字段中
            injection(obj, typeMap, 1);
            long injectionEnd = System.currentTimeMillis();

            log.info("注入耗时={} ms", (injectionEnd - injectionStart));
        } catch (Exception e) {
            log.warn("注入失败", e);
        }
    }

    /**
     * 将obj中标记InjectionField注解的字段，注入新值
     *
     * @param obj          对象、集合、IPage
     * @param ignoreFields obj中需要忽略的字段
     */
    public void injection(Object obj, String... ignoreFields) {
        injection(obj, false, ignoreFields);
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
        return "java.lang.Integer".equals(typeName) ||
                "java.lang.Byte".equals(typeName) ||
                "java.lang.Long".equals(typeName) ||
                "java.lang.Double".equals(typeName) ||
                "java.lang.Float".equals(typeName) ||
                "java.lang.Character".equals(typeName) ||
                "java.lang.Short".equals(typeName) ||
                "java.lang.Boolean".equals(typeName) ||
                "java.lang.String".equals(typeName) ||
                "com.github.zuihou.model.RemoteData".equals(typeName);
    }


    /**
     * 1，遍历字段，解析出数据
     * 2，遍历字段，设值
     *
     * @param obj          对象
     * @param typeMap      数据
     * @param depth        当前递归深度
     * @param ignoreFields 忽略注入的字段
     */
    private void parse(Object obj, Map<InjectionFieldPo, Map<Serializable, Object>> typeMap, int depth, String... ignoreFields) {
        if (obj == null) {
            return;
        }
        if (depth > ips.getMaxDepth()) {
            log.info("出现循环依赖，最多执行 {} 次， 已执行 {} 次，已为您跳出循环", ips.getMaxDepth(), depth);
            return;
        }
        if (typeMap == null) {
            typeMap = new ConcurrentHashMap<>(DEF_MAP_SIZE);
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
        Field[] fields = ReflectUtil.getFields(obj.getClass());

        for (Field field : fields) {
            FieldParam fieldParam = getFieldParam(obj, field, typeMap,
                    innerTypeMap -> parse(ReflectUtil.getFieldValue(obj, field), innerTypeMap, depth + 1, ignoreFields),
                    ignoreFields
            );
            if (fieldParam == null) {
                continue;
            }

            InjectionFieldPo type = new InjectionFieldPo(fieldParam.getInjection());
            Map<Serializable, Object> valueMap = typeMap.getOrDefault(type, new ConcurrentHashMap<>(DEF_MAP_SIZE));
            valueMap.put(fieldParam.getQueryKey(), Collections.emptyMap());
            typeMap.put(type, valueMap);
        }
    }

    /**
     * 解析 list
     *
     * @param list         数据集合
     * @param typeMap      待查询的参数
     * @param ignoreFields 忽略注入的字段
     */
    private void parseList(Collection<?> list, Map<InjectionFieldPo, Map<Serializable, Object>> typeMap, int depth, String... ignoreFields) {
        for (Object item : list) {
            parse(item, typeMap, depth, ignoreFields);
        }
    }

    /**
     * 向obj对象的字段中注入值
     *
     * @param obj          当前对象
     * @param typeMap      数据
     * @param depth        当前递归深度
     * @param ignoreFields 忽略注入的字段
     */
    @SneakyThrows
    private void injection(Object obj, Map<InjectionFieldPo, Map<Serializable, Object>> typeMap, int depth, String... ignoreFields) {
        if (obj == null) {
            return;
        }
        if (depth > ips.getMaxDepth()) {
            log.info("出现循环依赖，最多执行 {} 次， 已执行 {} 次，已为您跳出循环", ips.getMaxDepth(), depth);
            return;
        }
        if (typeMap == null || typeMap.isEmpty()) {
            return;
        }

        if (obj instanceof IPage) {
            List<?> records = ((IPage<?>) obj).getRecords();
            injectionList(records, typeMap, ignoreFields);
            return;
        }
        if (obj instanceof Collection) {
            injectionList((Collection<?>) obj, typeMap, ignoreFields);
            return;
        }

        //解析方法上的注解，计算出obj对象中所有需要查询的数据
        Field[] fields = ReflectUtil.getFields(obj.getClass());
        for (Field field : fields) {
            FieldParam fieldParam = getFieldParam(obj, field, typeMap,
                    innerTypeMap -> injection(ReflectUtil.getFieldValue(obj, field), innerTypeMap, depth + 1, ignoreFields),
                    ignoreFields);
            if (fieldParam == null) {
                continue;
            }
            InjectionField inField = fieldParam.getInjection();
            Object queryKey = fieldParam.getQueryKey();
            Object fieldValue = fieldParam.getFieldValue();

            Object newVal = getNewVal(inField, queryKey, typeMap);
            if (newVal == null) {
                continue;
            }
            if (newVal instanceof Map && ((Map<?, ?>) newVal).isEmpty()) {
                continue;
            }

            // 将新的值 反射 到指定字段
            if (fieldValue instanceof RemoteData) {
                RemoteData remoteData = (RemoteData) fieldValue;

                // feign 接口序列化 丢失类型
                if (newVal instanceof Map && !Object.class.equals(inField.beanClass())) {
                    newVal = JsonUtil.parse(JsonUtil.toJson(newVal), inField.beanClass());
                }
                remoteData.setData(newVal);
            } else {
                ReflectUtil.setFieldValue(obj, field, newVal);
            }
        }
    }

    /**
     * 从 valueMap
     *
     * @param queryKey 处理后的查询值
     * @param typeMap  已查询后的集合
     * @return 已查询后的值
     */
    private Object getNewVal(InjectionField inField, Object queryKey, Map<InjectionFieldPo, Map<Serializable, Object>> typeMap) {
        InjectionFieldPo type = new InjectionFieldPo(inField);

        Map<Serializable, Object> valueMap = typeMap.get(type);

        if (CollUtil.isEmpty(valueMap)) {
            return null;
        }

        Object newVal = valueMap.get(queryKey);
        // 可能由于序列化原因导致 get 失败，重新尝试get
        if (ObjectUtil.isNull(newVal) && ObjectUtil.isNotEmpty(queryKey)) {
            newVal = valueMap.get(queryKey.toString());

            // 可能由于是多key原因导致get失败
            if (ObjectUtil.isNull(newVal) && StrUtil.contains(queryKey.toString(), ips.getDictItemSeparator())) {
                String[] typeCodes = StrUtil.split(queryKey.toString(), ips.getDictSeparator());
                String[] codes = StrUtil.split(typeCodes[1], ips.getDictItemSeparator());

                newVal = Arrays.stream(codes).map(item -> {
                    String val = valueMap.getOrDefault(typeCodes[0] + ips.getDictSeparator() + item, EMPTY).toString();
                    return val == null ? EMPTY : val;
                }).collect(Collectors.joining(ips.getDictItemSeparator()));
            }
        }
        return newVal;
    }

    /**
     * 注入 集合
     *
     * @param list         数据集合
     * @param typeMap      待查询的参数
     * @param ignoreFields 忽略注入的字段
     */
    private void injectionList(Collection<?> list, Map<InjectionFieldPo, Map<Serializable, Object>> typeMap, String... ignoreFields) {
        for (Object item : list) {
            injection(item, typeMap, 1, ignoreFields);
        }
    }

    /**
     * 提取参数
     *
     * @param obj          当前对象
     * @param field        当前字段
     * @param typeMap      待查询的集合
     * @param consumer     字段为复杂类型时的回调处理
     * @param ignoreFields 忽略注入的字段
     * @return 字段参数
     */
    private FieldParam getFieldParam(Object obj, Field field, Map<InjectionFieldPo, Map<Serializable, Object>> typeMap,
                                     Consumer<Map<InjectionFieldPo, Map<Serializable, Object>>> consumer, String... ignoreFields) {
        //是否标记@InjectionField注解
        InjectionField inField = field.getDeclaredAnnotation(InjectionField.class);
        if (inField == null) {
            return null;
        }
        // 是否排除
        if (ArrayUtil.contains(ignoreFields, field.getName())) {
            log.debug("已经忽略{}字段的解析", field.getName());
            return null;
        }
        field.setAccessible(true);
        //类型
        if (isNotBaseType(field)) {
            consumer.accept(typeMap);
            return null;
        }

        String api = inField.api();
        Class<?> feign = inField.apiClass();

        if (StrUtil.isEmpty(api) && Object.class.equals(feign)) {
            log.warn("忽略注入字段: {}.{}", field.getType(), field.getName());
            return null;
        }

        Object fieldValue = ReflectUtil.getFieldValue(obj, field);
        if (fieldValue == null) {
            log.debug("字段[{}]为空,跳过", field.getName());
            return null;
        }

        Serializable queryKey = getQueryKey(inField, fieldValue);
        if (ObjectUtil.isEmpty(queryKey)) {
            return null;
        }
        return new FieldParam(inField, queryKey, fieldValue);
    }


    /**
     * 获取查询用的key
     *
     * @param injectionField 当前字段标记的注解
     * @param fieldValue     当前字段的具体值
     * @return 从当前字段的值构造出，调用api#method方法的参数
     */
    private Serializable getQueryKey(InjectionField injectionField, Object fieldValue) {
        String key = injectionField.key();
        String dictType = injectionField.dictType();
        Serializable queryKey;
        if (StrUtil.isNotEmpty(key)) {
            queryKey = key;
        } else {
            if (fieldValue instanceof RemoteData) {
                RemoteData remoteData = (RemoteData) fieldValue;
                queryKey = (Serializable) remoteData.getKey();
            } else {
                queryKey = (Serializable) fieldValue;
            }
        }
        if (ObjectUtil.isNotEmpty(queryKey) && StrUtil.isNotEmpty(dictType)) {
            queryKey = StrUtil.join(ips.getDictSeparator(), dictType, queryKey);
        }
        return queryKey;
    }
}
