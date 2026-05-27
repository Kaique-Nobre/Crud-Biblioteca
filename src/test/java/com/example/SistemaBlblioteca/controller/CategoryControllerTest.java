package com.example.SistemaBlblioteca.controller;

import com.example.SistemaBlblioteca.entity.Category;
import com.example.SistemaBlblioteca.exceptions.category.CategoryAlreadyExistException;
import com.example.SistemaBlblioteca.exceptions.category.CategoryNotFoundException;
import com.example.SistemaBlblioteca.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static com.example.SistemaBlblioteca.util.CategoryCreator.createCategory;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void save_CreateCategory_WhenSuccessfully() throws Exception {
        Category category = createCategory();

        when(categoryService.save(any(Category.class)))
                .thenReturn(category);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("ROMANCE"));
    }

    @Test
    void save_ReturnsException_WhenCategoryIsBlank() throws Exception {
        Category category = new Category();
        category.setName("");

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void save_ReturnsException_WhenCategoryAlreadyExist() throws Exception {
        Category category = createCategory();

        when(categoryService.save(any(Category.class))).thenThrow(new CategoryAlreadyExistException("Category already exist"));

        mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isConflict());
    }

    @Test
    void findAll_ReturnListOfCategories_WhenSuccessfully() throws Exception {
        Category category = createCategory();

        List<Category> categoryList = List.of(category);

        when(categoryService.findAll()).thenReturn(categoryList);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("ROMANCE"));
    }

    @Test
    void findAll_ReturnEmptyList_WhenThereAreNoSavedCategories() throws Exception {
        List<Category> categoryList = new ArrayList<>();

        when(categoryService.findAll()).thenReturn(categoryList);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void findById_ReturnCategory_WhenSuccessfully() throws Exception {
        Category category = createCategory();

        when(categoryService.findById(1L)).thenReturn(category);

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("ROMANCE"));
    }

    @Test
    void findById_ReturnsException_WhenCategoryNotFound() throws Exception {
        when(categoryService.findById(1L)).thenThrow(new CategoryNotFoundException("Category not found"));

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_UpdateCategory_WhenSuccessfully() throws Exception {
        Category request = new Category();
        request.setName("romance");

        Category response = new Category(1L, "FANTASY");

        when(categoryService.update(eq(1L), any(Category.class))).thenReturn(response);

        mockMvc.perform(put("/categories/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("FANTASY"));
    }

    @Test
    void delete_DeleteCategory_WhenSuccessfully() throws Exception {
        doNothing().when(categoryService).delete(1L);

        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());
        verify(categoryService).delete(1L);
    }
}
