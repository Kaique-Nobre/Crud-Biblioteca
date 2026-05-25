package com.example.SistemaBlblioteca.exceptions;

import com.example.SistemaBlblioteca.exceptions.book.BookAlreadyExistException;
import com.example.SistemaBlblioteca.exceptions.book.BookNotFoundException;
import com.example.SistemaBlblioteca.exceptions.book.BookUnavailableException;
import com.example.SistemaBlblioteca.exceptions.category.CategoryAlreadyExistException;
import com.example.SistemaBlblioteca.exceptions.category.CategoryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(com.example.SistemaBlblioteca.exceptions.BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException exception) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Bad Request")
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFoundException(BookNotFoundException ex){
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .title("Book not found")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BookAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleBookAlreadyExistException(BookAlreadyExistException ex){
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .title("Book already exist")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(BookUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleBookUnavailableException(BookUnavailableException ex){
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NO_CONTENT .value())
                .title("The book is currently unavailable")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFoundException(CategoryNotFoundException ex){
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .title("Category not found")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CategoryAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleCategoryAlreadyExistException(CategoryAlreadyExistException ex){
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .title("Category already exist")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
