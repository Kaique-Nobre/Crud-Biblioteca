package com.example.SistemaBlblioteca.integration;

import com.example.SistemaBlblioteca.dto.LoanDTO.LoanRequestDTO;
import com.example.SistemaBlblioteca.entity.Book;
import com.example.SistemaBlblioteca.entity.Category;
import com.example.SistemaBlblioteca.entity.Loan;
import com.example.SistemaBlblioteca.repository.BookRepository;
import com.example.SistemaBlblioteca.repository.CategoryRepository;
import com.example.SistemaBlblioteca.repository.LoanRepository;
import com.example.SistemaBlblioteca.security.user.Role;
import com.example.SistemaBlblioteca.security.user.User;
import com.example.SistemaBlblioteca.security.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.example.SistemaBlblioteca.util.BookCreator.createBookForIntegrationTests;
import static com.example.SistemaBlblioteca.util.CategoryCreator.createCategoryForIntegrationTests;
import static com.example.SistemaBlblioteca.util.UserCreator.createUserForIntegrationTests;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoanIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private UserRepository userRepository;
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
    @WithMockUser(username = "user@email.com", roles = "USER")
    void save_ShouldCreateLoan_WhenSuccessfully() throws Exception {
        User user = createUserForIntegrationTests();
        userRepository.save(user);

        Category category = createCategoryForIntegrationTests();
        categoryRepository.save(category);

        Book book = createBookForIntegrationTests();
        book.setCategory(category);
        bookRepository.save(book);

        LoanRequestDTO request = new LoanRequestDTO(book.getId());

        mockMvc.perform(post("/loan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        Optional<Book> updatedBook = bookRepository.findById(book.getId());
        Optional<Loan> loan = loanRepository.findAll().stream().findFirst();

        assertEquals(1, loanRepository.count());
        assertFalse(updatedBook.get().isAvailable());
        assertEquals(user.getId(), loan.get().getUser().getId());
    }

    @Test
    @WithMockUser(username = "user@email.com", roles = "USER")
    void save_ShouldReturnNotFound_WhenBookNotFound() throws Exception {
        User user = createUserForIntegrationTests();
        userRepository.save(user);

        LoanRequestDTO request = new LoanRequestDTO(1L);

        mockMvc.perform(post("/loan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        assertEquals(0, bookRepository.count());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void save_ShouldReturnConflict_WhenBookIsNotAvailable() throws Exception {
        User user = createUserForIntegrationTests();
        userRepository.save(user);

        Category category = createCategoryForIntegrationTests();
        categoryRepository.save(category);

        Book book = createBookForIntegrationTests();
        book.setCategory(category);
        book.setAvailable(false);
        bookRepository.save(book);

        LoanRequestDTO request = new LoanRequestDTO(book.getId());

        System.out.println(book.getId());

        mockMvc.perform(post("/loan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("The book is currently unavailable"));


        assertEquals(0, loanRepository.count());
    }

    @Test
    @WithMockUser(username = "user1@email.com", roles = "USER")
    void findAll_ReturnAllUserLoans_WhenIsUserFinding() throws Exception {
        User user1 = createUserForIntegrationTests();
        user1.setName("user1");
        user1.setEmail("user1@email.com");
        userRepository.save(user1);

        User user2 = createUserForIntegrationTests();
        user2.setName("user2");
        user2.setEmail("user2@email.com");
        userRepository.save(user2);

        Category category = createCategoryForIntegrationTests();
        categoryRepository.save(category);

        Book book1 = createBookForIntegrationTests();
        book1.setCategory(category);
        bookRepository.save(book1);

        Book book2 = createBookForIntegrationTests();
        book2.setTitle("book2");
        book2.setCategory(category);
        bookRepository.save(book2);

        Loan loan1 = new Loan();
        loan1.setUser(user1);
        loan1.setBook(book1);
        loanRepository.save(loan1);

        Loan loan2 = new Loan();
        loan2.setUser(user2);
        loan2.setBook(book2);
        loanRepository.save(loan2);

        mockMvc.perform(get("/loan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.[0].user").value("user1@email.com"))
                .andExpect(jsonPath("$[0].book").value("Noites Brancas"));

        assertEquals(1, loanRepository.findAllByUser(user1).size());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findAll_ReturnAllLoans_WhenIsAdminFinding() throws Exception {
        User admin = new User();
        admin.setName("admin");
        admin.setEmail("admin");
        admin.setPassword("admin");
        admin.setRole(Role.ROLE_ADMIN);
        userRepository.save(admin);

        User user1 = createUserForIntegrationTests();
        user1.setName("user1");
        user1.setEmail("user1@email.com");
        userRepository.save(user1);

        User user2 = createUserForIntegrationTests();
        user2.setName("user2");
        user2.setEmail("user2@email.com");
        userRepository.save(user2);

        Category category = createCategoryForIntegrationTests();
        categoryRepository.save(category);

        Book book1 = createBookForIntegrationTests();
        book1.setCategory(category);
        bookRepository.save(book1);

        Book book2 = createBookForIntegrationTests();
        book2.setTitle("book2");
        book2.setCategory(category);
        bookRepository.save(book2);

        Loan loan1 = new Loan();
        loan1.setUser(user1);
        loan1.setBook(book1);
        loanRepository.save(loan1);

        Loan loan2 = new Loan();
        loan2.setUser(user2);
        loan2.setBook(book2);
        loanRepository.save(loan2);

        mockMvc.perform(get("/loan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].book").value("Noites Brancas"))
                .andExpect(jsonPath("$[1].book").value("book2"));

        assertEquals(2, loanRepository.count());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findById_ReturnsLoan_WhenSuccessfully() throws Exception {
        User admin = new User();
        admin.setName("admin");
        admin.setEmail("admin");
        admin.setPassword("admin");
        admin.setRole(Role.ROLE_ADMIN);
        userRepository.save(admin);

        User user = createUserForIntegrationTests();
        userRepository.save(user);

        Category category = createCategoryForIntegrationTests();
        categoryRepository.save(category);

        Book book = createBookForIntegrationTests();
        book.setCategory(category);
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loanRepository.save(loan);

        mockMvc.perform(get("/loan/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(loan.getId()))
                .andExpect(jsonPath("$.book").value("Noites Brancas"));
        assertEquals(user.getId(), loanRepository.findById(1L).get().getUser().getId());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findById_ReturnsNotFound_WhenLoanDoesNotExist() throws Exception {
        User admin = new User();
        admin.setName("admin");
        admin.setEmail("admin");
        admin.setPassword("admin");
        admin.setRole(Role.ROLE_ADMIN);
        userRepository.save(admin);

        mockMvc.perform(get("/loan/admin/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void delete_ShouldDeleteLoan_WhenSuccessfully() throws Exception {
        User admin = new User();
        admin.setName("admin");
        admin.setEmail("admin");
        admin.setPassword("admin");
        admin.setRole(Role.ROLE_ADMIN);
        userRepository.save(admin);

        User user = createUserForIntegrationTests();
        userRepository.save(user);

        Category category = createCategoryForIntegrationTests();
        categoryRepository.save(category);

        Book book = createBookForIntegrationTests();
        book.setCategory(category);
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loanRepository.save(loan);

        mockMvc.perform(delete("/loan/{id}", loan.getId()))
                .andExpect(status().isOk());

        assertEquals(0, loanRepository.count());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void delete_ShouldReturnNotFound_WhenLoanNotFound() throws Exception {
        User admin = new User();
        admin.setName("admin");
        admin.setEmail("admin");
        admin.setPassword("admin");
        admin.setRole(Role.ROLE_ADMIN);
        userRepository.save(admin);

        mockMvc.perform(delete("/loan/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Loan not found"));
    }
}
