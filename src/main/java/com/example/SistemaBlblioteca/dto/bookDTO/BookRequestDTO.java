package com.example.SistemaBlblioteca.dto.bookDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookRequestDTO(
        @NotBlank(message = "Title cannot be empty")
        String title,

        @NotNull(message = "Category cannot be empty")
        Long category
) {
}
