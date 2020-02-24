package com.github.zuihou.user.feign;

import com.github.zuihou.base.R;
import com.github.zuihou.user.model.SysUser;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

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
    R<SysUser> getById(@PathVariable("id") Long id, @RequestBody UserQuery userQuery);
}
