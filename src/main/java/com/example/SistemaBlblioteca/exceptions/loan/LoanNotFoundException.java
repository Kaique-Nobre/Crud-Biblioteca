package com.example.SistemaBlblioteca.exceptions.loan;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(String message) {
        super(message);
    }
}
