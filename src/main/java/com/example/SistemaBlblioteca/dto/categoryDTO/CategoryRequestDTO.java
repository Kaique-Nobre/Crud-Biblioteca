package com.example.SistemaBlblioteca.dto.categoryDTO;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDTO(
        @NotBlank(message = "Category's name cannot be blank")
        String name
) {
}
