package com.example.SistemaBlblioteca.service;

import com.example.SistemaBlblioteca.entity.Category;
import com.example.SistemaBlblioteca.exceptions.category.CategoryAlreadyExistException;
import com.example.SistemaBlblioteca.exceptions.category.CategoryNotFoundException;
import com.example.SistemaBlblioteca.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.example.SistemaBlblioteca.util.CategoryCreator.createCategory;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void save_SaveCategory_WhenSuccessfully() {
        Category category = createCategory();

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category savedCategory = categoryService.save(category);

        assertNotNull(savedCategory);

        assertEquals(category.getId(), savedCategory.getId());
        assertEquals(category.getName(), savedCategory.getName());

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void save_ThrowsException_WhenCategoryAlreadyExists() {
        Category category = createCategory();
        when(categoryRepository.existsByName(category.getName().toUpperCase())).thenReturn(true);

        assertThrows(CategoryAlreadyExistException.class, () -> categoryService.save(category));

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void findById_ReturnCategory_WhenSuccessfully() {
        Category category = createCategory();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category foundedCategory = categoryService.findById(1L);

        assertNotNull(foundedCategory);
        assertEquals(category.getId(), foundedCategory.getId());

        verify(categoryRepository).findById(1L);
    }

    @Test
    void findById_ThrowsException_WhenCategoryNotFound() {
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(2L));
    }

    @Test
    void findAll_ListOfCategories_WhenSuccessfully() {
        Category category = createCategory();
        List<Category> categories = List.of(category);

        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> categoryList = categoryService.findAll();

        assertFalse(categoryList.isEmpty());
        assertEquals(categoryList.size(), categories.size());
        assertEquals(category.getId(), categoryList.get(0).getId());

        verify(categoryRepository).findAll();
    }

    @Test
    void findAll_ReturnsEmptyList_WhenThereAreNoSavedCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of());

        assertTrue(categoryService.findAll().isEmpty());

        verify(categoryRepository).findAll();
    }

    @Test
    void update_UpdateCategory_WhenSuccessfully() {
        Category category = createCategory();
        Category categoryToUpdate = new Category(1L, "UPDATED");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(categoryToUpdate);

        Category updatedCategory = categoryService.update(1L, categoryToUpdate);

        assertNotNull(updatedCategory);
        assertEquals(category.getId(), updatedCategory.getId());
        assertEquals("UPDATED", updatedCategory.getName());

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void delete_DeleteCategory_WhenSuccessfully() {
        Category category = createCategory();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(any(Category.class));

        categoryService.delete(category.getId());

        assertFalse(categoryRepository.existsById(1L));
        verify(categoryRepository).delete(any(Category.class));
    }
}
