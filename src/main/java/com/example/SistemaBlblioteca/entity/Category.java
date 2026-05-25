package com.example.SistemaBlblioteca.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "Categories")
@Getter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
}
