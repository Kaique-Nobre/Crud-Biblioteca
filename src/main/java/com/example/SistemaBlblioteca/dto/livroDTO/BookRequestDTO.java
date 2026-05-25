package com.example.SistemaBlblioteca.dto.livroDTO;

import jakarta.validation.constraints.NotBlank;

public record BookRequestDTO(
        @NotBlank(message = "Title cannot be empty")
        String title,

        @NotBlank(message = "Category cannot be empty")
        Long categoryId
) {
}
