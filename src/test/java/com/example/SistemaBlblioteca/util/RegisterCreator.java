package com.example.SistemaBlblioteca.util;

import com.example.SistemaBlblioteca.security.auth.dto.RegisterDTO;

public class RegisterCreator {
    public static RegisterDTO createRegister() {
        return new RegisterDTO("username", "user@email.com", "password");
    }
}
