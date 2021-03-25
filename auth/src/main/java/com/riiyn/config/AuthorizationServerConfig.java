package com.riiyn.config;

import com.riiyn.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;

/**
 * @author: riiyn
 * @date: 2021/3/24 16:39
 * @description: 认证服务配置中心
 */
@Slf4j
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    
//    @Autowired
//    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    /**
     * 使用密码模式时需要此配置
     * @param endpoints
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .authenticationManager(authenticationManager)
                // 使用jwt-token转换器
                .accessTokenConverter(jwtAccessTokenConverter())
                .userDetailsService(userDetailsService)
                // refresh_token有两种使用方式：重复使用(true)、非重复使用(false)，默认为true
                //      1.重复使用：access_token过期刷新时， refresh token过期时间未改变，仍以初次生成的时间为准
                //      2.非重复使用：access_token过期刷新时， refresh_token过期时间延续，在refresh_token有效期内刷新而无需失效再次登录
                .reuseRefreshTokens(false);
    }
    
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // 客户端配置，也可以在yaml中配置
        clients.inMemory()
                // 配置客户端id和密码
                .withClient("riiyn_client1")
                .secret("riiyn111")
                // token有效期，大厂一般7200秒，也就是2小时
                .accessTokenValiditySeconds(2400)
                // token刷新有效期,5分钟内都可刷新token
                .refreshTokenValiditySeconds(3000)
                // 配置申请的权限范围
                .scopes("all").autoApprove(true)
                // 配置grant_type，表示授权类型
                .authorizedGrantTypes("password", "refresh_token")
                // 可以添加多个客户端
                .and()
                .withClient("riiyn_client2")
                .secret("riiyn000")
                .accessTokenValiditySeconds(2400)
                .refreshTokenValiditySeconds(3000)
                .scopes("all").autoApprove(true)
                .authorizedGrantTypes("password", "refresh_token");
    }
    
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                // 允许表单认证请求
                .allowFormAuthenticationForClients()
                // spel表达式 访问公钥端点（/auth/token_key）需要认证
                .tokenKeyAccess("isAuthenticated()")
                // spel表达式 访问令牌解析端点（/auth/check_token）需要认证
                .checkTokenAccess("isAuthenticated()");
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
                new ClassPathResource("riiyn.jks"), "102323".toCharArray());
        return factory.getKeyPair("riiyn", "102323".toCharArray());
    }
}
