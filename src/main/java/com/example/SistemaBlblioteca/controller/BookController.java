package com.example.SistemaBlblioteca.controller;

import com.example.SistemaBlblioteca.dto.livroDTO.BookRequestDTO;
import com.example.SistemaBlblioteca.dto.livroDTO.BookResponseDTO;
import com.example.SistemaBlblioteca.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping
    public BookResponseDTO save(@Valid @RequestBody BookRequestDTO bookDTO) {
        return bookService.save(bookDTO);
    }

    @GetMapping
    public List<BookResponseDTO> findAll(){
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public BookResponseDTO findById(@PathVariable Long id){
        return bookService.findById(id);
    }

    @PutMapping("/{id}")
    public BookResponseDTO update(@PathVariable Long id, @Valid @RequestBody BookRequestDTO bookDTO) {
        return bookService.update(id, bookDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        bookService.delete(id);
    }
}
