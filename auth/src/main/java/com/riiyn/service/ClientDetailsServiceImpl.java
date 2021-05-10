package com.riiyn.service;

import com.riiyn.common.Oauth2Constant;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author: riiyn
 * @date: 2021/3/26 23:39
 * @description: 客户端信息处理（先查询redis缓存，若没有则查询数据库，并同步到redis）
 */
@Service
@Setter
@Log4j2
public class ClientDetailsServiceImpl extends JdbcClientDetailsService {
    
    @Resource
    private DataSource dataSource;
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    private static String cacheClientKey;
    
    public ClientDetailsServiceImpl(DataSource dataSource) {
        super(dataSource);
    }
    
    @Bean("clientService")
    @Primary
    public ClientDetailsServiceImpl clientDetailsService(){
        ClientDetailsServiceImpl clientDetailsService = new ClientDetailsServiceImpl(this.dataSource);
        clientDetailsService.setRedisTemplate(this.redisTemplate);
        return clientDetailsService;
    }
    
    /**
     * 从Redis缓存中加载客户端信息
     * @param clientId 客户端id
     * @return ClientDetails
     * @throws InvalidClientException
     */
    @Override
    public ClientDetails loadClientByClientId(String clientId) throws InvalidClientException {
        cacheClientKey = this.cacheClientKey(clientId);
        // 从redis查询client信息
        ClientDetails clientDetails = (ClientDetails) redisTemplate.opsForValue().get(cacheClientKey);
        log.info("从 redis 缓存中查询客户端信息，clientId：{}，clientDetails：{}", clientId, clientDetails);
        // 如果redis中没查到，就从数据库查询，并同步至redis
        if (ObjectUtils.isEmpty(clientDetails)) {
            log.info("redis 中没有此客户端信息，继续从数据库中查找");
            clientDetails = syncCacheClient(clientId);
        }
        return clientDetails;
    }
    
    /**
     * 从数据库查询客户端信息，并同步到redis缓存
     * @param clientId 客户端id
     * @return
     */
    private ClientDetails syncCacheClient(String clientId){
        ClientDetails clientDetails = null;
        try {
            // 调用父类的 loadClientByClientId 方法，从数据库查询客户端信息
            clientDetails = super.loadClientByClientId(clientId);
            if (!ObjectUtils.isEmpty(clientDetails)){
                // 如果数据库存在该客户端信息，就同步到redis，下次直接从redis获取
                redisTemplate.opsForValue().set(cacheClientKey, clientDetails);
                log.info("将 clientId：{} 的客户端信息 clientDetails：{} 同步至 redis 缓存，key 为：{}",
                        clientId, clientDetails, cacheClientKey);
            }
        } catch (Exception e) {
            log.error("Exception for clientId：{}, message：{}", clientId, e.getMessage());
        }
        return clientDetails;
    }
    
    /**
     * 处理redis中客户端key
     * @param clientId 客户端id
     * @return
     */
    private String cacheClientKey(String clientId){
        // 表名+id作为redis key
        return Oauth2Constant.CLIENT_TABLE + ":" + clientId;
    }
}
