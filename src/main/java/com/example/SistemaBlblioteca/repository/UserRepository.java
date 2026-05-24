package com.example.SistemaBlblioteca.repository;

import com.example.SistemaBlblioteca.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
