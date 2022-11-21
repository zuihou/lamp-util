package top.tangyh.basic.database.mybatis.auth;

import top.tangyh.basic.model.database.DataScope;

/**
 * 数据权限保证类
 *
 * @author tangyh
 * @version v1.0
 * @date 2022/9/16 9:33 PM
 * @create [2022/9/16 9:33 PM ] [tangyh] [初始创建]
 */
public class DataScopeHelper {
    protected static final ThreadLocal<DataScope> LOCAL_DATA_SCOPE = new ThreadLocal<>();

    /**
     * 获取 数据权限字段配置 参数
     *
     * @return
     */
    public static DataScope getLocalDataScope() {
        return LOCAL_DATA_SCOPE.get();
    }

    /**
     * 设置 数据权限字段配置
     *
     * @param dataScope
     */
    protected static void setLocalDataScope(DataScope dataScope) {
        LOCAL_DATA_SCOPE.set(dataScope);
    }

    /**
     * 移除本地变量
     */
    public static void clearDataScope() {
        LOCAL_DATA_SCOPE.remove();
    }

    /**
     * 开启数据权限
     *
     * @param dataScope sql中需要动态拼接条件的表的别名
     * @author tangyh
     * @date 2022/9/16 9:52 PM
     * @create [2022/9/16 9:52 PM ] [tangyh] [初始创建]
     */
    public static void startDataScope(DataScope dataScope) {
        if (dataScope == null) {
            return;
        }
        setLocalDataScope(dataScope);
    }
}
