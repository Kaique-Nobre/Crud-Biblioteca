package com.example.SistemaBlblioteca.util;

import com.example.SistemaBlblioteca.entity.Category;

public class CategoryCreator {
    public static Category createCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("ROMANCE");

        return category;
    }

    public static Category createCategoryForIntegrationTests() {
        Category category = new Category();
        category.setName("ROMANCE");

        return category;
    }

    public static Category createInvalidCategory() {
        Category category = new Category();
        category.setName("");
        return category;
    }
}
