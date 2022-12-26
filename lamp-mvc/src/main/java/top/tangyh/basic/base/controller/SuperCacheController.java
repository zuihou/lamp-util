package top.tangyh.basic.base.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import top.tangyh.basic.annotation.log.SysLog;
import top.tangyh.basic.annotation.security.PreAuth;
import top.tangyh.basic.base.R;
import top.tangyh.basic.base.service.SuperCacheService;

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
public abstract class SuperCacheController<S extends SuperCacheService<Entity>, Id extends Serializable, Entity, PageQuery, SaveDTO, UpdateDTO>
        extends SuperController<S, Id, Entity, PageQuery, SaveDTO, UpdateDTO> {

    /**
     * 查询
     *
     * @param id 主键id
     * @return 查询结果
     */
    @Override
    @SysLog("'查询:' + #id")
    @PreAuth("hasAnyPermission('{}view')")
    public R<Entity> get(@PathVariable Id id) {
        return success(baseService.getByIdCache(id));
    }


    /**
     * 刷新缓存
     *
     * @return 是否成功
     */
    @Operation(summary = "刷新缓存", description = "刷新缓存")
    @PostMapping("refreshCache")
    @SysLog("'刷新缓存'")
    @PreAuth("hasAnyPermission('{}add')")
    public R<Boolean> refreshCache() {
        baseService.refreshCache();
        return success(true);
    }

    /**
     * 清理缓存
     *
     * @return 是否成功
     */
    @Operation(summary = "清理缓存", description = "清理缓存")
    @PostMapping("clearCache")
    @SysLog("'清理缓存'")
    @PreAuth("hasAnyPermission('{}add')")
    public R<Boolean> clearCache() {
        baseService.clearCache();
        return success(true);
    }
}
