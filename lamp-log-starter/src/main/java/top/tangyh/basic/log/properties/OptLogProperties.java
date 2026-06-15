package top.tangyh.basic.log.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import top.tangyh.basic.constant.Constants;

import static top.tangyh.basic.log.properties.OptLogProperties.PREFIX;

/**
 * 操作日志配置类
 *
 * @author zuihou
 * @date 2020年03月09日15:00:47
 */
@ConfigurationProperties(prefix = PREFIX)
@Data
@NoArgsConstructor
public class OptLogProperties {
    public static final String PREFIX = Constants.PROJECT_PREFIX + ".log";

    /**
     * 是否启用
     */
    private Boolean enabled = true;

    /**
     * 日志存储类型
     */
    private OptLogType type = OptLogType.DB;

    private Ip2RegionProperties ipv4 = new Ip2RegionProperties();
    private Ip2RegionProperties ipv6 = new Ip2RegionProperties();

    @Data
    public static class Ip2RegionProperties {
        private boolean enabled = true;

        /**
         * 指定缓存策略: NoCache / VIndexCache / BufferCache
         */
        private int cachePolicy = 1;
        /**
         * 初始化的查询器数量
         */
        private Integer searchers = 20;
        /**
         * xdb 文件的路径
         * classpath:ip2region_v4.xdb      打包后读取boot-server/resources目录中的文件
         * file:/Users/xxx/ip2region_v4.xdb       测试或生产环境，存放在服务器的 绝对地址路径
         * file:./docs/ip2region_v4.xdb     本地环境，存放在IDE打开项目后的 相对地址路径
         */
        private String xdbPath;
        /**
         * 缓存的分片字节数，默认为 50MiB
         */
        private Integer cacheSliceBytes = 50;
        /**
         * ReentrantLock 是否使用公平锁
         */
        private Boolean fairLock;
    }
}
