package com.example.SistemaBlblioteca.util;

import com.example.SistemaBlblioteca.security.user.Role;
import com.example.SistemaBlblioteca.security.user.User;

public class UserCreator {
    public static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user.email.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);
        return user;
    }
}
