package com.example.SistemaBlblioteca.integration;

import com.example.SistemaBlblioteca.dto.LoanDTO.LoanRequestDTO;
import com.example.SistemaBlblioteca.dto.bookDTO.BookRequestDTO;
import com.example.SistemaBlblioteca.dto.categoryDTO.CategoryRequestDTO;
import com.example.SistemaBlblioteca.entity.Book;
import com.example.SistemaBlblioteca.entity.Category;
import com.example.SistemaBlblioteca.repository.BookRepository;
import com.example.SistemaBlblioteca.repository.CategoryRepository;
import com.example.SistemaBlblioteca.repository.LoanRepository;
import com.example.SistemaBlblioteca.security.user.Role;
import com.example.SistemaBlblioteca.security.user.User;
import com.example.SistemaBlblioteca.security.user.UserRepository;
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

import static com.example.SistemaBlblioteca.util.BookCreator.createBookForIntegrationTests;
import static com.example.SistemaBlblioteca.util.UserCreator.createUserForIntegrationTests;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveCategory_ShouldAllowAccess_WhenUserIsAdmin() throws Exception {
        CategoryRequestDTO request = new CategoryRequestDTO("ROMANCE");

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    void saveCategory_ShouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {
        CategoryRequestDTO request = new CategoryRequestDTO("ROMANCE");

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void saveCategory_ShouldReturnUnauthorized_WhenUserIsNotAuthenticated() throws Exception {
        CategoryRequestDTO request = new CategoryRequestDTO("ROMANCE");

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveBook_ShouldAllowAccess_WhenUserIsAdmin() throws Exception {
        Category category = new Category("FILOSOFIA");
        categoryRepository.save(category);
        BookRequestDTO request = new BookRequestDTO("Meditações", category.getId());

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    void saveBook_ShouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {
        BookRequestDTO request = new BookRequestDTO("Meditações", 1L);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void saveBook_ShouldReturnUnauthorized_WhenUserIsNotAuthenticated() throws Exception {
        BookRequestDTO request = new BookRequestDTO("Meditações", 1L);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createLoan_ShouldAllowAccess_WhenUserIsAdmin() throws Exception {
        User admin = new User();
        admin.setName("admin");
        admin.setEmail("admin");
        admin.setPassword("admin");
        admin.setRole(Role.ROLE_ADMIN);
        userRepository.save(admin);

        Category category = new Category("FILOSOFIA");
        categoryRepository.save(category);

        Book book = createBookForIntegrationTests();
        book.setCategory(category);
        bookRepository.save(book);

        LoanRequestDTO request = new LoanRequestDTO(book.getId());

        mockMvc.perform(post("/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user@email.com", roles = "USER")
    void createLoan_ShouldAllowAccess_WhenUserIsAuthenticated() throws Exception {
        User user = createUserForIntegrationTests();
        userRepository.save(user);

        Category category = new Category("FILOSOFIA");
        categoryRepository.save(category);

        Book book = createBookForIntegrationTests();
        book.setCategory(category);
        bookRepository.save(book);

        LoanRequestDTO request = new LoanRequestDTO(book.getId());

        mockMvc.perform(post("/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void createLoan_ShouldReturnUnauthorized_WhenUserIsNotAuthenticated() throws Exception {
        LoanRequestDTO request = new LoanRequestDTO(1L);

        mockMvc.perform(post("/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
