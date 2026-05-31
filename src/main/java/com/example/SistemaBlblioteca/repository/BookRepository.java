package com.example.SistemaBlblioteca.repository;

import com.example.SistemaBlblioteca.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByTitle(String title);
    Optional<Book> findByTitle(String title);

}
