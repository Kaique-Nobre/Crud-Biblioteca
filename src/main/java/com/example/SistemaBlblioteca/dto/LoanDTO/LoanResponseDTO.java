package com.example.SistemaBlblioteca.dto.LoanDTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record LoanResponseDTO(
        Long id,
        String user,
        String book,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate loanDate
) {
}
