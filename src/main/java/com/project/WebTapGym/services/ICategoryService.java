package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.CategoryDTO;
import com.project.WebTapGym.models.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryDTO categoryDTO);

    Category getCategoryById(Long id);

    List<Category> getAllCategories();

    Category updateCategory(Long categoryId, CategoryDTO categoryDTO);

    void deleteCategory(Long id);
}
