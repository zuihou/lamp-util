package top.tangyh.basic.base.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.springframework.transaction.annotation.Transactional;
import top.tangyh.basic.base.R;
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
public class SuperServiceImpl<M extends SuperMapper<T>, T> extends ServiceImpl<M, T> implements SuperService<T> {

    private Class<T> entityClass = null;

    public SuperMapper getSuperMapper() {
        if (baseMapper instanceof SuperMapper) {
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
    @Transactional(rollbackFor = Exception.class)
    public boolean save(T model) {
        R<Boolean> result = handlerSave(model);
        if (result.getDefExec()) {
            return super.save(model);
        }
        return result.getData();
    }

    /**
     * 处理新增相关处理
     *
     * @param model 实体
     * @return 是否成功
     */
    protected R<Boolean> handlerSave(T model) {
        return R.successDef();
    }

    /**
     * 处理修改相关处理
     *
     * @param model 实体
     * @return 是否成功
     */
    protected R<Boolean> handlerUpdateAllById(T model) {
        return R.successDef();
    }

    /**
     * 处理修改相关处理
     *
     * @param model 实体
     * @return 是否成功
     */
    protected R<Boolean> handlerUpdateById(T model) {
        return R.successDef();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAllById(T model) {
        R<Boolean> result = handlerUpdateAllById(model);
        if (result.getDefExec()) {
            return SqlHelper.retBool(getSuperMapper().updateAllById(model));
        }
        return result.getData();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(T model) {
        R<Boolean> result = handlerUpdateById(model);
        if (result.getDefExec()) {
            return super.updateById(model);
        }
        return result.getData();
    }


}
