package com.riiyn.config;

import com.riiyn.common.Oauth2Constant;
import com.riiyn.entity.UserInfo;
import com.riiyn.service.ClientDetailsServiceImpl;
import com.riiyn.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.security.KeyPair;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: riiyn
 * @date: 2021/3/24 16:39
 * @description: 认证服务配置中心
 */
@Slf4j
@Configuration
@EnableAuthorizationServer
@RequiredArgsConstructor
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    
//    @Autowired
//    private PasswordEncoder passwordEncoder;
    
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisConnectionFactory redisConnectionFactory;
    private final ClientDetailsServiceImpl clientDetailsService;
    
    /**
     * token存到redis中
     * @return
     */
    @Bean
    public RedisTokenStore redisTokenStore(){
        return new RedisTokenStore(redisConnectionFactory);
    }
    
    /**
     * 授权服务端点配置
     * @param endpoints
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        log.info("将 jwt 内容增强信息、jwt-token 转换器添加进 token 增强链");
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), jwtAccessTokenConverter()));
        
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        log.info("设置客户端信息到token服务");
        tokenServices.setClientDetailsService(clientDetailsService);
        log.info("设置token增强链到token服务");
        tokenServices.setTokenEnhancer(tokenEnhancerChain);
        
        endpoints
                // 配置认证管理器
                .authenticationManager(authenticationManager)
                // 使用jwt-token转换器
                .accessTokenConverter(jwtAccessTokenConverter())
                // 配置jwt内容增强
//                .tokenEnhancer(tokenEnhancerChain)
                // 配置token服务
                .tokenServices(tokenServices)
                // 配置token存储位置
                .tokenStore(redisTokenStore())
                
                // 配置自定义的用户信息处理服务
                .userDetailsService(userDetailsService)
                // refresh_token有两种使用方式：重复使用(true)、非重复使用(false)，默认为true
                //      1.重复使用：access_token过期刷新时， refresh token过期时间未改变，仍以初次生成的时间为准
                //      2.非重复使用：access_token过期刷新时， refresh_token过期时间延续，在refresh_token有效期内刷新而无需失效再次登录
                .reuseRefreshTokens(false);
    }
    
    /**
     * 客户端信息配置，从数据库查询
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // 设置从数据库查询客户端信息的SQL
        clientDetailsService.setSelectClientDetailsSql(Oauth2Constant.SELECT_CLIENT_DETAILS_SQL);
        clientDetailsService.setFindClientDetailsSql(Oauth2Constant.FIND_CLIENT_DETAILS_SQL);
        clients.withClientDetails(clientDetailsService);
        
        // 客户端配置，也可以在yaml中配置
//        clients.inMemory()
//                // 配置客户端id和密码
//                .withClient("riiyn_client1")
//                .secret("riiyn111")
//                // token有效期，大厂一般7200秒，也就是2小时
//                .accessTokenValiditySeconds(2400)
//                // token刷新有效期,5分钟内都可刷新token
//                .refreshTokenValiditySeconds(3000)
//                // 配置申请的权限范围
//                .scopes("all").autoApprove(true)
//                // 配置grant_type，表示授权类型
//                .authorizedGrantTypes("password", "refresh_token")
//                // 可以添加多个客户端
//                .and()
//                .withClient("riiyn_client2")
//                .secret("riiyn000")
//                .accessTokenValiditySeconds(2400)
//                .refreshTokenValiditySeconds(3000)
//                .scopes("all").autoApprove(true)
//                .authorizedGrantTypes(Oauth2Constant.PASSWORD, Oauth2Constant.REFRESH_TOKEN);
    }
    
    /**
     * 授权服务安全配置
     * @param security
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security){
        security
                // 允许表单认证请求
                .allowFormAuthenticationForClients()
                // spel表达式 访问公钥端点（/auth/token）需要认证
                .tokenKeyAccess("isAuthenticated()")
                // spel表达式 访问令牌解析端点（/auth/check_token）需要认证
                .checkTokenAccess("isAuthenticated()");
    }
    
    /**
     * jwt内容增强
     * @return
     */
    @Bean
    public TokenEnhancer tokenEnhancer(){
        return (oAuth2AccessToken, oAuth2Authentication) -> {
            Map<String, Object> map = new HashMap<>();
            final UserInfo userInfo = (UserInfo) oAuth2Authentication.getUserAuthentication().getPrincipal();
            map.put(Oauth2Constant.USER_ROLES, userInfo.getRoles());
            ((DefaultOAuth2AccessToken)oAuth2AccessToken).setAdditionalInformation(map);
            return oAuth2AccessToken;
        };
    }
    
    /**
     * 使用非对称加密算法对token签名
     * @return JwtAccessTokenConverter
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair());
        return jwtAccessTokenConverter;
    }
    
    /**
     * 从classpath下的密钥库中获取密钥对(公钥+私钥)
     * @return KeyPair
     */
    @Bean
    public KeyPair keyPair(){
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(
                new ClassPathResource(Oauth2Constant.ENCRYPT_KEY_FILE),
                Oauth2Constant.ENCRYPT_KEY_PASSWORD.toCharArray());
        
        return factory.getKeyPair(Oauth2Constant.ENCRYPT_KEY_ALIAS,
                Oauth2Constant.ENCRYPT_KEY_PASSWORD.toCharArray());
    }
}
