package com.riiyn.config;

import com.riiyn.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author: riiyn
 * @date: 2021/3/24 12:23
 * @description: 安全配置中心
 */
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    private final UserDetailsServiceImpl userDetailsService;
    
    @Override
    protected UserDetailsService userDetailsService() {
        return userDetailsService;
    }
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Bean
    public static PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
        // TODO 此处先不加密，因为数据库中密码没有用bcrypt加密，会报错
        return NoOpPasswordEncoder.getInstance();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // 授权请求白名单  "/login/**",
                .antMatchers("/oauth/**", "logout/**").permitAll()
                // 其他任何请求都需要认证
                .anyRequest().authenticated()
                .and()
                .formLogin().permitAll()
                .and()
                //csrf跨站请求
                .csrf().disable();
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                // 自定义的用户信息处理
                .userDetailsService(userDetailsService())
                // 使用 PasswordEncoder 密码编码器
                .passwordEncoder(passwordEncoder());
    }
}
