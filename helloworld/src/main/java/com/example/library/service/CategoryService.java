package com.example.library.service;

import org.springframework.transaction.annotation.Transactional;
import com.example.library.model.Category;
import com.example.library.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    
    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }
    
    @Transactional
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    @Transactional
    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Category> searchCategoryByName(String name) {
        return categoryRepository.findByNameContaining(name);
    }
}