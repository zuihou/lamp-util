package top.tangyh.basic.jwt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * jwt 中存储的 内容
 *
 * @author zuihou
 * @date 2018/11/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class JwtInfo implements Serializable {
    /**
     * 用户 id
     */
    private Long userId;
    /**
     * 员工id
     */
    private Long employeeId;
    /**
     * 当前所属的公司ID
     */
    private Long currentCompanyId;
    /**
     * 当前所属的顶级公司ID
     */
    private Long currentTopCompanyId;
    /**
     * 当前所属的部门ID
     */
    private Long currentDeptId;

    /**
     * 随机数
     */
    private String uuid;
}
