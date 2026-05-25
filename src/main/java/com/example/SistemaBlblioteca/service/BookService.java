package com.example.SistemaBlblioteca.service;

import com.example.SistemaBlblioteca.dto.livroDTO.BookRequestDTO;
import com.example.SistemaBlblioteca.dto.livroDTO.BookResponseDTO;
import com.example.SistemaBlblioteca.entity.Book;
import com.example.SistemaBlblioteca.entity.Category;
import com.example.SistemaBlblioteca.exceptions.BookAlreadyExistException;
import com.example.SistemaBlblioteca.exceptions.BookNotFoundException;
import com.example.SistemaBlblioteca.exceptions.BookUnavailableException;
import com.example.SistemaBlblioteca.exceptions.CategoryNotFoundException;
import com.example.SistemaBlblioteca.mapper.BookMapper;
import com.example.SistemaBlblioteca.repository.BookRepository;
import com.example.SistemaBlblioteca.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookResponseDTO save(BookRequestDTO bookDTO) {
        if(bookRepository.existsByTitle(bookDTO.title())) {
            throw new BookAlreadyExistException("Book with name: " +bookDTO.title()+ " already exists");
        }

        Category category = categoryRepository.findById(bookDTO.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category with id: " +bookDTO.categoryId()+ " was not found"));

        Book book = new  Book();
        book.setTitle(bookDTO.title());
        book.setCategory(category);
        book.setAvailable(true);
        Book savedBook = bookRepository.save(book);

        return new BookResponseDTO(
                savedBook.getId(),
                savedBook.getTitle(),
                savedBook.getCategory().getName(),
                savedBook.isAvailable()
        );
    }

    public BookResponseDTO findByTitle(String title) {
        Book book = bookRepository.findByTitle(title).orElseThrow(() -> new BookNotFoundException("Book not found with title: " + title));
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
        Category category = categoryRepository.findById(bookDTO.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category with id: " +bookDTO.categoryId()+ " was not found"));

        Book book = getBook(id);
        if (!book.isAvailable()) {
            throw new BookUnavailableException("Unavailable books cannot be updated");
        }

        book.setTitle(bookDTO.title());
        book.setCategory(category);
        book.setAvailable(true);

        bookRepository.save(book);
        return bookMapper.toDTO(book);
    }

    public void delete(Long id) {
        Book book = getBook(id);
        if (!book.isAvailable()) {
            throw new BookUnavailableException("Unavailable books cannot be deleted");
        }

        bookRepository.delete(book);
    }

    private Book getBook(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
    }
}
