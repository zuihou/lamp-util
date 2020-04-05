package com.github.zuihou.base.service;

import java.io.Serializable;

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
}
