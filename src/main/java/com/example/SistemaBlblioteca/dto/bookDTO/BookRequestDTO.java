package com.example.SistemaBlblioteca.dto.bookDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookRequestDTO(
        @NotBlank(message = "Title cannot be empty")
        @Schema(description = "Book's title", example = "The Little Prince")
        String title,

        @NotNull(message = "Category cannot be empty")
        @Schema(description = "Category ID", example = "1")
        Long category
) {
}
