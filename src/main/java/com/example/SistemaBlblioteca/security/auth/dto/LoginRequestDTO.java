package com.example.SistemaBlblioteca.security.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "Email cannot be null")
        String email,

        @NotBlank(message = "Password cannot be null")
        String password
) {
}
