package com.example.SistemaBlblioteca.repository;

import com.example.SistemaBlblioteca.entity.Loan;
import com.example.SistemaBlblioteca.security.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findAllByUser(User user);
}
