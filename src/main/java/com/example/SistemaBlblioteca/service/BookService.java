package com.example.SistemaBlblioteca.service;

import com.example.SistemaBlblioteca.dto.livroDTO.BookRequestDTO;
import com.example.SistemaBlblioteca.dto.livroDTO.BookResponseDTO;
import com.example.SistemaBlblioteca.entity.Book;
import com.example.SistemaBlblioteca.exceptions.BookAlreadyExistException;
import com.example.SistemaBlblioteca.exceptions.BookNotFoundException;
import com.example.SistemaBlblioteca.mapper.BookMapper;
import com.example.SistemaBlblioteca.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookResponseDTO save(BookRequestDTO bookDTO) {
        if(bookRepository.existsByTitle(bookDTO.title())) {
            throw new BookAlreadyExistException("Book already exists");
        }
        Book book = bookMapper.toEntity(bookDTO);
        bookRepository.save(book);
        return bookMapper.toDTO(book);
    }

    public BookResponseDTO findByTitle(String title) {
        Book book = bookRepository.findByTitle(title).orElseThrow(() -> new BookNotFoundException("Book not found"));
        return bookMapper.toDTO(book);
    }

    public BookResponseDTO findById(Long id) {
        Book book = getBook(id);
        return bookMapper.toDTO(book);
    }

    public List<BookResponseDTO> findAll() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(bookMapper::toDTO).toList();
    }

    public BookResponseDTO update(Long id, BookRequestDTO bookDTO) {
        Book book = getBook(id);

        book.setTitle(bookDTO.title());
        book.setCategory(bookDTO.category_id());

        bookRepository.save(book);
        return bookMapper.toDTO(book);
    }

    public void delete(Long id) {
        Book book = getBook(id);
        bookRepository.delete(book);
    }

    private Book getBook(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found"));
    }
}
