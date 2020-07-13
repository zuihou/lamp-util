package com.github.zuihou.injection.core;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
                            new LinkedBlockingQueue<Runnable>())
            );
            this.caches = CacheBuilder.newBuilder()
                    .maximumSize(guavaCache.getMaximumSize())
                    .refreshAfterWrite(guavaCache.getRefreshWriteTime(), TimeUnit.MINUTES)
                    .build(new CacheLoader<InjectionFieldExtPo, Map<Serializable, Object>>() {
                        @Override
                        public Map<Serializable, Object> load(InjectionFieldExtPo type) throws Exception {
                            log.info("首次读取缓存: " + type);
                            return loadMap(type);
                        }

                        // 自动刷新缓存，防止脏数据
                        @Override
                        public ListenableFuture<Map<Serializable, Object>> reload(final InjectionFieldExtPo key, Map<Serializable, Object> oldValue) throws Exception {
                            return backgroundRefreshPools.submit(() -> {
                                BaseContextHandler.setTenant(key.getTenant());
                                return load(key);
                            });
                        }
                    });
        }
    }

    /**
     * 加载数据
     *
     * @param type
     * @return
     */
    private Map<Serializable, Object> loadMap(InjectionFieldExtPo type) {
        Object bean = null;
        if (StrUtil.isNotEmpty(type.getApi())) {
            bean = SpringUtils.getBean(type.getApi());
            log.info("建议在方法： [{}.{}]，上加入缓存，加速查询", type.getApi(), type.getMethod());
        } else {
            bean = SpringUtils.getBean(type.getApiClass());
            log.info("建议在方法： [{}.{}]，上加入缓存，加速查询", type.getApiClass().toString(), type.getMethod());
        }
        Map<Serializable, Object> value = ReflectUtil.invoke(bean, type.getMethod(), type.getKeys());
        return value;
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
     * @param obj        需要注入的对象、集合、IPage
     * @param isUseCache 是否使用guava缓存
     * @throws Exception
     */
    public void injection(Object obj, boolean isUseCache) {
        try {
            // key 为远程查询的对象
            // value 为 待查询的数据
            Map<InjectionFieldPo, Map<Serializable, Object>> typeMap = new HashMap();

            long parseStart = System.currentTimeMillis();
            //1. 通过反射将obj的字段上标记了@InjectionFiled注解的字段解析出来
            parse(obj, typeMap, 1);
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

    public void injection(Object obj) {
        injection(obj, true);
    }


    /**
     * 判断是否为基本类型
     *
     * @param obj
     * @param field 字段
     * @return
     */
    private boolean isNotBaseType(Object obj, Field field) {
        String typeName = field.getType().getName();
        if ("java.lang.Integer".equals(typeName) ||
                "java.lang.Byte".equals(typeName) ||
                "java.lang.Long".equals(typeName) ||
                "java.lang.Double".equals(typeName) ||
                "java.lang.Float".equals(typeName) ||
                "java.lang.Character".equals(typeName) ||
                "java.lang.Short".equals(typeName) ||
                "java.lang.Boolean".equals(typeName) ||
                "java.lang.String".equals(typeName) ||
                "com.github.zuihou.model.RemoteData".equals(typeName)
        ) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * 1，遍历字段，解析出数据
     * 2，遍历字段，设值
     *
     * @param obj     对象
     * @param typeMap 数据
     * @param depth   当前递归深度
     * @throws Exception
     */
    private void parse(Object obj, Map<InjectionFieldPo, Map<Serializable, Object>> typeMap, int depth) {
        if (obj == null) {
            return;
        }
        if (depth > ips.getMaxDepth()) {
            log.info("出现循环依赖，最多执行 {} 次， 已执行 {} 次，已为您跳出循环", ips.getMaxDepth(), depth);
            return;
        }
        if (typeMap == null) {
            typeMap = new HashMap();
        }

        if (obj instanceof IPage) {
            List records = ((IPage) obj).getRecords();
            parseList(records, typeMap, depth);
            return;
        }
        if (obj instanceof Collection) {
            parseList((Collection) obj, typeMap, depth);
            return;
        }

        //解析方法上的注解，计算出obj对象中所有需要查询的数据
        Field[] fields = ReflectUtil.getFields(obj.getClass());

        for (Field field : fields) {
            FieldParam fieldParam = getFieldParam(obj, field, typeMap,
                    (innerTypeMap) -> parse(ReflectUtil.getFieldValue(obj, field), innerTypeMap, depth + 1)
            );
            if (fieldParam == null) {
                continue;
            }

            InjectionFieldPo type = new InjectionFieldPo(fieldParam.getAnno());
            Map<Serializable, Object> valueMap = typeMap.getOrDefault(type, new HashMap());
            valueMap.put(fieldParam.getQueryKey(), null);
            typeMap.put(type, valueMap);
        }
    }

    /**
     * 解析 list
     *
     * @param list
     * @param typeMap
     * @throws Exception
     */
    private void parseList(Collection list, Map typeMap, int depth) {
        for (Object item : list) {
            parse(item, typeMap, depth);
        }
    }

    /**
     * 向obj对象的字段中注入值
     *
     * @param obj     当前对象
     * @param typeMap 数据
     * @param depth   当前递归深度
     * @throws Exception
     */
    @SneakyThrows
    private void injection(Object obj, Map<InjectionFieldPo, Map<Serializable, Object>> typeMap, int depth) {
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
            List records = ((IPage) obj).getRecords();
            injectionList((Collection) records, typeMap);
            return;
        }
        if (obj instanceof Collection) {
            injectionList((Collection) obj, typeMap);
            return;
        }

        //解析方法上的注解，计算出obj对象中所有需要查询的数据
        Field[] fields = ReflectUtil.getFields(obj.getClass());
        for (Field field : fields) {
            FieldParam fieldParam = getFieldParam(obj, field, typeMap,
                    (innerTypeMap) -> injection(ReflectUtil.getFieldValue(obj, field), innerTypeMap, depth + 1));
            if (fieldParam == null) {
                continue;
            }
            InjectionField anno = fieldParam.getAnno();
            Object queryKey = fieldParam.getQueryKey();
            Object curField = fieldParam.getCurField();

            InjectionFieldPo type = new InjectionFieldPo(anno);
            Map<Serializable, Object> valueMap = typeMap.get(type);

            if (valueMap == null || valueMap.isEmpty()) {
                continue;
            }

            Object newVal = valueMap.get(queryKey);
            if (ObjectUtil.isNull(newVal) && ObjectUtil.isNotEmpty(queryKey)) {
                newVal = valueMap.get(queryKey.toString());
            }

            if (curField instanceof RemoteData) {
                RemoteData remoteData = (RemoteData) curField;

                // feign 接口序列化 丢失类型
                if (newVal != null && newVal instanceof Map && !Object.class.equals(type.getBeanClass())) {
                    newVal = JsonUtil.parse(JsonUtil.toJson(newVal), type.getBeanClass());
                }
                remoteData.setData(newVal);
            } else {
                ReflectUtil.setFieldValue(obj, field, newVal);
            }
        }
    }


    /**
     * 注入 集合
     *
     * @param list
     * @param typeMap
     */
    private void injectionList(Collection list, Map typeMap) {
        for (Object item : list) {
            injection(item, typeMap, 1);
        }
    }

    /**
     * 提取参数
     *
     * @param obj
     * @param field
     * @param typeMap
     * @param consumer
     * @return
     */
    private FieldParam getFieldParam(Object obj, Field field, Map<InjectionFieldPo, Map<Serializable, Object>> typeMap, Consumer<Map<InjectionFieldPo, Map<Serializable, Object>>> consumer) {
        //是否使用MyAnno注解
        InjectionField anno = field.getDeclaredAnnotation(InjectionField.class);
        if (anno == null) {
            return null;
        }
        field.setAccessible(true);
        //类型
        if (isNotBaseType(obj, field)) {
            consumer.accept(typeMap);
            return null;
        }

        String api = anno.api();
        Class<?> feign = anno.apiClass();

        if (StrUtil.isEmpty(api) && Object.class.equals(feign)) {
            log.warn("忽略注入字段: {}.{}", field.getType(), field.getName());
            return null;
        }

        Object curField = ReflectUtil.getFieldValue(obj, field);
        if (curField == null) {
            log.debug("字段[{}]为空,跳过", field.getName());
            return null;
        }

        Serializable queryKey = getQueryKey(anno, curField);
        if (ObjectUtil.isEmpty(queryKey)) {
            return null;
        }
        return new FieldParam(anno, queryKey, curField);
    }


    /**
     * 获取查询用的key
     *
     * @param anno
     * @param curField
     * @return
     */
    private Serializable getQueryKey(InjectionField anno, Object curField) {
        String key = anno.key();
        String dictType = anno.dictType();
        Serializable queryKey;
        if (StrUtil.isNotEmpty(key)) {
            queryKey = key;
        } else {
            if (curField instanceof RemoteData) {
                RemoteData remoteData = (RemoteData) curField;
                queryKey = (Serializable) remoteData.getKey();
            } else {
                queryKey = (Serializable) curField;
            }
        }
        if (ObjectUtil.isNotEmpty(queryKey) && StrUtil.isNotEmpty(dictType)) {
            queryKey = StrUtil.join(ips.getDictSeparator(), dictType, queryKey);
        }
        return queryKey;
    }


}
