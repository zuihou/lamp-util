package com.github.zuihou.base.controller;

import com.github.zuihou.base.service.SuperService;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;

/**
 * 简单的实现了BaseController，为了获取注入 Service 和 实体类型
 *
 * @param <S>      Service
 * @param <Entity> 实体
 * @author zuihou
 * @date 2020年03月07日22:08:27
 */
public class SuperSimpleController<S extends SuperService<Entity>, Entity> implements BaseController<Entity> {

    protected Class<Entity> entityClass = null;
    @Autowired
    protected S baseService;

    @Override
    public Class<Entity> getEntityClass() {
        if (entityClass == null) {
            this.entityClass = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        }
        return this.entityClass;
    }

    @Override
    public SuperService<Entity> getBaseService() {
        return baseService;
    }
}
