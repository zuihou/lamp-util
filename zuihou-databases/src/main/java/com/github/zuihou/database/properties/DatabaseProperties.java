package com.github.zuihou.database.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.zuihou.database.properties.DatabaseProperties.PREFIX;
import static com.github.zuihou.database.properties.MultiTenantType.SCHEMA;


/**
 * 客户端认证配置
 *
 * @author zuihou
 * @date 2018/11/20
 */
@ConfigurationProperties(prefix = PREFIX)
@Data
@NoArgsConstructor
public class DatabaseProperties {
    public static final String PREFIX = "zuihou.database";
    /**
     * 攻击 SQL 阻断解析器
     *
     * @return
     */
    public Boolean isBlockAttack = false;
    /**
     * 是否启用 seata
     */
    public Boolean isSeata = false;
    /**
     * 分页大小限制
     */
    protected long limit = -1;
    /**
     * 是否禁止写入
     */
    private Boolean isNotWrite = false;
    /**
     * 是否启用数据权限
     */
    private Boolean isDataScope = true;
    /**
     * 事务超时时间
     */
    private int txTimeout = 60 * 60;

    /**
     * 租户库 前缀
     */
    private String tenantDatabasePrefix = "zuihou_base";

    /**
     * 多租户模式
     */
    private MultiTenantType multiTenantType = SCHEMA;
    /**
     * 租户id 列名
     */
    private String tenantIdColumn = "tenant_code";

    /**
     * Id 生成策略
     */
    private Id id = new Id();

    /**
     * 统一管理事务的方法名
     */
    private List<String> transactionAttributeList = new ArrayList<>(Arrays.asList("add*", "save*", "insert*",
            "create*", "update*", "edit*", "upload*", "delete*", "remove*",
            "clean*", "recycle*", "batch*", "mark*", "disable*", "enable*", "handle*", "syn*",
            "reg*", "gen*", "*Tx"
    ));
    /**
     * 事务扫描基础包
     */
    private String transactionScanPackage = "com.github.zuihou";

    @Data
    public static class Id {
        /**
         * 终端ID (0-31)      单机配置0 即可。 集群部署，根据情况每个实例自增即可。
         */
        private Long workerId = 0L;
        /**
         * 数据中心ID (0-31)   单机配置0 即可。 集群部署，根据情况每个实例自增即可。
         */
        private Long dataCenterId = 0L;
    }

}
