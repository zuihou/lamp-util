package com.github.zuihou.scan.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 系统接口扫描配置
 *
 * @author zuihou
 * @date 2019/12/17
 */
@Data
@ConfigurationProperties(prefix = "zuihou.scan")
public class ScanProperties {

    private ScanPersistenceType type = ScanPersistenceType.FEIGN;
}
