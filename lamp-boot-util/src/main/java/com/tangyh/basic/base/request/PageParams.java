package com.tangyh.basic.base.request;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tangyh.basic.base.entity.Entity;
import com.tangyh.basic.base.entity.SuperEntity;
import com.tangyh.basic.utils.AntiSqlFilterUtils;
import com.tangyh.basic.utils.StrPool;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页参数
 *
 * @author zuihou
 * @date 2020年02月14日16:19:36
 */
@Data
@ApiModel(value = "PageParams", description = "分页参数")
public class PageParams<T> {

    @NotNull(message = "查询对象model不能为空")
    @ApiModelProperty(value = "查询参数", required = true)
    private T model;

    @ApiModelProperty(value = "页面大小", example = "10")
    private long size = 10;

    @ApiModelProperty(value = "当前页", example = "1")
    private long current = 1;

    @ApiModelProperty(value = "排序,默认createTime", allowableValues = "id,createTime,updateTime", example = "id")
    private String sort = SuperEntity.FIELD_ID;

    @ApiModelProperty(value = "排序规则, 默认descending", allowableValues = "descending,ascending", example = "descending")
    private String order = "descending";

    @ApiModelProperty("扩展参数")
    private Map<String, Object> extra = new HashMap<>(16);

    /**
     * 支持多个字段排序，用法：
     * eg.1, 参数：{order:"name,id", order:"descending,ascending" }。 排序： name desc, id asc
     * eg.2, 参数：{order:"name", order:"descending,ascending" }。 排序： name desc
     * eg.3, 参数：{order:"name,id", order:"descending" }。 排序： name desc
     *
     * @return 分页对象
     */
    @JsonIgnore
    public <E> IPage<E> buildPage() {
        PageParams params = this;
        //没有排序参数
        if (StrUtil.isEmpty(params.getSort())) {
            return new Page(params.getCurrent(), params.getSize());
        }

        Page<E> page = new Page(params.getCurrent(), params.getSize());

        List<OrderItem> orders = new ArrayList<>();
        String[] sortArr = StrUtil.split(params.getSort(), StrPool.COMMA);
        String[] orderArr = StrUtil.split(params.getOrder(), StrPool.COMMA);

        int len = Math.min(sortArr.length, orderArr.length);
        for (int i = 0; i < len; i++) {
            String humpSort = sortArr[i];
            // 简单的 驼峰 转 下划线
            String underlineSort = StrUtil.toUnderlineCase(humpSort);

            // 除了 create_time 和 updateTime 都过滤sql关键字
            if (!StrUtil.equalsAny(humpSort, SuperEntity.CREATE_TIME, Entity.UPDATE_TIME)) {
                underlineSort = AntiSqlFilterUtils.getSafeValue(underlineSort);
            }

            orders.add("ascending".equals(orderArr[i]) ? OrderItem.asc(underlineSort) : OrderItem.desc(underlineSort));
        }

        page.setOrders(orders);

        return page;
    }

    /**
     * 计算当前分页偏移量
     */
    @JsonIgnore
    public long offset() {
        long current = this.current;
        if (current <= 1L) {
            return 0L;
        }
        return (current - 1) * this.size;
    }

    @JsonIgnore
    public PageParams<T> put(String key, Object value) {
        if (this.extra == null) {
            this.extra = new HashMap<>(16);
        }
        this.extra.put(key, value);
        return this;
    }

    @JsonIgnore
    public PageParams<T> putAll(Map<String, Object> extra) {
        if (this.extra == null) {
            this.extra = new HashMap<>(16);
        }
        this.extra.putAll(extra);
        return this;
    }
}
