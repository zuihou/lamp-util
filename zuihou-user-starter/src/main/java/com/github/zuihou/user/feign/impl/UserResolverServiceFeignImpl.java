//package com.github.zuihou.user.feign.impl;
//
//import com.github.zuihou.base.R;
//import com.github.zuihou.user.feign.UserQuery;
//import com.github.zuihou.user.feign.UserResolveApi;
//import com.github.zuihou.user.feign.UserResolverService;
//import com.github.zuihou.user.model.SysUser;
//
///**
// * feign 实现
// *
// * @author zuihou
// * @date 2020年02月24日10:51:46
// */
//public class UserResolverServiceFeignImpl implements UserResolverService {
//    final UserResolveApi userResolveApi;
//
//    public UserResolverServiceFeignImpl(UserResolveApi userResolveApi) {
//        this.userResolveApi = userResolveApi;
//    }
//
//    @Override
//    public R<SysUser> getById(Long id, UserQuery userQuery) {
//        return userResolveApi.getById(id, userQuery);
//    }
//}
