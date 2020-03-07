package com.github.zuihou.base.service;

import cn.hutool.core.collection.CollUtil;
import com.github.zuihou.base.R;
import com.github.zuihou.base.entity.SuperEntity;
import com.github.zuihou.base.mapper.SuperMapper;
import net.oschina.j2cache.CacheChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.io.Serializable;
import java.util.Collection;

/**
 * 基于SpringCache + J2Cache 实现的 缓存实现
 * key规则： CacheConfig#cacheNames:id
 * <p>
 * CacheConfig#cacheNames 会先从子类获取，子类没设置，就从SuperServiceCacheImpl类获取
 * <p>
 * 1，getByIdCache：新增的方法： 先查缓存，在查db
 * 2，removeById：重写 ServiceImpl 类的方法，删除db后，淘汰缓存
 * 3，removeByIds：重写 ServiceImpl 类的方法，删除db后，淘汰缓存
 * 4，updateAllById： 新增的方法： 修改数据（所有字段）后，淘汰缓存
 * 5，updateById：重写 ServiceImpl 类的方法，修改db后，淘汰缓存
 *
 * @param <M>
 * @param <T>
 * @author zuihou
 * @date 2020年02月27日18:15:17
 */
@CacheConfig(cacheNames = SuperCacheServiceImpl.CACHE_NAMES)
public abstract class SuperCacheServiceImpl<M extends SuperMapper<T>, T> extends SuperServiceImpl<M, T> implements SuperCacheService<T> {

    protected final static String CACHE_NAMES = "default";

    @Autowired
    protected CacheChannel cacheChannel;

    /**
     * 缓存的 region,
     * 这个值一定要全类型唯一，否则会跟其他缓存冲突
     * 记得重写该类！
     *
     * @return
     */
    protected abstract String getRegion();

    @Override
    @Cacheable(key = "#id")
    public T getByIdCache(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        if (CollUtil.isEmpty(idList)) {
            return true;
        }
        boolean flag = super.removeByIds(idList);

        String[] keys = idList.stream().map(id -> key(id)).toArray(String[]::new);
        cacheChannel.evict(getRegion(), keys);
        return flag;
    }

    @Override
    public boolean save(T model) {
        R<Boolean> result = handlerSave(model);
        if (result.getDefExec()) {
            boolean save = super.save(model);
            if (model instanceof SuperEntity) {
                String key = key(((SuperEntity) model).getId());
                cacheChannel.set(getRegion(), key, model);
            }
            return save;
        }
        return result.getData();
    }


    @Override
    @CacheEvict(key = "#p0.id")
    public boolean updateAllById(T model) {
        return super.updateAllById(model);
    }

    @Override
    @CacheEvict(key = "#p0.id")
    public boolean updateById(T model) {
        return super.updateById(model);
    }

}
