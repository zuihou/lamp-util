package com.tangyh.basic.security.feign;

import com.tangyh.basic.base.R;
import com.tangyh.basic.context.ContextUtil;
import com.tangyh.basic.security.model.SysUser;

/**
 * @author zuihou
 * @date 2020年02月24日10:41:49
 */
public interface UserResolverService {
    /**
     * 根据id查询用户
     *
     * @param id        用户id
     * @param userQuery 查询条件
     * @return 用户信息
     */
    R<SysUser> getById(Long id, UserQuery userQuery);

    /**
     * 查询当前用户的信息
     *
     * @param userQuery 查询条件
     * @return 用户信息
     */
    default R<SysUser> getById(UserQuery userQuery) {
        return this.getById(ContextUtil.getUserId(), userQuery);
    }
}
