package com.riiyn.config;

import com.riiyn.api.ResultCode;
import com.riiyn.security.AuthorizationManager;
import com.riiyn.util.WebUtils;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

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
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(authenticationEntryPoint())
                .and()
                .csrf().disable();
        return httpSecurity.build();
    }
    
    /**
     * 自定义token未授权响应
     * @return
     */
    @Bean
    ServerAccessDeniedHandler accessDeniedHandler(){
        return (serverWebExchange, e) -> Mono.defer(() -> Mono.just(serverWebExchange.getResponse()))
                .flatMap(response -> WebUtils.writeFailedToResponse(response, ResultCode.ACCESS_UNAUTHORIZED));
    }
    
    /**
     * 自定义token无效/过期响应
     * @return
     */
    @Bean
    ServerAuthenticationEntryPoint authenticationEntryPoint(){
        return (serverWebExchange, e) -> Mono.defer(() -> Mono.just(serverWebExchange.getResponse()))
                .flatMap(response -> WebUtils.writeFailedToResponse(response, ResultCode.TOKEN_INVALID_OR_EXPIRED));
    }
}
