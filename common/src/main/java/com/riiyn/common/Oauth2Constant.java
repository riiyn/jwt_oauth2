package com.riiyn.common;

import org.springframework.stereotype.Component;

/**
 * @author: riiyn
 * @date: 2021/3/29 21:47
 * @description: 认证常用相关常量
 */
@Component
public class Oauth2Constant {
    /**
     * 客户端表名称
     */
    public static final String CLIENT_TABLE = "oauth_client_details";
    private static final String CLIENT_SECRET_PREFIX = "'{noop}'";
    /**
     * 客户端基本信息
     */
    public static final String CLIENT_DETAILS_FIELDS = "client_id, CONCAT(" + CLIENT_SECRET_PREFIX + ",client_secret) as client_secret, resource_ids, " +
            "scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, " +
            "additional_information, autoapprove";
    /**
     * 客户端信息基本查询语句
     */
    public static final String BASE_CLIENT_DETAILS_SQL = "select " + CLIENT_DETAILS_FIELDS + " from " + CLIENT_TABLE;
    /**
     * 查找所有客户端信息
     */
    public static final String FIND_CLIENT_DETAILS_SQL = BASE_CLIENT_DETAILS_SQL + " order by client_id";
    /**
     * 根据客户端id查找
     */
    public static final String SELECT_CLIENT_DETAILS_SQL = BASE_CLIENT_DETAILS_SQL + " where client_id = ?";
    /**
     * 认证类型参数
     */
    public static final String GRANT_TYPE = "grant_type";
    /**
     * 认证类型参数：刷新token
     */
    public static final String REFRESH_TOKEN = "refresh_token";
    /**
     * 认证类型参数：密码模式
     */
    public static final String PASSWORD = "password";
    /**
     * 用户角色
     */
    public static final String USER_ROLES = "user_roles";
    /**
     * 秘钥文件
     */
    public static final String ENCRYPT_KEY_FILE = "riiyn.jks";
    /**
     * 秘钥文件别名
     */
    public static final String ENCRYPT_KEY_ALIAS = "riiyn";
    /**
     * 秘钥口令
     */
    public static final String ENCRYPT_KEY_PASSWORD = "102323";
    
    public static final Integer ENABLE = 0;
    
    public static final Integer DISABLE = 1;
    
    public static final String JWT_PAYLOAD_KEY = "payload";
    
    public static final String JWT_ATI_KEY = "ati";
    
    public static final String JWT_JTI_KEY = "jti";
    
    public static final String JWT_EXP_KEY = "exp";
    
    public static final String TOKEN_BLACKLIST_PREFIX = "auth:token:blacklist:";
    
    public static final String AUTHORIZATION = "Authorization";
    
    public static final String TOKEN_TYPE = "Bearer ";
    
    public static final String DEFAULT_SUCCESS_MESSAGE = "处理成功";
    
    public static final String DEFAULT_NULL_MESSAGE = "数据为空";
    
    public static final String DEFAULT_FAIL_MESSAGE = "处理失败";
    
    /**
     * json类型报文，UTF-8字符集
     */
    public static final String JSON_UTF8 = "application/json;charset=UTF-8";
    
    public static final String REDIS_TOKEN_KEY_PREFIX = "auth:";
}
