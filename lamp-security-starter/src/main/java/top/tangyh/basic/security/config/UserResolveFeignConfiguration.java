package top.tangyh.basic.security.config;

import top.tangyh.basic.base.R;
import top.tangyh.basic.security.feign.UserQuery;
import top.tangyh.basic.security.feign.UserResolverService;
import top.tangyh.basic.security.model.SysUser;
import top.tangyh.basic.security.properties.SecurityProperties;
import top.tangyh.basic.utils.SpringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 类型为Feign时，使用的的实现类
 *
 * @author zuihou
 * @date 2020年02月29日21:35:37
 */
@Configuration
@ConditionalOnProperty(prefix = SecurityProperties.PREFIX, name = "type", havingValue = "FEIGN", matchIfMissing = true)
@EnableFeignClients(basePackageClasses = UserResolveFeignConfiguration.UserResolveApi.class)
public class UserResolveFeignConfiguration {

    @Bean
    @ConditionalOnMissingBean(UserResolverService.class)
    public UserResolverService getUserResolverService(UserResolveApi userResolveApi) {
        return new UserResolverServiceFeignImpl(userResolveApi);
    }

    @Bean
    @ConditionalOnMissingBean(SpringUtils.class)
    public SpringUtils getSpringUtils(ApplicationContext applicationContext) {
        SpringUtils instance = SpringUtils.getInstance();
        SpringUtils.setApplicationContext(applicationContext);
        return instance;
    }

    @FeignClient(name = "${lamp.feign.oauth-server:lamp-oauth-server}", path = "/user",
            fallback = UserResolveApiFallback.class)
    public interface UserResolveApi {

        /**
         * 根据id 查询用户详情
         *
         * @param id        用户id
         * @param userQuery 查询条件
         * @return 系统用户
         */
        @PostMapping(value = "/anno/id/{id}")
        R<SysUser> getById(@PathVariable("id") Long id, @RequestBody UserQuery userQuery);
    }

    /**
     * feign 实现
     *
     * @author zuihou
     * @date 2020年02月24日10:51:46
     */
    public static class UserResolverServiceFeignImpl implements UserResolverService {
        private final UserResolveApi userResolveApi;

        public UserResolverServiceFeignImpl(UserResolveApi userResolveApi) {
            this.userResolveApi = userResolveApi;
        }

        @Override
        public R<SysUser> getById(Long id, UserQuery userQuery) {
            return userResolveApi.getById(id, userQuery);
        }
    }

    /**
     * 用户API熔断
     *
     * @author zuihou
     * @date 2019/07/10
     */
    @Component
    public static class UserResolveApiFallback implements UserResolveApi {
        @Override
        public R<SysUser> getById(Long id, UserQuery userQuery) {
            return R.timeout();
        }
    }
}
