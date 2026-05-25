package com.example.SistemaBlblioteca.service;

import com.example.SistemaBlblioteca.entity.Category;
import com.example.SistemaBlblioteca.exceptions.category.CategoryAlreadyExistException;
import com.example.SistemaBlblioteca.exceptions.category.CategoryNotFoundException;
import com.example.SistemaBlblioteca.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return getCategory(id);
    }

    public Category save(Category category) {
        if(categoryRepository.existsByName(category.getName().toUpperCase())) {
            throw new CategoryAlreadyExistException("Category already exists with name " + category.getName());
        }
        String upperCase = category.getName().toUpperCase();
        category.setName(upperCase);
        return categoryRepository.save(category);
    }

    public Category update(Long id, Category category) {
        Category categoryToUpdate = getCategory(id);

        Category categoryUpdated = new Category();
        categoryUpdated.setName(categoryToUpdate.getName().toUpperCase());

        return categoryRepository.save(categoryUpdated);
    }

    public void delete(Long id) {
        Category category = getCategory(id);
        categoryRepository.delete(category);
    }

    private Category getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id " + id));
        return category;
    }
}
