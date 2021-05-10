package com.riiyn.security;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.util.DateUtils;
import com.riiyn.common.Oauth2Constant;
import com.riiyn.feign.AuthFeign;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Map;

/**
 * @author: riiyn
 * @date: 2021/3/28 21:04
 * @description: 鉴权管理
 */
@Component
@Log4j2
@AllArgsConstructor
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    
    private final AuthFeign authFeign;
    
    @Autowired
    private final RedisTemplate redisTemplate;
    
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        
        final ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        final String path = request.getURI().getPath();
        log.info("请求path：{}", path);
        
        PathMatcher pathMatcher = new AntPathMatcher();
        // 白名单放行
        if (!pathMatcher.matchStart("/*/admin/**", path)) {
            log.info("非白名单，校验token...");
            if (!checkToken(request, path)) {
                return Mono.just(new AuthorizationDecision(false));
            }
        }
        log.info("开始鉴权...");
        // todo
        
        return Mono.just(new AuthorizationDecision(true));
    }
    
    /**
     * token校验
     * @param request
     * @param path
     * @return
     */
    private boolean checkToken(ServerHttpRequest request, String path) {
        // 从请求头获取token
        String token = request.getHeaders().getFirst(Oauth2Constant.AUTHORIZATION);
        if (StringUtils.isEmpty(token)){
            log.info("token为空，拒绝访问：{}", path);
            return false;
        }
        
        // 去掉token前缀
        token = org.apache.commons.lang.StringUtils.substringAfter(token, Oauth2Constant.TOKEN_TYPE);
        final JWSObject jwsObject;
        try {
            jwsObject = JWSObject.parse(token);
            final Map<String, Object> objectMap = jwsObject.getPayload().toJSONObject();
            final Long exp = (Long) objectMap.get(Oauth2Constant.JWT_EXP_KEY);
            if (getExpTime(exp).getTime() < System.currentTimeMillis()){
                log.info("token已过期：{}", path);
                return false;
            }
    
            // 判断token是否在黑名单
//            final String jti = (String) objectMap.get(Oauth2Constant.JWT_JTI_KEY);
//            final Boolean hasKey = redisTemplate.hasKey(Oauth2Constant.TOKEN_BLACKLIST_PREFIX + jti);
//            if (hasKey){
//                log.info("token不合法：{}", path);
//                return false;
//            }
    
            final Boolean hasKey = redisTemplate.hasKey(Oauth2Constant.REDIS_TOKEN_KEY_PREFIX + token);
            if (!hasKey){
                log.info("token不合法：{}", path);
                return false;
            }
    
            // 校验token签名
            final Map<String, Object> publicKey = authFeign.getPublicKey();
            final RSAKey rsaKey = RSAKey.parse(publicKey);
            if (!jwsObject.verify(new RSASSAVerifier(rsaKey.toPublicJWK()))){
                log.info("token签名不合法：{}", path);
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }
    
    private Date getExpTime(Long expirationTime){
        if (null == expirationTime) return null;
        return DateUtils.fromSecondsSinceEpoch(((Number) expirationTime).longValue());
    }
}
