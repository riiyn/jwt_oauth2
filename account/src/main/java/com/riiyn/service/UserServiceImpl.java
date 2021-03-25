package com.riiyn.service;

import com.riiyn.entity.User;
import com.riiyn.repository.UserRepository;
import com.riiyn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: riiyn
 * @date: 2021/3/24 15:04
 * @description:
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public User findById(Integer id) {
        return userRepository.findById(id);
    }
    
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
