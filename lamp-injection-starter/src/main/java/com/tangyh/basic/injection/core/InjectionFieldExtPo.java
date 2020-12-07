package com.tangyh.basic.injection.core;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Objects;
import com.tangyh.basic.annotation.injection.InjectionField;
import com.tangyh.basic.context.ContextUtil;
import com.tangyh.basic.utils.SpringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 封装 InjectionField 注解中解析出来的参数 + 标记该注解的值的集合
 * <p>
 * 必须重写该类的 equals() 和 hashCode() 便于Map操作
 *
 * @author zuihou
 * @date 2020年02月03日18:48:15
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@Slf4j
public class InjectionFieldExtPo extends InjectionFieldPo {

    /**
     * 动态查询值
     */
    private Set<Serializable> keys = new HashSet<>();

    private String tenant;

    public InjectionFieldExtPo(InjectionField rf) {
        super(rf);
    }

    public InjectionFieldExtPo(InjectionFieldPo po, Set<Serializable> keys) {
        super(po);
        this.keys = keys;
        this.tenant = ContextUtil.getTenant();
    }

    public InjectionFieldExtPo(InjectionField rf, Set<Serializable> keys) {
        super(rf);
        this.keys = keys;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InjectionFieldExtPo that = (InjectionFieldExtPo) o;

        boolean isEquals = Objects.equal(getMethod(), that.getMethod());

        if (StrUtil.isNotEmpty(getApi())) {
            isEquals = isEquals && Objects.equal(getApi(), that.getApi());
        } else {
            isEquals = isEquals && Objects.equal(getApiClass(), that.getApiClass());
        }

        boolean isEqualsKeys = keys.size() == that.keys.size() && keys.containsAll(that.keys);

        return isEquals && isEqualsKeys;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getApi(), getApiClass(), getMethod(), keys);
    }


    /**
     * 加载数据
     *
     * @return 查询指定接口后得到的值
     */
    public Map<Serializable, Object> loadMap() {
        Object bean;
        if (StrUtil.isNotEmpty(this.getApi())) {
            bean = SpringUtils.getBean(this.getApi());
            log.info("建议在方法： [{}.{}]，上加入缓存，加速查询", this.getApi(), this.getMethod());
        } else {
            bean = SpringUtils.getBean(this.getApiClass());
            log.info("建议在方法： [{}.{}]，上加入缓存，加速查询", this.getApiClass().toString(), this.getMethod());
        }
        return ReflectUtil.invoke(bean, this.getMethod(), this.getKeys());
    }
}
