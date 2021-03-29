package com.riiyn.controller;

import com.nimbusds.jose.jwk.RSAKey;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

/**
 * @author: riiyn
 * @date: 2021/3/26 21:49
 * @description: 开放RSA公钥接口
 */
@RestController
@AllArgsConstructor
@RequestMapping("/oauth")
public class PublicKeyController {
    
    private final KeyPair keyPair;
    
    @GetMapping("/getPublicKey")
    public Map<String, Object> getPublicKey(){
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        final RSAKey rsaKey = new RSAKey.Builder(rsaPublicKey).build();
        return rsaKey.toJSONObject();
    }
}
