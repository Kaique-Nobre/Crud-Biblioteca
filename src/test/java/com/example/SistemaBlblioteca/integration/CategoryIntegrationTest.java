package com.example.SistemaBlblioteca.integration;

import com.example.SistemaBlblioteca.dto.categoryDTO.CategoryRequestDTO;
import com.example.SistemaBlblioteca.entity.Category;
import com.example.SistemaBlblioteca.repository.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoryIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        categoryRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void save_ShouldCreateCategory_WhenSuccessfully() throws Exception{
        Category request = new Category();
        request.setName("romance");

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        List<Category> categoryList = categoryRepository.findAll();

        assertEquals(1, categoryList.size());
        assertEquals("ROMANCE", categoryList.get(0).getName());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void save_ShoudReturnConflict_WhenCategoryAlreadyExists() throws Exception{
        Category request = new Category();
        request.setName("romance");

        Category category = new Category();
        category.setName("ROMANCE");

        categoryRepository.save(category);

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldUpdateCategory_WhenSuccessfully() throws Exception{
        Category categoryToUpdate = new Category("romance");

        CategoryRequestDTO request = new CategoryRequestDTO("fantasy");

        categoryRepository.save(categoryToUpdate);

        Optional<Category> category = categoryRepository.findById(categoryToUpdate.getId());

        category.ifPresent(category1 -> category1.setName(request.name().toUpperCase()));

        categoryRepository.save(category.get());

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertEquals(request.name().toUpperCase(), category.get().getName());
        assertEquals("FANTASY", category.get().getName());
        assertEquals(1L, category.get().getId());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldDeleteCategory_WhenSuccessfully() throws Exception{
        Category categoryToDelete = new Category("romance");
        Category category = categoryRepository.save(categoryToDelete);

        mockMvc.perform(delete("/categories/{id}", category.getId()))
                .andExpect(status().isNoContent());

        assertFalse(categoryRepository.findById(category.getId()).isPresent());
    }
}
