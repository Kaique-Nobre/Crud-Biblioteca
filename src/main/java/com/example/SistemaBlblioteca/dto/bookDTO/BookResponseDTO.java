package com.example.SistemaBlblioteca.dto.bookDTO;

public record BookResponseDTO(
        Long id,
        String title,
        String category,
        boolean available
) {
}
