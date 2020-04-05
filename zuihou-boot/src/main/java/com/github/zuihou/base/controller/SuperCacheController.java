package com.github.zuihou.base.controller;

import com.github.zuihou.base.R;
import com.github.zuihou.base.service.SuperCacheService;
import com.github.zuihou.log.annotation.SysLog;
import com.github.zuihou.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.Serializable;

/**
 * SuperCacheController
 * <p>
 * 继承该类，在SuperController类的基础上扩展了以下方法：
 * 1，get ： 根据ID查询缓存，若缓存不存在，则查询DB
 *
 * @author zuihou
 * @date 2020年03月06日11:06:46
 */
public abstract class SuperCacheController<S extends SuperCacheService<Entity>, Id extends Serializable, Entity, PageDTO, SaveDTO, UpdateDTO>
        extends SuperController<S, Id, Entity, PageDTO, SaveDTO, UpdateDTO> {

    /**
     * 查询
     *
     * @param id 主键id
     * @return 查询结果
     */
    @Override
    @SysLog("'查询:' + #id")
    @PreAuth("hasPermit('{}view')")
    public R<Entity> get(@PathVariable Id id) {
        return success(baseService.getByIdCache(id));
    }

}
