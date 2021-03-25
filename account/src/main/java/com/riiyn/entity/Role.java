package com.riiyn.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: riiyn
 * @date: 2021/3/24 13:44
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    private Integer id;
    private String roleName;
    private List<User> users;
    private List<Auth> auths;
}
