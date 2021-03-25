package com.riiyn.repository;

import com.riiyn.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: riiyn
 * @date: 2021/3/24 13:56
 * @description:
 */
@Repository
public interface UserRepository {
    User findByUsername(String username);
    User findById(Integer id);
    List<User> findAll();
}
