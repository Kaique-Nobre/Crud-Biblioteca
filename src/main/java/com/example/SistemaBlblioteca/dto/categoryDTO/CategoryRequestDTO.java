package com.example.SistemaBlblioteca.dto.categoryDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDTO(
        @NotBlank(message = "Category's name cannot be blank")
        @Schema(description = "Category's name", example = "FANTASY")
        String name
) {
}
