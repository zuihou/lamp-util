package top.tangyh.basic.model;


import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 加载数据
 * <p>
 * 3.2.5 版本开始，只保留一个方法，若一个表，想要有多重回显场景，可以新建多个实现类，返回不一样的Map
 *
 * @author zuihou
 * @date 2020-05-19 10:26:15
 */
public interface LoadService {
    /**
     * 根据id查询待回显参数
     *
     * @param ids 唯一键（可能不是主键ID)
     * @return
     */
    Map<Serializable, Object> findByIds(Set<Serializable> ids);
}
