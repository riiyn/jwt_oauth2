package com.riiyn.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

/**
 * @author: riiyn
 * @date: 2021/3/24 15:32
 * @description:
 */
@Getter
public class UserInfo extends User {
    
    private final Integer id;
    private final List<String> roles;
    
    public UserInfo(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, Integer id, List<String> roles) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.roles = roles;
    }
}
