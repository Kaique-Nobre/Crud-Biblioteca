package com.example.SistemaBlblioteca.util;

import com.example.SistemaBlblioteca.dto.bookDTO.BookRequestDTO;
import com.example.SistemaBlblioteca.dto.bookDTO.BookResponseDTO;
import com.example.SistemaBlblioteca.entity.Book;
import com.example.SistemaBlblioteca.entity.Category;

public class BookCreator {
    public static BookRequestDTO createBookRequestDTO() {
        return new BookRequestDTO("Meditações", 1L);
    }

    public static Book createBook(){
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Meditações");
        book.setCategory(new Category("FILOSOFIA"));
        book.setAvailable(true);
        return book;
    }

    public static Book createBookForIntegrationTests(){
        Book book = new Book();
        book.setTitle("Noites Brancas");
        book.setAvailable(true);
        return book;
    }

    public static BookResponseDTO createBookResponseDTO(){
        return new BookResponseDTO(1L, "Meditações", "FILOSOFIA", true);
    }

}
