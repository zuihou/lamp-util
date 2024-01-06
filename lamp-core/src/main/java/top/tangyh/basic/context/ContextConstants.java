package top.tangyh.basic.context;

/**
 * 跟上下文常量工具类
 *
 * @author zuihou
 * @date 2018/12/21
 */
public final class ContextConstants {
    /**
     * lamp_defaults库
     */
    public static final Long DEF_TENANT_ID = 0L;
    public static final String DEF_TENANT_ID_STR = "0";
    /**
     * 内置租户
     */
    public static final String BUILT_IN_TENANT_ID_STR = "1";
    /**
     * 请求头中携带的 应用id key
     */
    public static final String APPLICATION_ID_KEY = "ApplicationId";
    /**
     * 请求头中携带的 token key
     */
    public static final String TOKEN_KEY = "Token";
    /**
     * 请求头中携带的 客户端信息 key
     */
    public static final String CLIENT_KEY = "Authorization";
    /**
     * 请求头中携带的 租户id
     */
    public static final String TENANT_ID_KEY = "TenantId";
    /**
     * JWT中封装的 用户id
     */
    public static final String JWT_KEY_USER_ID = "UserId";
    /**
     * JWT中封装的 员工id
     */
    public static final String JWT_KEY_EMPLOYEE_ID = "EmployeeId";
    public static final String JWT_KEY_COMPANY_ID = "CurrentCompanyId";
    public static final String JWT_KEY_TOP_COMPANY_ID = "CurrentTopCompanyId";
    public static final String JWT_KEY_DEPT_ID = "CurrentDeptId";
    /**
     * JWT中封装的 随机数
     */
    public static final String JWT_KEY_UUID = "Uuid";
    /**
     * 请求头和线程变量中的 base库 租户数据源标识
     */
    public static final String TENANT_BASE_POOL_NAME_HEADER = "lamp_base";
    /**
     * 请求头和线程变量中的 extend库 租户数据源标识
     */
    public static final String TENANT_EXTEND_POOL_NAME_HEADER = "lamp_extend";
    /**
     * 请求头和线程变量中的 企业ID
     */
    public static final String TENANT_ID_HEADER = TENANT_ID_KEY;
    /**
     * 请求头和线程变量中的 用户ID
     */
    public static final String USER_ID_HEADER = JWT_KEY_USER_ID;
    /**
     * 请求头和线程变量中的 员工ID
     */
    public static final String EMPLOYEE_ID_HEADER = JWT_KEY_EMPLOYEE_ID;
    /**
     * 请求头和线程变量中的 当前单位ID
     */
    public static final String CURRENT_COMPANY_ID_HEADER = JWT_KEY_COMPANY_ID;
    /**
     * 请求头和线程变量中的 当前所属的顶级公司ID
     */
    public static final String CURRENT_TOP_COMPANY_ID_HEADER = JWT_KEY_TOP_COMPANY_ID;
    /**
     * 请求头和线程变量中的 当前所属的部门ID
     */
    public static final String CURRENT_DEPT_ID_HEADER = JWT_KEY_DEPT_ID;
    /**
     * 请求头和线程变量中的 应用ID
     */
    public static final String APPLICATION_ID_HEADER = APPLICATION_ID_KEY;
    /**
     * 请求头和线程变量中的 前端页面地址栏#号后的路径
     */
    public static final String PATH_HEADER = "Path";
    /**
     * 请求头和线程变量中的 token
     */
    public static final String TOKEN_HEADER = TOKEN_KEY;
    /**
     * 请求头和线程变量中的 客户端id
     */
    public static final String CLIENT_ID_HEADER = "ClientId";
    /**
     * 是否boot项目
     */
    public static final String IS_BOOT = "boot";
    /**
     * 是否 内部调用项目
     */
    public static final String FEIGN = "x-feign";
    /**
     * 日志链路追踪id信息头
     */
    public static final String TRACE_ID_HEADER = "trace";
    /**
     * 灰度发布版本号
     */
    public static final String GRAY_VERSION = "gray_version";
    /**
     * WriteInterceptor 放行标志
     */
    public static final String PROCEED = "proceed";
    /**
     * WriteInterceptor 禁止执行标志
     */
    public static final String STOP = "stop";

    private ContextConstants() {
    }

}
