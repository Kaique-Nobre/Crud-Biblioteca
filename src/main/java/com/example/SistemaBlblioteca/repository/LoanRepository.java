package com.example.SistemaBlblioteca.repository;

import com.example.SistemaBlblioteca.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
