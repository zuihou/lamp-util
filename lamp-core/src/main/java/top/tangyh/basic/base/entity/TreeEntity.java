package top.tangyh.basic.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 包括id、create_time、created_by、updated_by、update_time、label、parent_id、sort_value 字段的表继承的树形实体
 *
 * @author zuihou
 * @date 2019/05/05
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TreeEntity<E, T extends Serializable> extends Entity<T> {

    /**
     * 名称
     */
    @Schema(description = "名称")
    @NotEmpty(message = "名称不能为空")
    @Size(max = 255, message = "名称长度不能超过255")
    @TableField(value = "label")
    protected String label;

    /**
     * 父ID
     */
    @Schema(description = "父ID")
    @TableField(value = "parent_id")
    protected T parentId;

    /**
     * 排序
     */
    @Schema(description = "排序号")
    @TableField(value = "sort_value")
    protected Integer sortValue;


    @Schema(description = "子节点", hidden = true)
    @TableField(exist = false)
    protected List<E> children;


    /**
     * 初始化子类
     */
    @JsonIgnore
    public void initChildren() {
        if (getChildren() == null) {
            this.setChildren(new ArrayList<>());
        }
    }

    @JsonIgnore
    public void addChildren(E child) {
        initChildren();
        children.add(child);
    }
}
