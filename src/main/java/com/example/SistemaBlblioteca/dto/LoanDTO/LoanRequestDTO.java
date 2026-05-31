package com.example.SistemaBlblioteca.dto.LoanDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record LoanRequestDTO(
        @NotNull(message = "Book cannot be null")
        @Schema(description = "id of the book the user wants to borrow", example = "2")
        Long book
) {
}
