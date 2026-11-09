package com.example.e_commerce.entity;

import java.util.Set;

public interface AuthUser {
    String getUsernameOrEmail();
    String getPassword();
    String getUserType();
    Set<Role> getRoles();
}
