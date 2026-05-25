package com.example.SistemaBlblioteca.exceptions.book;

public class BookAlreadyExistException extends RuntimeException {
    public BookAlreadyExistException(String message) {
        super(message);
    }
}
