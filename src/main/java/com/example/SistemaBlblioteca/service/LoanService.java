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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanMapper loanMapper;

    public void save(LoanRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        Book book = bookRepository.findById(request.book())
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID:" + request.book()));

        if(!book.isAvailable()) {
            throw new BookUnavailableException("Book is not available");
        }

        User user  = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email:" + email));

        book.setAvailable(false);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(LocalDateTime.now());
        loanRepository.save(loan);
    }

    public List<LoanResponseDTO> findAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email:" + email));

        List<Loan> loans;

        if(user.getRole() == Role.ROLE_ADMIN) {
            loans = loanRepository.findAll();
        }else {
            loans = loanRepository.findAllByUser(user);
        }

        return loanMapper.toLoanResponseList(loans);
    }

    public LoanResponseDTO findById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID:" + id));

        return loanMapper.toDTO(loan);
    }

    public ResponseEntity<MessageResponseDTO> delete(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID:" + id));
        loan.getBook().setAvailable(true);

        loanRepository.delete(loan);

        return new ResponseEntity<>(new MessageResponseDTO("Loan deleted successfully"), HttpStatus.NO_CONTENT);
    }
}
