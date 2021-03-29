package com.riiyn.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * @author: riiyn
 * @date: 2021/3/28 22:39
 * @description:
 */
@FeignClient(value = "auth", path = "oauth")
public interface AuthFeign {
    
    @GetMapping("/getPublicKey")
    Map<String, Object> getPublicKey();
}
