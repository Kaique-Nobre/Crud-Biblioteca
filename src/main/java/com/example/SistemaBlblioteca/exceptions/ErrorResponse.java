package com.example.SistemaBlblioteca.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class ErrorResponse {
    private String title;
    private int status;
    private String message;
    private LocalDateTime timestamp;
}
