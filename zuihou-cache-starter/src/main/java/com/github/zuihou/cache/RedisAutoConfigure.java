package com.github.zuihou.cache;

import com.github.zuihou.cache.lock.RedisDistributedLock;
import com.github.zuihou.cache.properties.CustomCacheProperties;
import com.github.zuihou.cache.redis.RedisOps;
import com.github.zuihou.cache.repository.CacheOps;
import com.github.zuihou.cache.repository.CachePlusOps;
import com.github.zuihou.cache.repository.impl.RedisOpsImpl;
import com.github.zuihou.cache.utils.RedisObjectSerializer;
import com.github.zuihou.lock.DistributedLock;
import com.github.zuihou.utils.StrPool;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;


/**
 * redis 配置类
 *
 * @author zuihou
 * @date 2019-08-06 10:42
 */
@ConditionalOnClass(RedisConnectionFactory.class)
@ConditionalOnProperty(prefix = CustomCacheProperties.PREFIX, name = "type", havingValue = "REDIS", matchIfMissing = true)
@EnableConfigurationProperties({RedisProperties.class, CustomCacheProperties.class})
public class RedisAutoConfigure {
    @Autowired
    private CustomCacheProperties cacheProperties;

    /**
     * 分布式锁
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public DistributedLock RedisDistributedLock(RedisTemplate<String, Object> redisTemplate) {
        return new RedisDistributedLock(redisTemplate);
    }

    /**
     * RedisTemplate配置
     *
     * @param factory
     */
    @Bean
    @ConditionalOnMissingBean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        RedisObjectSerializer redisObjectSerializer = new RedisObjectSerializer();
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(redisObjectSerializer);
        redisTemplate.setValueSerializer(redisObjectSerializer);
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

    /**
     * redis 持久库
     *
     * @param redisOps the redis template
     * @return the redis repository
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheOps cacheOps(RedisOps redisOps) {
        return new RedisOpsImpl(redisOps);
    }

    /**
     * redis 增强持久库
     *
     * @param redisOps the redis template
     * @return the redis repository
     */
    @Bean
    @ConditionalOnMissingBean
    public CachePlusOps cachePlusOps(RedisOps redisOps) {
        return new RedisOpsImpl(redisOps);
    }

    /**
     * 用于 @Cacheable 相关注解
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean(name = "cacheManager")
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defConfig = getDefConf();
        defConfig.entryTtl(cacheProperties.getDef().getTimeToLive());

        Map<String, CustomCacheProperties.Cache> configs = cacheProperties.getConfigs();
        Map<String, RedisCacheConfiguration> map = Maps.newHashMap();
        //自定义的缓存过期时间配置
        Optional.ofNullable(configs).ifPresent((config) -> {
            config.forEach((key, cache) -> {
                RedisCacheConfiguration cfg = handleRedisCacheConfiguration(cache, defConfig);
                map.put(key, cfg);
            });
        });

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defConfig)
                .withInitialCacheConfigurations(map)
                .build();
    }

    private RedisCacheConfiguration getDefConf() {
        RedisCacheConfiguration def = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new RedisObjectSerializer()));
        return handleRedisCacheConfiguration(cacheProperties.getDef(), def);
    }

    private RedisCacheConfiguration handleRedisCacheConfiguration(CustomCacheProperties.Cache redisProperties, RedisCacheConfiguration config) {
        if (Objects.isNull(redisProperties)) {
            return config;
        }
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.computePrefixWith(cacheName -> redisProperties.getKeyPrefix().concat(StrPool.COLON).concat(cacheName).concat(StrPool.COLON));
        } else {
            config = config.computePrefixWith(cacheName -> cacheName.concat(StrPool.COLON));
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }

        return config;
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisOps getRedisOps(RedisTemplate<String, Object> redisTemplate) {
        return new RedisOps(redisTemplate, cacheProperties.getCacheNullVal());
    }
}
