package com.example.SistemaBlblioteca.controller;

import com.example.SistemaBlblioteca.dto.bookDTO.BookRequestDTO;
import com.example.SistemaBlblioteca.dto.bookDTO.BookResponseDTO;
import com.example.SistemaBlblioteca.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "Books")
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class BookController {
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a book", description = "Only admin can create books")
    public BookResponseDTO save(@Valid @RequestBody BookRequestDTO bookDTO) {
        return bookService.save(bookDTO);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all available books")
    public List<BookResponseDTO> findAll(){
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Return a books by ID")
    public BookResponseDTO findById(@PathVariable Long id){
        return bookService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a book", description = "Only admin can update books, an unavailable book can't be updated")
    public BookResponseDTO update(@PathVariable Long id, @Valid @RequestBody BookRequestDTO bookDTO) {
        return bookService.update(id, bookDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a book", description = "Only admin can delete books, an unavailable book can't be deleted")
    public void delete(@PathVariable Long id){
        bookService.delete(id);
    }
}
