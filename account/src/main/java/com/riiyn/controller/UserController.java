package com.riiyn.controller;

import com.riiyn.entity.User;
import com.riiyn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: riiyn
 * @date: 2021/3/24 15:05
 * @description:
 */
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/getByUsername/{username}")
    public User getByUsername(@PathVariable String username){
        return userService.findByUsername(username);
    }
    
    @GetMapping("/getById/{id}")
    public User getById(@PathVariable Integer id){
        return userService.findById(id);
    }
    
    @GetMapping("/getAll")
    public List<User> getAll(){
        return userService.findAll();
    }
}
