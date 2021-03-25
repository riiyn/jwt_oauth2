package com.riiyn.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author: riiyn
 * @date: 2021/3/24 14:24
 * @description:
 */
@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void findByUsername() {
        System.out.println(userRepository.findByUsername("admin"));
    }
    
    @Test
    void findById() {
        System.out.println(userRepository.findById(2));
    }
    
    @Test
    void findAll() {
        userRepository.findAll().forEach(System.out::println);
    }
}