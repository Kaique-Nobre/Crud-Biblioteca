package com.example.SistemaBlblioteca.util;

import com.example.SistemaBlblioteca.dto.LoanDTO.LoanRequestDTO;
import com.example.SistemaBlblioteca.dto.LoanDTO.LoanResponseDTO;
import com.example.SistemaBlblioteca.entity.Book;
import com.example.SistemaBlblioteca.entity.Loan;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.SistemaBlblioteca.util.BookCreator.createBook;
import static com.example.SistemaBlblioteca.util.UserCreator.createUser;

public class LoanCreator {
    public static Loan createLoan() {
        Book book = createBook();
        book.setAvailable(false);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setUser(createUser());
        loan.setBook(book);
        loan.setLoanDate(LocalDateTime.now());
        return loan;
    }

    public static LoanResponseDTO createLoanResponse() {
        return new LoanResponseDTO(1L, "user", "book", LocalDate.now());
    }
}
