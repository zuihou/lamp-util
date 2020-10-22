package com.github.zuihou.base.service;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 基于MP的 IService 新增了3个方法： getByIdCache
 * 其中：
 * 1，getByIdCache 方法 会先从缓存查询，后从DB查询 （取决于实现类）
 * 2、SuperService 上的方法
 *
 * @param <T> 实体
 * @author zuihuo
 * @date 2020年03月03日20:49:03
 */
public interface SuperCacheService<T> extends SuperService<T> {

    /**
     * 根据id 先查缓存，再查db
     *
     * @param id
     * @return
     */
    T getByIdCache(Serializable id);

    /**
     * 根据 region 和 key 查询缓存中存放的id，缓存不存在根据loader加载并写入数据，然后根据查询出来的id查询 实体
     *
     * @param region
     * @param key
     * @param loader
     * @return
     */
    T getByKey(String region, String key, Function<String, Object> loader);


    /**
     * 刷新缓存
     */
    void refreshCache();

    /**
     * 清理
     */
    void clearCache();
}
