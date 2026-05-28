package com.example.SistemaBlblioteca.security.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(
        @NotBlank(message = "Name cannot be null")
        String name,

        @NotBlank(message = "Email cannot be null")
        String email,

        @NotBlank(message = "Password cannot be null")
        String password
) {
}
