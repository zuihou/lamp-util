package top.tangyh.basic.context;

import cn.hutool.core.convert.Convert;
import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.val;
import top.tangyh.basic.utils.StrPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 获取当前线程变量中的 用户id、用户昵称、租户编码、账号等信息
 *
 * @author zuihou
 * @date 2017-12-13 16:52
 */
public final class ContextUtil {
    private ContextUtil() {
    }

    /**
     * 支持多线程传递参数
     *
     * @author tangyh
     * @date 2021/6/23 9:26 下午
     * @create [2021/6/23 9:26 下午 ] [tangyh] [初始创建]
     */
    private static final ThreadLocal<Map<String, String>> THREAD_LOCAL = new ThreadLocal<>();

    public static void putAll(Map<String, String> map) {
        map.forEach((k, v) -> {
            set(k, v);
        });
    }

    public static void set(String key, Object value) {
        Map<String, String> map = getLocalMap();
        map.put(key, value == null ? StrPool.EMPTY : value.toString());
    }

    public static <T> T get(String key, Class<T> type) {
        Map<String, String> map = getLocalMap();
        return Convert.convert(type, map.get(key));
    }

    public static <T> T get(String key, Class<T> type, Object def) {
        Map<String, String> map = getLocalMap();
        return Convert.convert(type, map.getOrDefault(key, String.valueOf(def == null ? StrPool.EMPTY : def)));
    }

    public static String get(String key) {
        Map<String, String> map = getLocalMap();
        return map.getOrDefault(key, StrPool.EMPTY);
    }

    public static Map<String, String> getLocalMap() {
        Map<String, String> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new ConcurrentHashMap<>(10);
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    public static void setLocalMap(Map<String, String> localMap) {
        THREAD_LOCAL.set(localMap);
    }


    /**
     * 是否boot项目
     *
     * @return 是否boot项目
     */
    public static Boolean getBoot() {
        return get(ContextConstants.IS_BOOT, Boolean.class, false);
    }

    public static void setBoot(Boolean val) {
        set(ContextConstants.IS_BOOT, val);
    }

    /**
     * 用户ID
     *
     * @return 用户ID
     */
    public static Long getUserId() {
        return get(ContextConstants.JWT_KEY_USER_ID, Long.class, 0L);
    }

    public static String getUserIdStr() {
        return String.valueOf(getUserId());
    }

    /**
     * 用户ID
     *
     * @param userId 用户ID
     */
    public static void setUserId(Long userId) {
        set(ContextConstants.JWT_KEY_USER_ID, userId);
    }

    public static void setUserId(String userId) {
        set(ContextConstants.JWT_KEY_USER_ID, userId);
    }

    /**
     * 登录账号
     *
     * @return 登录账号
     */
    public static String getAccount() {
        return get(ContextConstants.JWT_KEY_ACCOUNT, String.class);
    }

    /**
     * 登录账号
     *
     * @param account 登录账号
     */
    public static void setAccount(String account) {
        set(ContextConstants.JWT_KEY_ACCOUNT, account);
    }


    /**
     * 用户姓名
     *
     * @return 用户姓名
     */
    public static String getName() {
        return get(ContextConstants.JWT_KEY_NAME, String.class);
    }

    /**
     * 用户姓名
     *
     * @param name 用户姓名
     */
    public static void setName(String name) {
        set(ContextConstants.JWT_KEY_NAME, name);
    }

    /**
     * 获取token
     *
     * @return token
     */
    public static String getToken() {
        return get(ContextConstants.BEARER_HEADER_KEY, String.class);
    }

    public static void setToken(String token) {
        set(ContextConstants.BEARER_HEADER_KEY, token);
    }

    /**
     * 获取租户编码
     *
     * @return 租户编码
     */
    public static String getTenant() {
        return get(ContextConstants.JWT_KEY_TENANT, String.class, StrPool.EMPTY);
    }

    public static void setTenant(String val) {
        set(ContextConstants.JWT_KEY_TENANT, val);
        setTenantBasePoolName(val);
        setTenantExtendPoolName(val);
    }

    public static void clearDatabase() {
        set(ContextConstants.DATABASE, StrPool.EMPTY);
    }
    public static String getDatabase() {
        return get(ContextConstants.DATABASE, String.class, StrPool.EMPTY);
    }
    public static void setDatabaseBase() {
        set(ContextConstants.DATABASE, ContextConstants.TENANT_BASE_POOL_NAME_HEADER);
    }

    public static void setDatabaseExtend() {
        set(ContextConstants.DATABASE, ContextConstants.TENANT_EXTEND_POOL_NAME_HEADER);
    }


    /**
     * 切换base库
     *
     * @param tenant
     */
    public static void setTenantBasePoolName(Object tenant) {
        set(ContextConstants.TENANT_BASE_POOL_NAME_HEADER, tenant);
    }

    /**
     * 切换extend库
     *
     * @param tenant
     */
    public static void setTenantExtendPoolName(Object tenant) {
        set(ContextConstants.TENANT_EXTEND_POOL_NAME_HEADER, tenant);
    }


    /**
     * 设置默认库
     */
    public static void setDefTenant() {
        set(ContextConstants.TENANT_BASE_POOL_NAME_HEADER, ContextConstants.DEF_TENANT);
        set(ContextConstants.TENANT_EXTEND_POOL_NAME_HEADER, ContextConstants.DEF_TENANT);
    }

    public static boolean isDefTenantId() {
        String base = get(ContextConstants.TENANT_BASE_POOL_NAME_HEADER);
        if (ContextConstants.DEF_TENANT.equals(base)) {
            return true;
        }
        String extend = get(ContextConstants.TENANT_EXTEND_POOL_NAME_HEADER);
        if (ContextConstants.DEF_TENANT.equals(extend)) {
            return true;
        }
        return false;
    }


    public static String getSubTenant() {
        return get(ContextConstants.JWT_KEY_SUB_TENANT, String.class, StrPool.EMPTY);
    }

    public static void setSubTenant(String val) {
        set(ContextConstants.JWT_KEY_SUB_TENANT, val);
    }

    public static String getClientId() {
        return get(ContextConstants.JWT_KEY_CLIENT_ID, String.class);
    }

    public static void setClientId(String val) {
        set(ContextConstants.JWT_KEY_CLIENT_ID, val);
    }

    /**
     * 地址栏路径
     */
    public static String getPath() {
        return get(ContextConstants.PATH_HEADER, String.class, StrPool.EMPTY);
    }

    /**
     * 地址栏路径
     *
     * @param path 地址栏路径
     */
    public static void setPath(Object path) {
        set(ContextConstants.PATH_HEADER, path == null ? StrPool.EMPTY : path);
    }

    /**
     * 获取灰度版本号
     *
     * @return 灰度版本号
     */
    public static String getGrayVersion() {
        return get(ContextConstants.GRAY_VERSION, String.class);
    }

    public static void setGrayVersion(String val) {
        set(ContextConstants.GRAY_VERSION, val);
    }

    public static boolean isEmptyTenant() {
        return isEmptyStr(ContextConstants.JWT_KEY_TENANT);
    }

    private static boolean isEmptyLong(String key) {
        String val = getLocalMap().get(key);
        return val == null || StrPool.NULL.equals(val) || StrPool.ZERO.equals(val);
    }

    private static boolean isEmptyStr(String key) {
        String val = getLocalMap().get(key);
        return val == null || StrPool.NULL.equals(val);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }


}
