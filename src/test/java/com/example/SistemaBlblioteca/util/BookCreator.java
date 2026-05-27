package com.example.SistemaBlblioteca.util;

import com.example.SistemaBlblioteca.dto.livroDTO.BookRequestDTO;
import com.example.SistemaBlblioteca.dto.livroDTO.BookResponseDTO;
import com.example.SistemaBlblioteca.entity.Book;
import com.example.SistemaBlblioteca.entity.Category;

import static com.example.SistemaBlblioteca.util.CategoryCreator.createCategory;

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

    public static BookResponseDTO createBookResponseDTO(){
        return new BookResponseDTO(1L, "Meditações", "FILOSOFIA", true);
    }

}
