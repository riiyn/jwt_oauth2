package com.riiyn.controller;

import com.riiyn.entity.User;
import com.riiyn.feign.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: riiyn
 * @date: 2021/3/24 17:22
 * @description:
 */
@RestController
public class UserController {
    @Autowired
    private UserFeignClient userFeignClient;
    
    @GetMapping("/getByUsername/{username}")
    public User getByUsername(@PathVariable String username){
        return userFeignClient.getByUsername(username);
    }
}
