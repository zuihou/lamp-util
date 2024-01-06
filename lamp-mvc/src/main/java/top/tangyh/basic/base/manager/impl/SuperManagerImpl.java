package top.tangyh.basic.base.manager.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import top.tangyh.basic.base.manager.SuperManager;
import top.tangyh.basic.base.mapper.SuperMapper;
import top.tangyh.basic.exception.BizException;

import java.lang.reflect.ParameterizedType;

import static top.tangyh.basic.exception.code.ExceptionCode.SERVICE_MAPPER_ERROR;

/**
 * 不含缓存的Service实现
 * <p>
 * <p>
 * 2，removeById：重写 ServiceImpl 类的方法，删除db
 * 3，removeByIds：重写 ServiceImpl 类的方法，删除db
 * 4，updateAllById： 新增的方法： 修改数据（所有字段）
 * 5，updateById：重写 ServiceImpl 类的方法，修改db后
 *
 * @param <M> Mapper
 * @param <T> 实体
 * @author zuihou
 * @date 2020年02月27日18:15:17
 */
public class SuperManagerImpl<M extends SuperMapper<T>, T> extends ServiceImpl<M, T> implements SuperManager<T> {

    private Class<T> entityClass = null;

    public SuperMapper getSuperMapper() {
        if (baseMapper != null) {
            return baseMapper;
        }
        throw BizException.wrap(SERVICE_MAPPER_ERROR);
    }

    @Override
    public Class<T> getEntityClass() {
        if (entityClass == null) {
            this.entityClass = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        }
        return this.entityClass;
    }


    @Override
    public boolean updateAllById(T model) {
        return SqlHelper.retBool(getSuperMapper().updateAllById(model));
    }

}
