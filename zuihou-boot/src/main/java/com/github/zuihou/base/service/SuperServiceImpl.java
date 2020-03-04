package com.github.zuihou.base.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.zuihou.base.mapper.SuperMapper;
import com.github.zuihou.exception.BizException;
import com.github.zuihou.exception.code.ExceptionCode;

import java.io.Serializable;
import java.util.Collection;

import static com.github.zuihou.exception.code.ExceptionCode.SERVICE_MAPPER_ERROR;

/**
 * 不含缓存的Service实现
 * <p>
 * <p>
 * 2，removeById：重写 ServiceImpl 类的方法，删除db
 * 3，removeByIds：重写 ServiceImpl 类的方法，删除db
 * 4，updateAllById： 新增的方法： 修改数据（所有字段）
 * 5，updateById：重写 ServiceImpl 类的方法，修改db后
 *
 * @param <M>
 * @param <T>
 * @author zuihou
 * @date 2020年02月27日18:15:17
 */
public class SuperServiceImpl<M extends SuperMapper<T>, T> extends ServiceImpl<M, T> implements SuperService<T> {

    public SuperMapper getSuperMapper() {
        if (baseMapper instanceof SuperMapper) {
            return baseMapper;
        }
        throw BizException.wrap(SERVICE_MAPPER_ERROR);
    }

    @Override
    public boolean save(T model) {
        boolean bool = super.save(model);
        if (bool) {
            if (!handlerSave(model)) {
                throw BizException.wrap(ExceptionCode.DATA_SAVE_ERROR);
            }
        }
        return bool;
    }

    /**
     * 处理新增相关处理
     *
     * @param model
     * @return
     */
    protected boolean handlerSave(T model) {
        return true;
    }

    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        if (CollUtil.isEmpty(idList)) {
            return true;
        }
        return super.removeByIds(idList);
    }


    @Override
    public boolean updateAllById(T model) {
        boolean bool = SqlHelper.retBool(getSuperMapper().updateAllById(model));

        if (bool) {
            if (!handlerUpdateAllById(model)) {
                throw BizException.wrap(ExceptionCode.DATA_UPDATE_ERROR);
            }
        }
        return bool;
    }

    @Override
    public boolean updateById(T model) {
        boolean bool = super.updateById(model);
        if (bool) {
            if (!handlerUpdateById(model)) {
                throw BizException.wrap(ExceptionCode.DATA_UPDATE_ERROR);
            }
        }
        return bool;
    }

    /**
     * 处理修改相关处理
     *
     * @param model
     * @return
     */
    protected boolean handlerUpdateAllById(T model) {
        return true;
    }

    /**
     * 处理修改相关处理
     *
     * @param model
     * @return
     */
    protected boolean handlerUpdateById(T model) {
        return true;
    }

}
