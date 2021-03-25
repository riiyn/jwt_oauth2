package com.riiyn.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: riiyn
 * @date: 2021/3/24 13:46
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Auth {
    private Integer id;
    private String authorities;
    private List<Role> roles;
}
