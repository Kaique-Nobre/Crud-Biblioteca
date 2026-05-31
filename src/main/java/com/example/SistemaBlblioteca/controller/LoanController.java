package com.example.SistemaBlblioteca.controller;

import com.example.SistemaBlblioteca.dto.LoanDTO.LoanRequestDTO;
import com.example.SistemaBlblioteca.dto.LoanDTO.LoanResponseDTO;
import com.example.SistemaBlblioteca.dto.messageDTO.MessageResponseDTO;
import com.example.SistemaBlblioteca.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loan")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponseDTO> save(@Valid @RequestBody LoanRequestDTO request) {
        loanService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponseDTO("Loan registered successfully"));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<LoanResponseDTO> findAll() {
        return loanService.findAll();
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public LoanResponseDTO findById(@PathVariable Long id){
        return loanService.findById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponseDTO> delete(@PathVariable Long id){
        loanService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponseDTO("Loan has been deleted successfully"));
    }
}
