package com.riiyn.service;

import com.riiyn.entity.User;
import com.riiyn.entity.UserInfo;
import com.riiyn.feign.UserFeignClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author: riiyn
 * @date: 2021/3/24 12:37
 * @description: 自定义用户认证和授权
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserFeignClient userFeignClient;
    
    private static final Integer ENABLE = 0;
    private static final Integer DISABLE = 1;
    
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        
        log.info("用户名：{}", s);
        final User user = userFeignClient.getByUsername(s);
        if (ObjectUtils.isEmpty(user)) {
            log.error("用户不存在：{}", s);
            throw new UsernameNotFoundException("用户 " + s + " 不存在");
        }
        if (DISABLE.equals(user.getStatus())) {
            log.info("该用户：{} 账户已停用", s);
            throw new UsernameNotFoundException("该用户: " + s + " 账户已停用");
        }
        
        return getUserDetails(user);
    }
    
    private UserDetails getUserDetails(User user){
        Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("*:*:*");
        List<Integer> roleIds = new ArrayList<>();
        user.getRoles().forEach(item -> roleIds.add(item.getId()));
        log.info("用户角色：{}", roleIds);
        return new UserInfo(user.getUsername(), user.getPassword(), user.getStatus().equals(ENABLE),
                true, true, true, authorities, user.getId(), roleIds);
    }
}
