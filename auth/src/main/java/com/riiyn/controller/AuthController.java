package com.riiyn.controller;

import com.riiyn.common.Oauth2Constant;
import com.riiyn.util.AuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.Map;

/**
 * @author: riiyn
 * @date: 2021/3/29 16:24
 * @description: 自定义认证
 */
//@RestController
@RequestMapping("/oauth")
@Slf4j
public class AuthController {
    
    @Resource
    private TokenEndpoint tokenEndpoint;
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/token")
    public OAuth2AccessToken accessToken(Principal principal, @RequestParam Map<String, String> parameters) {
        if (AuthUtil.isRefreshTokenRequest(parameters)){
            // 刷新token 将旧token加入黑名单
            if (AuthUtil.token2BlackList(parameters.get(Oauth2Constant.REFRESH_TOKEN), redisTemplate)) {
                log.info("旧token已加入黑名单...");
            }
        }
        OAuth2AccessToken token = null;
        try {
            token = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        } catch (Exception e) {
           log.error("token认证出错 {}", e.getMessage());
        }
        return token;
    }
}
