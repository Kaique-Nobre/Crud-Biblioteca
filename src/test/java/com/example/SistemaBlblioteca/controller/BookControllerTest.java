package com.example.SistemaBlblioteca.controller;

import com.example.SistemaBlblioteca.dto.livroDTO.BookRequestDTO;
import com.example.SistemaBlblioteca.dto.livroDTO.BookResponseDTO;
import com.example.SistemaBlblioteca.exceptions.book.BookAlreadyExistException;
import com.example.SistemaBlblioteca.exceptions.book.BookNotFoundException;
import com.example.SistemaBlblioteca.exceptions.book.BookUnavailableException;
import com.example.SistemaBlblioteca.exceptions.category.CategoryNotFoundException;
import com.example.SistemaBlblioteca.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.example.SistemaBlblioteca.util.BookCreator.createBookRequestDTO;
import static com.example.SistemaBlblioteca.util.BookCreator.createBookResponseDTO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @Test
    void save_SaveBook_WhenSuccessfully() throws Exception{
        BookRequestDTO request = createBookRequestDTO();
        BookResponseDTO response = createBookResponseDTO();

        when(bookService.save(any(BookRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Meditações"))
                .andExpect(jsonPath("$.category").value("FILOSOFIA"));
    }

    @Test
    void save_ThrowsException_WhenBookAlreadyExist() throws Exception{
        BookRequestDTO request = createBookRequestDTO();

        when(bookService.save(any(BookRequestDTO.class))).thenThrow(new BookAlreadyExistException("Book already exist"));

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Book already exist"));
    }

    @Test
    void save_ThrowsException_WhenBookNameIsEmpty() throws Exception{
        BookRequestDTO request = new BookRequestDTO("", 1L);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findById_ReturnBook_WhenSuccessfully() throws Exception{
        BookResponseDTO response = createBookResponseDTO();

        when(bookService.findById(anyLong())).thenReturn(response);

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Meditações"))
                .andExpect(jsonPath("$.category").value("FILOSOFIA"));
    }

    @Test
    void findById_ThrowsException_WhenBookNotFound() throws Exception{
        when(bookService.findById(anyLong())).thenThrow(new BookNotFoundException("Book not found"));

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_ReturnListOfBooks_WhenSuccessfully() throws Exception{
        BookResponseDTO response = createBookResponseDTO();
        List<BookResponseDTO> responseList = List.of(response);

        when(bookService.findAll()).thenReturn(responseList);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Meditações"));
    }

    @Test
    void findAll_ReturnEmptyList_WhenThereAreNoSavedBooks() throws Exception{
        when(bookService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void update_UpdateBook_WhenSuccessfully() throws Exception{
        BookRequestDTO request = new BookRequestDTO("Nada Pode Me Ferir", 2L);
        BookResponseDTO response = new BookResponseDTO(1L, "Nada Pode Me Ferir", "BIOGRAFIA", true);

        when(bookService.update(eq(1L), any(BookRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Nada Pode Me Ferir"))
                .andExpect(jsonPath("$.category").value("BIOGRAFIA"));
    }

    @Test
    void update_ThrowsException_WhenCategoryNotFound() throws Exception{
        BookRequestDTO request = new BookRequestDTO("Nada Pode Me Ferir", 2L);

        when(bookService.update(eq(1L), any(BookRequestDTO.class))).thenThrow(new CategoryNotFoundException("Category not found"));

        mockMvc.perform(put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_ThrowsException_WhenBookNotFound() throws Exception{
        BookRequestDTO request = new BookRequestDTO("Nada Pode Me Ferir", 2L);

        when(bookService.update(eq(1L), any(BookRequestDTO.class))).thenThrow(new CategoryNotFoundException("Book not found"));

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_ThrowsException_WhenBookIsUnavailable() throws Exception{
        BookRequestDTO request = new BookRequestDTO("Nada Pode Me Ferir", 2L);

        when(bookService.update(eq(1L), any(BookRequestDTO.class))).thenThrow(new BookUnavailableException("Cannot update an unavailable book"));

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void delete_DeleteBook_WhenSuccessfully() throws Exception{
        doNothing().when(bookService).delete(anyLong());

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_ThrowsException_WhenBookIsUnavailable() throws Exception{
        doThrow(new BookUnavailableException("Cannot delete an unavailable book")).when(bookService).delete(anyLong());

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isConflict());
    }
}
