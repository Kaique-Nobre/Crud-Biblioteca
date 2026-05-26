package com.example.SistemaBlblioteca.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@SuperBuilder
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> fields;
}
