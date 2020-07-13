package org.springframework.cloud.openfeign;

import feign.Feign.Builder;
import feign.Target.HardCodedTarget;

/**
 * 覆盖 spring-cloud-openfeign-core 包的 Targeter 列
 *
 * @author zuihou
 */
public interface Targeter {
    /**
     * target
     *
     * @param factory
     * @param feign
     * @param context
     * @param target
     * @param <T>
     * @return T
     */
    <T> T target(FeignClientFactoryBean factory, Builder feign, FeignContext context, HardCodedTarget<T> target);
}
