package top.tangyh.basic.log;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.ConfigBuilder;
import org.lionsoul.ip2region.service.Ip2Region;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import top.tangyh.basic.log.properties.OptLogProperties;

import java.io.InputStream;
import java.nio.file.Paths;

/**
 *
 * @author tangyh
 * @since 2026/5/18 16:54
 */
@Configuration
@AllArgsConstructor
@ConditionalOnWebApplication
@EnableConfigurationProperties(OptLogProperties.class)
@Slf4j
public class Ip2RegionAutoConfiguration {
    private final ResourceLoader resourceLoader;

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public Ip2Region ip2Region(OptLogProperties optLogProperties) throws Exception {
        Config v4Config = null;
        OptLogProperties.Ip2RegionProperties ipv4 = optLogProperties.getIpv4();
        if (ipv4.isEnabled()) {
            v4Config = buildConfig(ipv4, true);
        }

        Config v6Config = null;
        OptLogProperties.Ip2RegionProperties ipv6 = optLogProperties.getIpv6();
        if (ipv6.isEnabled()) {
            v6Config = buildConfig(ipv6, false);
        }

        return Ip2Region.create(v4Config, v6Config);
    }

    private Config buildConfig(OptLogProperties.Ip2RegionProperties p, boolean isV4) throws Exception {
        String path;
        Integer searchers;
        int cachePolicy;
        Integer cacheSliceBytes;
        Boolean fairLock;


        path = p.getXdbPath();
        searchers = p.getSearchers();
        cachePolicy = p.getCachePolicy();
        cacheSliceBytes = p.getCacheSliceBytes();
        fairLock = p.getFairLock();


        ConfigBuilder builder = Config.custom();

        // 缓存策略
        switch (cachePolicy) {
            case 1:
                builder.setCachePolicy(Config.VIndexCache);
                break;
            case 2:
                builder.setCachePolicy(Config.BufferCache);
                break;
            default:
                builder.setCachePolicy(Config.NoCache);
        }

        if (searchers != null) {
            builder.setSearchers(searchers);
        }
        if (cacheSliceBytes != null) {
            builder.setCacheSliceBytes(cacheSliceBytes);
        }
        if (fairLock != null) {
            builder.setFairLock(fairLock);
        }


        // 支持 classpath: 、jar内部、绝对路径
        if (StrUtil.startWith(path, URLUtil.CLASSPATH_URL_PREFIX)) {
            Resource resource = resourceLoader.getResource(path);
            if (resource.exists() && resource.isReadable()) {
                try (InputStream in = resource.getInputStream()) {
                    builder.setXdbInputStream(in);
                    // setXdbInputStream 仅方便使用者从 jar 包中加载 xdb 文件内容，这时 cachePolicy 只能设置为 Config.BufferCache
                    builder.setCachePolicy(Config.BufferCache);
                }
            }
        } else if (StrUtil.startWith(path, URLUtil.FILE_URL_PREFIX)) {
            String filePath = StrUtil.subAfter(path, URLUtil.FILE_URL_PREFIX, false);
            if (FileUtil.isAbsolutePath(filePath)) {
                builder.setXdbPath(filePath);
            } else {
                String systemPath = System.getProperty("user.dir");
                String xdbPath = Paths.get(systemPath, filePath).toString();
                log.info("最终路径: {}", xdbPath);
                builder.setXdbPath(xdbPath);
            }
        } else {
            throw new IllegalArgumentException("[路径必须以classpath:或file:开头] 无效的 xdb 路径:" + path);
        }

        return isV4 ? builder.asV4() : builder.asV6();
    }

}
