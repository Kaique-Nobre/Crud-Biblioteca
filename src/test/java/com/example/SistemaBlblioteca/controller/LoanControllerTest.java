package com.example.SistemaBlblioteca.controller;

import com.example.SistemaBlblioteca.dto.LoanDTO.LoanRequestDTO;
import com.example.SistemaBlblioteca.dto.LoanDTO.LoanResponseDTO;
import com.example.SistemaBlblioteca.dto.messageDTO.MessageResponseDTO;
import com.example.SistemaBlblioteca.entity.Loan;
import com.example.SistemaBlblioteca.exceptions.book.BookNotFoundException;
import com.example.SistemaBlblioteca.exceptions.book.BookUnavailableException;
import com.example.SistemaBlblioteca.exceptions.loan.LoanNotFoundException;
import com.example.SistemaBlblioteca.security.jwt.JwtAuthenticationFilter;
import com.example.SistemaBlblioteca.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.ArrayList;
import java.util.List;

import static com.example.SistemaBlblioteca.util.BookCreator.createBook;
import static com.example.SistemaBlblioteca.util.LoanCreator.createLoan;
import static com.example.SistemaBlblioteca.util.LoanCreator.createLoanResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LoanControllerTest {
    @MockitoBean
    private LoanService loanService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void save_ShouldSaveLoan_WhenSuccessfully() throws Exception {
        LoanRequestDTO loanRequestDTO = new LoanRequestDTO(1L);

        doNothing().when(loanService).save(any(LoanRequestDTO.class));

        mockMvc.perform(post("/loan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loanRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Loan registered successfully"));
    }

    @Test
    void save_ShouldReturnConflict_WhenBookIsUnavailable() throws Exception {
        LoanRequestDTO loanRequestDTO = new LoanRequestDTO(1L);

        doThrow(new BookUnavailableException("Book is not available")).when(loanService).save(any(LoanRequestDTO.class));

        mockMvc.perform(post("/loan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loanRequestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("The book is currently unavailable"));
    }

    @Test
    void save_ShouldReturnNotFound_WhenBookIsNotFound() throws Exception {
        LoanRequestDTO loanRequestDTO = new LoanRequestDTO(1L);

        doThrow(new BookNotFoundException("Book is not found")).when(loanService).save(any(LoanRequestDTO.class));

        mockMvc.perform(post("/loan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loanRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Book not found"));
    }

    @Test
    void findAll_ShouldReturnListOfLoans_WhenSuccessfully() throws Exception {
        LoanRequestDTO loanRequestDTO = new LoanRequestDTO(1L);
        LoanResponseDTO loan = createLoanResponse();

        List<LoanResponseDTO> loans = List.of(loan);

        when(loanService.findAll()).thenReturn(loans);

        mockMvc.perform(get("/loan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loanRequestDTO)))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$[0].id").value(1))
                .andExpect((ResultMatcher) jsonPath("$[0].user").value("user"))
                .andExpect((ResultMatcher) jsonPath("$[0].book").value("book"));
    }

    @Test
    void findById_ShouldReturnLoan_WhenSuccessfully() throws Exception {
        LoanRequestDTO loanRequestDTO = new LoanRequestDTO(1L);
        LoanResponseDTO loan = createLoanResponse();

        when(loanService.findById(anyLong())).thenReturn(loan);

        mockMvc.perform(get("/loan/admin/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loanRequestDTO)))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.id").value(1))
                .andExpect((ResultMatcher) jsonPath("$.user").value("user"))
                .andExpect((ResultMatcher) jsonPath("$.book").value("book"));
    }

    @Test
    void findById_ShouldReturnNotFound_WhenLoanIsNotFound() throws Exception {

        when(loanService.findById(anyLong())).thenThrow(LoanNotFoundException.class);

        mockMvc.perform(get("/loan/admin/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Loan not found"));

    }

    @Test
    void delete_ShouldReturnNoContent_WhenSuccessfully() throws Exception {
        ResponseEntity<MessageResponseDTO> messageResponseDTO = ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(new MessageResponseDTO("Loan has been deleted successfully"));

        when(loanService.delete(anyLong())).thenReturn(messageResponseDTO);

        mockMvc.perform(delete("/loan/1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value("Loan has been deleted successfully"));
    }

    @Test
    void delete_ShouldReturnNotFound_WhenLoanIsNotFound() throws Exception {

        when(loanService.delete(anyLong())).thenThrow(LoanNotFoundException.class);

        mockMvc.perform(delete("/loan/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Loan not found"));
    }
}
