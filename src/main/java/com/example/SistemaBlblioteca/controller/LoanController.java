package com.example.SistemaBlblioteca.controller;

import com.example.SistemaBlblioteca.dto.LoanDTO.LoanRequestDTO;
import com.example.SistemaBlblioteca.dto.LoanDTO.LoanResponseDTO;
import com.example.SistemaBlblioteca.dto.messageDTO.MessageResponseDTO;
import com.example.SistemaBlblioteca.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "Loans")
@RestController
@RequestMapping("/loan")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class LoanController {
    private final LoanService loanService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a loan")
    public ResponseEntity<MessageResponseDTO> save(@Valid @RequestBody LoanRequestDTO request) {
        loanService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponseDTO("Loan registered successfully"));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Return all loans", description = "A user can only see their own loans, an admin can see all loans")
    public List<LoanResponseDTO> findAll() {
        return loanService.findAll();
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Return a loan by ID", description = "Only admin can search loans by ID")
    public LoanResponseDTO findById(@PathVariable Long id){
        return loanService.findById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a loan", description = "Only admin can delete loans")
    public ResponseEntity<MessageResponseDTO> delete(@PathVariable Long id){
        loanService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponseDTO("Loan has been deleted successfully"));
    }
}
