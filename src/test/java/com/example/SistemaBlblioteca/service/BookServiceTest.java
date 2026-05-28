package com.example.SistemaBlblioteca.service;

import com.example.SistemaBlblioteca.dto.bookDTO.BookRequestDTO;
import com.example.SistemaBlblioteca.dto.bookDTO.BookResponseDTO;
import com.example.SistemaBlblioteca.entity.Book;
import com.example.SistemaBlblioteca.entity.Category;
import com.example.SistemaBlblioteca.exceptions.book.BookAlreadyExistException;
import com.example.SistemaBlblioteca.exceptions.book.BookNotFoundException;
import com.example.SistemaBlblioteca.exceptions.book.BookUnavailableException;
import com.example.SistemaBlblioteca.exceptions.category.CategoryNotFoundException;
import com.example.SistemaBlblioteca.mapper.BookMapper;
import com.example.SistemaBlblioteca.repository.BookRepository;
import com.example.SistemaBlblioteca.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.example.SistemaBlblioteca.util.BookCreator.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void save_SaveBook_WhenSuccessfully() throws Exception{
        BookRequestDTO request = createBookRequestDTO();
        Book book = createBook();
        Category category = new Category("FILOSOFIA");

        when(bookRepository.existsByTitle(request.title())).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponseDTO savedBook = bookService.save(request);

        assertNotNull(savedBook);
        assertEquals(1L, savedBook.id());
        assertEquals(request.title(), savedBook.title());
        assertEquals("FILOSOFIA", savedBook.category());

        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void save_ThrowsException_WhenBookAlreadyExist() throws Exception{
        BookRequestDTO request = createBookRequestDTO();

        when(bookRepository.existsByTitle(request.title())).thenReturn(true);

        assertThrows(BookAlreadyExistException.class, () -> bookService.save(request));

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void save_ThrowsException_WhenCategoryNotFound() throws Exception{
        BookRequestDTO request = createBookRequestDTO();

        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> bookService.save(request));

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void findById_ReturnBook_WhenSuccessfully() throws Exception{
        Book book = createBook();

        BookResponseDTO response =  createBookResponseDTO();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDTO(book)).thenReturn(response);

        BookResponseDTO foundBook = bookService.findById(1L);

        assertNotNull(foundBook);
        assertEquals(1L ,foundBook.id());
        assertEquals(book.getTitle(),  foundBook.title());
        assertEquals(book.getCategory().getName(), foundBook.category());

        verify(bookRepository).findById(1L);
    }

    @Test
    void findById_ReturnException_WhenBookNotFound() throws Exception{
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.findById(1L));
    }

    @Test
    void findAll_ReturnListOfBooks_WhenSuccessfully() throws Exception{
        Book book = createBook();
        List<Book> books = List.of(book);

        when(bookMapper.toDTO(book)).thenReturn(createBookResponseDTO());
        when(bookRepository.findAll()).thenReturn(books);

        List<BookResponseDTO> response = bookService.findAll();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertFalse (response.isEmpty());
        assertEquals(book.getId() ,response.get(0).id());
        assertEquals(book.getTitle() ,response.get(0).title());
        assertEquals(book.isAvailable(), response.get(0).available());
    }

    @Test
    void findAll_ReturnEmptyList_WhenThereAreNoSavedBooks() throws Exception{
        List<Book> books = List.of();

        when(bookRepository.findAll()).thenReturn(books);

        List<BookResponseDTO> response = bookService.findAll();

        assertTrue(response.isEmpty());
    }

    @Test
    void update_UpdateBook_WhenSuccessfully() throws Exception{
        BookRequestDTO request = new BookRequestDTO("Nada Pode Me Ferir", 1L);
        Category category = new Category("BIOGRAFIA");
        BookResponseDTO response = new BookResponseDTO(1L, "Nada Pode Me Ferir", category.getName(), true);

        Book bookToPugrade = createBook();

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle(request.title());
        updatedBook.setCategory(category);
        updatedBook.setAvailable(true);

        when(categoryRepository.findById(request.category())).thenReturn(Optional.of(category));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(bookToPugrade));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);
        when(bookMapper.toDTO(any(Book.class))).thenReturn(response);

        BookResponseDTO update = bookService.update(1L, request);

        assertNotNull(update);
        assertEquals(1L, update.id());
        assertEquals(request.title(), update.title());
        assertEquals(category.getName(), update.category());

        verify(bookRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void update_ThrowsException_WhenCategoryNotFound() throws Exception{
        BookRequestDTO request = new BookRequestDTO("Nada Pode Me Ferir", 1L);

        when( categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> bookService.update(1L, request));

        verify(bookRepository, never()).findById(1L);
    }

    @Test
    void update_ThrowsException_WhenBookIsUnavailable() throws Exception{
        BookRequestDTO request = new BookRequestDTO("Nada Pode Me Ferir", 1L);
        Category category = new Category("BIOGRAFIA");
        Book bookToPugrade = new Book();

        bookToPugrade.setId(1L);
        bookToPugrade.setTitle("Meditações");
        bookToPugrade.setCategory(new Category("FILOSOFIA"));
        bookToPugrade.setAvailable(false);

        when(categoryRepository.findById(request.category())).thenReturn(Optional.of(category));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(bookToPugrade));

        assertThrows(BookUnavailableException.class, () -> bookService.update(1L, request));

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void delete_DeleteBook_WhenSuccessfully() throws Exception{
        Book book = createBook();

        doNothing().when(bookRepository).delete(book);

        bookRepository.delete(book);

        verify(bookRepository).delete(book);
    }

    @Test
    void delete_ThrowsException_WhenBookIsUnavailable() throws Exception{
        Book unavailableBook = new Book();
        unavailableBook.setAvailable(false);

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(unavailableBook));

        assertThrows(BookUnavailableException.class,
                () -> bookService.delete(1L));

        verify(bookRepository, never()).delete(any());
    }
}
