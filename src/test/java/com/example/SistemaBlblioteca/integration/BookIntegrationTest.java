package com.example.SistemaBlblioteca.integration;

import com.example.SistemaBlblioteca.dto.bookDTO.BookRequestDTO;
import com.example.SistemaBlblioteca.entity.Book;
import com.example.SistemaBlblioteca.entity.Category;
import com.example.SistemaBlblioteca.repository.BookRepository;
import com.example.SistemaBlblioteca.repository.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.example.SistemaBlblioteca.util.BookCreator.createBookForIntegrationTests;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BookIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void cleanDatabase() {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void save_ShouldSaveBook_WhenSuccessfully() throws Exception {
        Category category = new Category();
        category.setName("FILOSOFIA");

        categoryRepository.save(category);

        BookRequestDTO request = new BookRequestDTO("Meditações", category.getId());

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Meditações"))
                .andExpect(jsonPath("$.category").value("FILOSOFIA"));

        List<Book> books = bookRepository.findAll();

        assertEquals(1, books.size());

        assertEquals("Meditações", books.get(0).getTitle());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void save_ShouldReturnConflict_WhenBookAlreadyExists() throws Exception {
        Category category = new Category();
        category.setName("ROMANCE");

        categoryRepository.save(category);

        Book book = createBookForIntegrationTests();
        book.setCategory(category);
        bookRepository.save(book);

        BookRequestDTO request = new BookRequestDTO("Noites Brancas", category.getId());

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Book already exist"));

        assertEquals(1, bookRepository.count());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void save_ShouldReturnNotFound_WhenCategoryDoesNotExist() throws Exception {
        BookRequestDTO request = new BookRequestDTO("Meditações", 99L);

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Category not found"));

        assertEquals(0, bookRepository.count());
    }

    @Test
    @WithMockUser
    void findById_SouldReturnBook_WhenSuccessfully() throws Exception {
        Category category = new Category();
        category.setName("ROMANCE");
        categoryRepository.save(category);

        Book book = createBookForIntegrationTests();
        book.setCategory(category);
        bookRepository.save(book);

        mockMvc.perform(get("/books/{id}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Noites Brancas"))
                .andExpect(jsonPath("$.category").value("ROMANCE"));
    }

    @Test
    @WithMockUser
    void findById_SouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
        mockMvc.perform(get("/books/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Book not found"));
    }

    @Test
    @WithMockUser
    void findAll_ReturnListOfBooks_WhenSuccessfully() throws Exception {
        Category category = new Category();
        category.setName("ROMANCE");
        categoryRepository.save(category);

        Book book = createBookForIntegrationTests();
        book.setCategory(category);
        bookRepository.save(book);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Noites Brancas"));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldUpdateBook_WhenSuccessfully() throws Exception {
        Category category = new Category();
        category.setName("ROMANCE");
        categoryRepository.save(category);

        Book book = createBookForIntegrationTests();
        book.setCategory(category);
        bookRepository.save(book);

        BookRequestDTO request = new BookRequestDTO("update", category.getId());

        mockMvc.perform(put("/books/{id}", book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("update"));
        assertEquals("update", bookRepository.findById(book.getId()).get().getTitle());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldUpdateNotFound_WhenCategoryDoesNotExist() throws Exception {
        BookRequestDTO request = new BookRequestDTO("update", 99L);

        mockMvc.perform(put("/books/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Category not found"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldReturnConflict_WhenBookIsNotAvailable() throws Exception {
        Category category = new Category();
        category.setName("ROMANCE");
        categoryRepository.save(category);

        Book book = new Book();
        book.setTitle("Noites Brancas");
        book.setCategory(category);
        book.setAvailable(false);
        bookRepository.save(book);

        BookRequestDTO request = new BookRequestDTO("update", category.getId());

        mockMvc.perform(put("/books/{id}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("The book is currently unavailable"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldDeleteBook_WhenSuccessfully() throws Exception {
        Category category = new Category();
        category.setName("ROMANCE");
        categoryRepository.save(category);

        Book book = createBookForIntegrationTests();
        book.setCategory(category);
        bookRepository.save(book);

        mockMvc.perform(delete("/books/{id}", book.getId()))
                .andExpect(status().isNoContent());

        assertEquals(0, bookRepository.count());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturnConflict_WhenBookIsNotAvailable() throws Exception {
        Category category = new Category();
        category.setName("ROMANCE");
        categoryRepository.save(category);

        Book book = new Book();
        book.setTitle("Noites Brancas");
        book.setCategory(category);
        book.setAvailable(false);
        bookRepository.save(book);

        mockMvc.perform(delete("/books/{id}", book.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("The book is currently unavailable"));

        assertEquals(1, bookRepository.count());
    }
}
