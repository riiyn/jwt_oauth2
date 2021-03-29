package com.riiyn.util;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.riiyn.common.Oauth2Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: riiyn
 * @date: 2021/3/29 21:26
 * @description: 认证相关工具类
 */
@Slf4j
public class AuthUtil {
    /**
     * 将token加入黑名单
     * @param refreshToken 刷新token
     * @param redisTemplate redis
     * @return
     */
    public static boolean token2BlackList(String refreshToken, RedisTemplate<String, Object> redisTemplate){
        try {
            final JWSObject jwsObject = JWSObject.parse(refreshToken);
            final Map<String, Object> jsonObject = jwsObject.getPayload().toJSONObject();
            final String ati = JSONObjectUtils.getString(jsonObject, Oauth2Constant.JWT_ATI_KEY);
            final long exp = JSONObjectUtils.getLong(jsonObject, Oauth2Constant.JWT_EXP_KEY);
            final long current = System.currentTimeMillis() / 1000;
            if (exp > current){
                redisTemplate.opsForValue()
                        .set(Oauth2Constant.TOKEN_BLACKLIST_PREFIX + ati, ati, (exp - current), TimeUnit.SECONDS);
                return true;
            }
            
        } catch (ParseException e) {
            log.error("payload解析错误：{}", e.getMessage());
            log.error("旧token加入黑名单失败...");
            return false;
        }
        return true;
    }
    
    /**
     * 判断是否是刷新token请求
     * @param parameters 参数
     * @return
     */
    public static boolean isRefreshTokenRequest(Map<String, String> parameters) {
        return Oauth2Constant.REFRESH_TOKEN.equals(parameters.get(Oauth2Constant.GRANT_TYPE))
                && parameters.get(Oauth2Constant.GRANT_TYPE) != null;
    }
}
