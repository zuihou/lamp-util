package com.github.zuihou.user.feign;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * 用户查询对象
 *
 * @author zuihou
 * @date 2019-07-10 11:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ToString
@Builder
public class UserQuery {
    /**
     * 是否查询SysUser对象所有信息，true则通过rpc接口查询
     */
    private Boolean full;

    /**
     * 是否只查询角色信息，true则通过rpc接口查询
     */
    private Boolean roles;

    /**
     * 是否只查询组织信息，true则通过rpc接口查询
     */
    private Boolean org;

    /**
     * 是否只查询岗位信息，true则通过rpc接口查询
     */
    private Boolean station;

    public static UserQuery buildFull() {
        return new UserQuery().setFull(true);
    }

    public static UserQuery buildRoles() {
        return new UserQuery().setRoles(true);
    }

    public static UserQuery buildOrg() {
        return new UserQuery().setOrg(true);
    }

    public static UserQuery buildStation() {
        return new UserQuery().setStation(true);
    }
}
