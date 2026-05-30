package com.example.SistemaBlblioteca.dto.LoanDTO;

import jakarta.validation.constraints.NotNull;

public record LoanRequestDTO(
        @NotNull(message = "Book cannot be null")
        Long book
) {
}
