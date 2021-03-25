package com.riiyn.service;

import com.riiyn.entity.User;

import java.util.List;

/**
 * @author: riiyn
 * @date: 2021/3/24 15:03
 * @description:
 */
public interface UserService {
    User findByUsername(String username);
    User findById(Integer id);
    List<User> findAll();
}
