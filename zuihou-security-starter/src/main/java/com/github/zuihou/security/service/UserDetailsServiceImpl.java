package com.github.zuihou.security.service;

import com.github.zuihou.security.util.SecurityConstants;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户详细信息
 *
 * @author zuihou
 * @date 2020年03月25日23:04:21
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * 根据用户密码查询用户
     *
     * @param username 用户名
     * @return
     */
    @Override
    @SneakyThrows
    public UserDetails loadUserByUsername(String username) {
        //远程查询用户
        UserDetails userDetails = getUserDetails();
        return userDetails;
    }

    /**
     * 构建 userdetails
     *
     * @param result 用户信息
     * @return
     */
    private UserDetails getUserDetails() {
//		if (result == null || result.getData() == null) {
//			throw new UsernameNotFoundException("用户不存在");
//		}
//
//		UserInfo info = result.getData();
        Set<String> dbAuthsSet = new HashSet<>();
        dbAuthsSet.add("ROLE_1");
        dbAuthsSet.add("ROLE_2");
        dbAuthsSet.add("gen_form_edit");
//		if (ArrayUtil.isNotEmpty(info.getRoles())) {
//			// 获取角色
//			Arrays.stream(info.getRoles()).forEach(role -> dbAuthsSet.add(SecurityConstants.ROLE + role));
//			// 获取资源
//			dbAuthsSet.addAll(Arrays.asList(info.getPermissions()));
//
//		}
        Collection<? extends GrantedAuthority> authorities
                = AuthorityUtils.createAuthorityList(dbAuthsSet.toArray(new String[0]));
//		SysUser user = info.getSysUser();
//
//		// 构造security用户
//		return new PigUser(user.getUserId(), user.getDeptId(), user.getUsername(), SecurityConstants.BCRYPT + user.getPassword(),
//			StrUtil.equals(user.getLockFlag(), CommonConstants.STATUS_NORMAL), true, true, true, authorities);
        return new SysUser(3L, 1L, 1L, "zuihou", SecurityConstants.BCRYPT + "d9d17d88918aa72834289edaf38f42e2", true,
                true, true, true, authorities);
    }
}
