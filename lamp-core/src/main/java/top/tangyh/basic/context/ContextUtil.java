package top.tangyh.basic.context;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
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

    /**
     * 支持多线程传递参数
     *
     * @author tangyh
     * @date 2021/6/23 9:26 下午
     * @create [2021/6/23 9:26 下午 ] [tangyh] [初始创建]
     */
    private static final ThreadLocal<Map<String, String>> THREAD_LOCAL = new ThreadLocal<>();
//    private static final ThreadLocal<Map<String, String>> THREAD_LOCAL = new TransmittableThreadLocal<>();  // 配合 @Async 使用时会有问题

    private ContextUtil() {
    }

    public static void putAll(Map<String, String> map) {
        map.forEach(ContextUtil::set);
    }

    public static void set(String key, Object value) {
        if (ObjectUtil.isEmpty(value) || StrUtil.isBlankOrUndefined(value.toString())) {
            return;
        }
        Map<String, String> map = getLocalMap();
        map.put(key, value.toString());
    }

    public static <T> T get(String key, Class<T> type) {
        Map<String, String> map = getLocalMap();
        return Convert.convert(type, map.get(key));
    }

    public static <T> T get(String key, Class<T> type, Object def) {
        Map<String, String> map = getLocalMap();
        String value;
        if (def == null) {
            value = map.get(key);
        } else {
            value = map.getOrDefault(key, String.valueOf(def));
        }
        return Convert.convert(type, StrUtil.isEmpty(value) ? def : value);
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
     * 用户ID
     *
     * @return 用户ID
     */
    public static Long getUserId() {
        return get(ContextConstants.USER_ID_HEADER, Long.class);
    }

    /**
     * 用户ID
     *
     * @param userId 用户ID
     */
    public static void setUserId(Object userId) {
        set(ContextConstants.USER_ID_HEADER, userId);
    }

    /**
     * 员工id
     */
    public static Long getEmployeeId() {
        return get(ContextConstants.EMPLOYEE_ID_HEADER, Long.class);
    }

    /**
     * 员工id
     *
     * @param employeeId 员工id
     */
    public static void setEmployeeId(Object employeeId) {
        set(ContextConstants.EMPLOYEE_ID_HEADER, employeeId);
    }

    public static boolean isEmptyUserId() {
        return isEmptyLong(ContextConstants.USER_ID_HEADER);
    }

    public static boolean isEmptyEmployeeId() {
        return isEmptyLong(ContextConstants.EMPLOYEE_ID_HEADER);
    }

    public static boolean isEmptyApplicationId() {
        return isEmptyLong(ContextConstants.APPLICATION_ID_HEADER);
    }


    /**
     * 应用ID
     */
    public static Long getApplicationId() {
        return get(ContextConstants.APPLICATION_ID_HEADER, Long.class);
    }

    /**
     * 应用ID
     *
     * @param applicationId 应用ID
     */
    public static void setApplicationId(Object applicationId) {
        set(ContextConstants.APPLICATION_ID_HEADER, applicationId);
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
     * 获取token
     *
     * @return token
     */
    public static String getToken() {
        return get(ContextConstants.TOKEN_HEADER, String.class);
    }

    public static void setToken(String token) {
        set(ContextConstants.TOKEN_HEADER, token == null ? StrPool.EMPTY : token);
    }

    /**
     * 获取 当前所属的公司ID
     *
     * @return java.lang.Long
     * @author tangyh
     * @date 2022/9/9 4:50 PM
     * @create [2022/9/9 4:50 PM ] [tangyh] [初始创建]
     */
    public static Long getCurrentCompanyId() {
        return get(ContextConstants.CURRENT_COMPANY_ID_HEADER, Long.class);
    }

    public static void setCurrentCompanyId(Object val) {
        set(ContextConstants.CURRENT_COMPANY_ID_HEADER, val);
    }

    /**
     * 获取 当前所属的顶级公司ID
     *
     * @return java.lang.Long
     * @author tangyh
     * @date 2022/9/9 4:50 PM
     * @create [2022/9/9 4:50 PM ] [tangyh] [初始创建]
     */
    public static Long getCurrentTopCompanyId() {
        return get(ContextConstants.CURRENT_TOP_COMPANY_ID_HEADER, Long.class);
    }

    public static void setCurrentTopCompanyId(Object val) {
        set(ContextConstants.CURRENT_TOP_COMPANY_ID_HEADER, val);
    }

    /**
     * 获取 当前所属的部门ID
     *
     * @return java.lang.Long
     * @author tangyh
     * @date 2022/9/9 4:50 PM
     * @create [2022/9/9 4:50 PM ] [tangyh] [初始创建]
     */
    public static Long getCurrentDeptId() {
        return get(ContextConstants.CURRENT_DEPT_ID_HEADER, Long.class);
    }

    public static void setCurrentDeptId(Object val) {
        set(ContextConstants.CURRENT_DEPT_ID_HEADER, val);
    }

    public static String getClientId() {
        return get(ContextConstants.CLIENT_ID_HEADER, String.class);
    }

    public static void setClientId(String val) {
        set(ContextConstants.CLIENT_ID_HEADER, val);
    }

    private static boolean isEmptyLong(String key) {
        String val = getLocalMap().get(key);
        return StrUtil.isEmpty(val) || StrPool.NULL.equals(val) || StrPool.ZERO.equals(val);
    }

    private static boolean isEmptyStr(String key) {
        String val = getLocalMap().get(key);
        return val == null || StrPool.NULL.equals(val);
    }

    public static String getLogTraceId() {
        return get(ContextConstants.TRACE_ID_HEADER, String.class);
    }

    public static void setLogTraceId(String val) {
        set(ContextConstants.TRACE_ID_HEADER, val);
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

    /**
     * 仅用于演示环境禁止执行某些操作
     * 后续sql是否可以执行
     */
    public static Boolean isProceed() {
        String proceed = get(ContextConstants.PROCEED, String.class);
        return StrUtil.isNotEmpty(proceed);
    }

    public static void setProceed() {
        set(ContextConstants.PROCEED, StrPool.ONE);
    }

    /**
     * 仅用于演示环境禁止执行某些操作
     * 后续sql是否不能执行
     */
    public static Boolean isStop() {
        String proceed = get(ContextConstants.STOP, String.class);
        return StrUtil.isNotEmpty(proceed);
    }

    public static void setStop() {
        set(ContextConstants.STOP, StrPool.ONE);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }

}
