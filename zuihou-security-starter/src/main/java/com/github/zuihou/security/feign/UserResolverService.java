package com.github.zuihou.security.feign;

import com.github.zuihou.base.R;
import com.github.zuihou.context.BaseContextHandler;
import com.github.zuihou.security.model.SysUser;

/**
 * @author zuihou
 * @date 2020年02月24日10:41:49
 */
public interface UserResolverService {
    /**
     * 根据id查询用户
     *
     * @param id
     * @param userQuery
     * @return
     */
    R<SysUser> getById(Long id, UserQuery userQuery);

    /**
     * 查询当前用户的信息
     *
     * @param userQuery
     * @return
     */
    default R<SysUser> getById(UserQuery userQuery) {
        return this.getById(BaseContextHandler.getUserId(), userQuery);
    }
}
