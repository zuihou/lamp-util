package com.github.zuihou.base.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.zuihou.base.R;
import com.github.zuihou.base.request.PageParams;
import com.github.zuihou.database.mybatis.conditions.Wraps;
import com.github.zuihou.database.mybatis.conditions.query.QueryWrap;
import com.github.zuihou.log.annotation.SysLog;
import com.github.zuihou.utils.DateUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 查询Controller
 *
 * @param <Entity>  实体
 * @param <Id>      主键
 * @param <PageDTO> 分页参数
 * @author zuihou
 * @date 2020年03月07日22:06:35
 */
public interface QueryController<Entity, Id extends Serializable, PageDTO> extends BaseController<Entity> {

    /**
     * 查询
     *
     * @param id 主键id
     * @return 查询结果
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", dataType = "long", paramType = "query"),
    })
    @ApiOperation(value = "查询", notes = "查询")
    @GetMapping("/{id}")
    @SysLog("'查询:' + #id")
    default R<Entity> get(@PathVariable Id id) {
        return success(getBaseService().getById(id));
    }


    /**
     * 转换后保存
     *
     * @param list
     */
    default void handlerImport(List<Map<String, String>> list) {

    }

    /**
     * 分页查询
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "分页列表查询")
    @PostMapping(value = "/page")
    @SysLog(value = "'分页列表查询:第' + #params?.current + '页, 显示' + #params?.size + '行'", response = false)
    default R<IPage<Entity>> page(@RequestBody @Validated PageParams<PageDTO> params) {
        // 处理参数
        IPage<Entity> page = params.getPage();
        query(params, page, null);
        return success(page);
    }


    /**
     * 根据 bean字段 反射出 数据库字段
     *
     * @param beanField
     * @param clazz
     * @return
     */
    default String getDbField(String beanField, Class<?> clazz) {
        Field field = ReflectUtil.getField(clazz, beanField);
        if (field == null) {
            return StrUtil.EMPTY;
        }
        TableField tf = field.getAnnotation(TableField.class);
        if (tf != null && StringUtils.isNotEmpty(tf.value())) {
            String str = tf.value();
            return str;
        }
        return StrUtil.EMPTY;
    }

    /**
     * 批量查询
     *
     * @param data 批量查询
     * @return 查询结果
     */
    @ApiOperation(value = "批量查询", notes = "批量查询")
    @PostMapping("/query")
    @SysLog("批量查询")
    default R<List<Entity>> query(@RequestBody Entity data) {
        QueryWrap<Entity> wrapper = Wraps.q(data);
        return success(getBaseService().list(wrapper));
    }


    /**
     * 处理参数
     *
     * @param params
     */
    default void handlerQueryParams(PageParams<PageDTO> params) {
    }


    /**
     * 执行查询
     * <p>
     * 可以覆盖后重写查询逻辑
     *
     * @param params
     * @param page
     * @param defSize
     */
    default void query(PageParams<PageDTO> params, IPage<Entity> page, Long defSize) {
        handlerQueryParams(params);

        if (defSize != null) {
            page.setSize(defSize);
        }
        Entity model = BeanUtil.toBean(params.getModel(), getEntityClass());
        QueryWrap<Entity> wrapper = Wraps.q(model);

        handlerWrapper(wrapper, params);
        getBaseService().page(page, wrapper);

        // 处理结果
        handlerResult(page);
    }


    /**
     * 处理时间区间，可以覆盖后处理组装查询条件
     *
     * @param wrapper
     * @param params
     */
    default void handlerWrapper(QueryWrap<Entity> wrapper, PageParams<PageDTO> params) {
        if (CollUtil.isNotEmpty(params.getMap())) {
            Map<String, String> map = params.getMap();
            //拼装区间
            for (Map.Entry<String, String> field : map.entrySet()) {
                String key = field.getKey();
                String value = field.getValue();
                if (StrUtil.isEmpty(value)) {
                    continue;
                }
                if (key.endsWith("_st")) {
                    String beanField = StrUtil.subBefore(key, "_st", true);
                    wrapper.ge(getDbField(beanField, getEntityClass()), DateUtils.getStartTime(value));
                }
                if (key.endsWith("_ed")) {
                    String beanField = StrUtil.subBefore(key, "_ed", true);
                    wrapper.le(getDbField(beanField, getEntityClass()), DateUtils.getEndTime(value));
                }
            }
        }
    }

    /**
     * 自定义处理返回结果
     *
     * @param page
     */
    default void handlerResult(IPage<Entity> page) {
        // 调用注入方法
    }

}
