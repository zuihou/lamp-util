package top.tangyh.basic.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 包括id、created_time、created_by字段的表继承的基础实体
 *
 * @author zuihou
 * @date 2019/05/05
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode
public class SuperEntity<T> implements Serializable {
    public static final String ID_FIELD = "id";
    public static final String CREATED_TIME = "createdTime";
    public static final String CREATED_TIME_FIELD = "created_time";
    public static final String CREATED_BY = "createdBy";
    public static final String CREATED_BY_FIELD = "created_by";
    public static final String CREATED_ORG_ID = "createdOrgId";
    public static final String CREATED_ORG_ID_FIELD = "created_org_id";

    private static final long serialVersionUID = -4603650115461757622L;

    @TableId(value = "id", type = IdType.INPUT)
    @Schema(description = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    protected T id;

    @Schema(description = "创建时间")
    @TableField(value = CREATED_TIME_FIELD, fill = FieldFill.INSERT)
    protected LocalDateTime createdTime;

    @Schema(description = "创建人ID")
    @TableField(value = CREATED_BY_FIELD, fill = FieldFill.INSERT)
    protected T createdBy;

    /**
     * 保存和缺省验证组
     */
    public interface Save extends Default {

    }

    /**
     * 更新和缺省验证组
     */
    public interface Update extends Default {

    }
}
