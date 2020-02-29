package com.github.zuihou.user.resolver;

import cn.hutool.core.convert.Convert;
import com.github.zuihou.base.R;
import com.github.zuihou.context.BaseContextHandler;
import com.github.zuihou.user.annotation.LoginUser;
import com.github.zuihou.user.feign.UserQuery;
import com.github.zuihou.user.feign.UserResolverService;
import com.github.zuihou.user.model.SysUser;
import com.github.zuihou.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Token转化SysUser
 *
 * @author zuihou
 * @date 2018/12/21
 */
@Slf4j
public class ContextArgumentResolver implements HandlerMethodArgumentResolver {

//    private final UserResolverService userResolverService;
//
//    public ContextArgumentResolver(UserResolverService userResolverService) {
//        this.userResolverService = userResolverService;
//    }

    /**
     * 入参筛选
     *
     * @param mp 参数集合
     * @return 格式化后的参数
     */
    @Override
    public boolean supportsParameter(MethodParameter mp) {
        return mp.hasParameterAnnotation(LoginUser.class) && mp.getParameterType().equals(SysUser.class);
    }

    /**
     * @param methodParameter       入参集合
     * @param modelAndViewContainer model 和 view
     * @param nativeWebRequest      web相关
     * @param webDataBinderFactory  入参解析
     * @return 包装对象
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) {
        Long userId = BaseContextHandler.getUserId();
        String account = BaseContextHandler.getAccount();
        String name = BaseContextHandler.getName();

        //以下代码为 根据 @LoginUser 注解来注入 SysUser 对象
        SysUser user = SysUser.builder()
                .id(userId)
                .account(account)
                .name(name)
                .build();

        try {
            LoginUser loginUser = methodParameter.getParameterAnnotation(LoginUser.class);
            boolean isFull = loginUser.isFull();

            if (isFull || loginUser.isStation() || loginUser.isOrg() || loginUser.isRoles()) {
                UserResolverService userResolverService = SpringUtils.getBean(UserResolverService.class);
                R<SysUser> result = userResolverService.getById(Convert.toLong(userId),
                        UserQuery.builder()
                                .full(isFull)
                                .org(loginUser.isOrg())
                                .station(loginUser.isStation())
                                .roles(loginUser.isRoles())
                                .build());
                if (result.getIsSuccess() && result.getData() != null) {
                    return result.getData();
                }
            }
        } catch (Exception e) {
            log.warn("注入登录人信息时，发生异常. --> {}", user, e);
        }

        return user;
    }
}
