package com.example.SistemaBlblioteca.exceptions;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String title,
        String message
) {
}
