package com.riiyn.config;

import com.riiyn.security.AuthorizationManager;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @author: riiyn
 * @date: 2021/3/28 20:52
 * @description: 资源服务配置
 */
@Configuration
@EnableWebFluxSecurity
@AllArgsConstructor
public class ResourceServerConfig {
    
    private final AuthorizationManager authorizationManager;
    
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity){
        httpSecurity.authorizeExchange()
                // 认证相关直接放行
                .pathMatchers("/auth/oauth/**").permitAll()
                .anyExchange().access(authorizationManager)
                .and()
                .csrf().disable();
        return httpSecurity.build();
    }
}
