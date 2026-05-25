package com.example.SistemaBlblioteca.dto.livroDTO;

public record BookResponseDTO(
        Long id,
        String title,
        String category,
        boolean available
) {
}
