package com.example.SistemaBlblioteca.controller;

import com.example.SistemaBlblioteca.dto.bookDTO.BookRequestDTO;
import com.example.SistemaBlblioteca.dto.bookDTO.BookResponseDTO;
import com.example.SistemaBlblioteca.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public BookResponseDTO save(@Valid @RequestBody BookRequestDTO bookDTO) {
        return bookService.save(bookDTO);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<BookResponseDTO> findAll(){
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public BookResponseDTO findById(@PathVariable Long id){
        return bookService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BookResponseDTO update(@PathVariable Long id, @Valid @RequestBody BookRequestDTO bookDTO) {
        return bookService.update(id, bookDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        bookService.delete(id);
    }
}
