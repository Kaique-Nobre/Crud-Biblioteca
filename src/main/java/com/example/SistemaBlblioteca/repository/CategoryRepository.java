package com.example.SistemaBlblioteca.repository;

import com.example.SistemaBlblioteca.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
}
