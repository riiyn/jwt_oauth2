package com.riiyn.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author: riiyn
 * @date: 2021/3/23 22:28
 * @description: 鉴权
 */
@Component
@Slf4j
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        final ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        final ServerHttpResponse response = authorizationContext.getExchange().getResponse();
        System.out.println();
        return null;
    }
    
    @Override
    public Mono<Void> verify(Mono<Authentication> authentication, AuthorizationContext object) {
        System.out.println();
        return null;
    }
}
