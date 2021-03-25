package com.riiyn.feign;

import com.riiyn.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author: riiyn
 * @date: 2021/3/24 15:12
 * @description:
 */
@FeignClient(value = "account", path = "user")
public interface UserFeignClient {
    @GetMapping("/getByUsername/{username}")
    User getByUsername(@PathVariable String username);
}
