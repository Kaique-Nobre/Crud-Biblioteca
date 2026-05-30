package com.example.SistemaBlblioteca.service;

import com.example.SistemaBlblioteca.dto.LoanDTO.LoanRequestDTO;
import com.example.SistemaBlblioteca.dto.LoanDTO.LoanResponseDTO;
import com.example.SistemaBlblioteca.dto.messageDTO.MessageResponseDTO;
import com.example.SistemaBlblioteca.entity.Book;
import com.example.SistemaBlblioteca.entity.Loan;
import com.example.SistemaBlblioteca.exceptions.book.BookNotFoundException;
import com.example.SistemaBlblioteca.exceptions.book.BookUnavailableException;
import com.example.SistemaBlblioteca.exceptions.loan.LoanNotFoundException;
import com.example.SistemaBlblioteca.mapper.LoanMapper;
import com.example.SistemaBlblioteca.repository.BookRepository;
import com.example.SistemaBlblioteca.repository.LoanRepository;
import com.example.SistemaBlblioteca.security.user.Role;
import com.example.SistemaBlblioteca.security.user.User;
import com.example.SistemaBlblioteca.security.user.UserRepository;
import org.apache.catalina.security.SecurityUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.SistemaBlblioteca.util.LoanCreator.createLoan;
import static com.example.SistemaBlblioteca.util.LoanCreator.createLoanResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {
    @Mock
    LoanRepository loanRepository;

    @Mock
    BookRepository bookRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    LoanMapper loanMapper;

    @Mock
    SecurityContextHolder securityContextHolder;

    @InjectMocks
    LoanService loanService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void save_ShouldSaveLoan_WhenSuccessfully() throws Exception {
        LoanRequestDTO request = new LoanRequestDTO(1L);

        Book book = new Book();
        book.setId(1L);
        book.setAvailable(true);

        User user = new User();
        user.setEmail("user@email.com");

        mockAuthenticatedUser("user@email.com");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));

        loanService.save(request);

        ArgumentCaptor<Loan> captor = ArgumentCaptor.forClass(Loan.class);

        verify(loanRepository).save(captor.capture());

        Loan savedLoan = captor.getValue();

        assertEquals(book, savedLoan.getBook());
        assertEquals(user, savedLoan.getUser());
        assertFalse(book.isAvailable());

        verify(bookRepository).findById(1L);
        verify(userRepository).findByEmail("user@email.com");
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void save_ShouldThrowException_WhenBoookNotFound() throws Exception {
        LoanRequestDTO request = new LoanRequestDTO(1L);

        User user = new User();
        user.setEmail("user@email.com");

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@email.com");

        securityContextHolder.setContext(securityContext);

        when(bookRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                BookNotFoundException.class,
                () -> loanService.save(request)
        );

        verify(loanRepository, never())
                .save(any());
    }

    @Test
    void save_ShouldThrowException_WhenBookIsNotAvailable() throws Exception {
        LoanRequestDTO request = new LoanRequestDTO(1L);

        User user = new User();
        user.setEmail("user@email.com");

        mockAuthenticatedUser("user@email.com");

        Book book = new Book();
        book.setAvailable(false);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(BookUnavailableException.class, () -> loanService.save(request));

        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void findAll_ShouldReturnAllUserLoans_WhenHasRoleUser() throws Exception {
        User user = new User();
        user.setEmail("user@email.com");
        user.setRole(Role.ROLE_USER);


        Loan loan = createLoan();
        List<Loan> loans = List.of(loan);
        List<LoanResponseDTO> response = List.of(createLoanResponse());

        mockAuthenticatedUser("user@email.com");
        when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));
        when(loanRepository.findAllByUser(user)).thenReturn(loans);
        when(loanMapper.toLoanResponseList(loans)).thenReturn(response);


        List<LoanResponseDTO> result = loanService.findAll();

        assertNotNull(result);

        assertEquals(1, result.size());

        verify(loanRepository).findAllByUser(user);

        verify(loanRepository, never()).findAll();
    }

    @Test
    void findAll_ShouldReturnAllLoans_WhenHasRoleAdmin() throws Exception {
        User admin = new User();
        admin.setEmail("admin@email.com");
        admin.setRole(Role.ROLE_ADMIN);

        Loan loan = createLoan();
        List<Loan> loans = List.of(loan);
        List<LoanResponseDTO> response = List.of(createLoanResponse());

        mockAuthenticatedUser(admin.getEmail());

        when(userRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(admin));
        when(loanRepository.findAll()).thenReturn(loans);
        when(loanMapper.toLoanResponseList(loans)).thenReturn(response);

        List<LoanResponseDTO> result = loanService.findAll();

        assertNotNull(result);

        assertEquals(1, result.size());

        verify(loanRepository).findAll();

        verify(loanRepository, never()).findAllByUser(any());
    }

    @Test
    void findById_ReturnsLoan_WhenSuccessfully() throws Exception {
        LoanResponseDTO response = createLoanResponse();
        Loan loan = createLoan();

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanMapper.toDTO(loan)).thenReturn(response);

        LoanResponseDTO foundedLoan = loanService.findById(1L);

        assertNotNull(foundedLoan);
        assertEquals(1, foundedLoan.id());
        assertEquals(response.user(), foundedLoan.user());
        assertEquals(response.book(), foundedLoan.book());

        verify(loanRepository).findById(1L);
    }

    @Test
    void findById_ShouldThrowException_WhenLoanNotFound() throws Exception {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundException.class, () -> loanService.findById(1L));
    }

    @Test
    void delete_ShouldDeleteLoan_WhenSuccessfully() throws Exception {
        Loan loan = createLoan();

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        ResponseEntity<MessageResponseDTO> response = loanService.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertTrue(loan.getBook().isAvailable());

        verify(loanRepository).findById(1L);

        verify(loanRepository).delete(loan);
    }

    @Test
    void delete_ShouldThrowException_WhenLoanNotFound() throws Exception {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundException.class, () -> loanService.delete(1L));

        verify(loanRepository, never()).delete(any());
    }

    private void mockAuthenticatedUser(String email) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);

        SecurityContextHolder.setContext(securityContext);
    }
}
