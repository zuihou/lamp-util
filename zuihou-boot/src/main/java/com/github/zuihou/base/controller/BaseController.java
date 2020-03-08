package com.github.zuihou.base.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.zuihou.base.R;
import com.github.zuihou.base.request.PageParams;
import com.github.zuihou.base.service.SuperService;
import com.github.zuihou.context.BaseContextHandler;
import com.github.zuihou.database.mybatis.conditions.Wraps;
import com.github.zuihou.database.mybatis.conditions.query.QueryWrap;
import com.github.zuihou.exception.BizException;
import com.github.zuihou.exception.code.BaseExceptionCode;
import com.github.zuihou.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 基础接口
 *
 * @param <Entity> 实体
 * @author zuihou
 * @date 2020年03月07日21:56:32
 */
public interface BaseController<Entity> {

    Class<Entity> getEntityClass();

    SuperService<Entity> getBaseService();


    /**
     * 成功返回
     *
     * @param data 返回内容
     * @param <T>  返回类型
     * @return R
     */
    default <T> R<T> success(T data) {
        return R.success(data);
    }

    /**
     * 成功返回
     *
     * @return R.true
     */
    default R<Boolean> success() {
        return R.success();
    }

    /**
     * 失败返回
     *
     * @param msg 失败消息
     * @param <T> 返回类型
     * @return
     */
    default <T> R<T> fail(String msg) {
        return R.fail(msg);
    }

    /**
     * 失败返回
     *
     * @param msg  失败消息
     * @param args 动态参数
     * @param <T>  返回类型
     * @return
     */
    default <T> R<T> fail(String msg, Object... args) {
        return R.fail(msg, args);
    }

    /**
     * 失败返回
     *
     * @param code 失败编码
     * @param msg  失败消息
     * @param <T>  返回类型
     * @return
     */
    default <T> R<T> fail(int code, String msg) {
        return R.fail(code, msg);
    }

    /**
     * 失败返回
     *
     * @param exceptionCode 失败异常码
     * @param <T>
     * @return
     */
    default <T> R<T> fail(BaseExceptionCode exceptionCode) {
        return R.fail(exceptionCode);
    }

    /**
     * 失败返回
     *
     * @param exception 异常
     * @param <T>
     * @return
     */
    default <T> R<T> fail(BizException exception) {
        return R.fail(exception);
    }

    /**
     * 失败返回
     *
     * @param throwable 异常
     * @param <T>
     * @return
     */
    default <T> R<T> fail(Throwable throwable) {
        return R.fail(throwable);
    }

    /**
     * 参数校验失败返回
     *
     * @param msg 错误消息
     * @param <T>
     * @return
     */
    default <T> R<T> validFail(String msg) {
        return R.validFail(msg);
    }

    /**
     * 参数校验失败返回
     *
     * @param msg  错误消息
     * @param args 错误参数
     * @param <T>
     * @return
     */
    default <T> R<T> validFail(String msg, Object... args) {
        return R.validFail(msg, args);
    }

    /**
     * 参数校验失败返回
     *
     * @param exceptionCode 错误编码
     * @param <T>
     * @return
     */
    default <T> R<T> validFail(BaseExceptionCode exceptionCode) {
        return R.validFail(exceptionCode);
    }

    /**
     * 获取当前id
     *
     * @return userId
     */
    default Long getUserId() {
        return BaseContextHandler.getUserId();
    }

    /**
     * 当前请求租户
     *
     * @return 租户编码
     */
    default String getTenant() {
        return BaseContextHandler.getTenant();
    }

    /**
     * 登录人账号
     *
     * @return 账号
     */
    default String getAccount() {
        return BaseContextHandler.getAccount();
    }

    /**
     * 登录人姓名
     *
     * @return 姓名
     */
    default String getName() {
        return BaseContextHandler.getName();
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
     * 处理参数
     *
     * @param params
     */
    default <PageDTO> void handlerQueryParams(PageParams<PageDTO> params) {
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
    default <PageDTO> void query(PageParams<PageDTO> params, IPage<Entity> page, Long defSize) {
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
    default <PageDTO> void handlerWrapper(QueryWrap<Entity> wrapper, PageParams<PageDTO> params) {
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
