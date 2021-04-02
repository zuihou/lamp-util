package com.tangyh.basic.model;


import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 加载数据
 *
 * @author zuihou
 * @date 2020-05-19 10:26:15
 */
public interface LoadService {
    /**
     * 根据id查询 显示名
     *
     * @param ids 唯一键（可能不是主键ID)
     * @return
     */
    Map<Serializable, Object> findNameByIds(Set<Serializable> ids);

    /**
     * 根据id查询实体
     *
     * @param ids 唯一键（可能不是主键ID)
     * @return
     */
    Map<Serializable, Object> findByIds(Set<Serializable> ids);


}
