package com.mycompany.webstore.application.service;

import com.mycompany.webstore.application.port.in.CategoryPort;
import com.mycompany.webstore.application.port.out.CategoryRepository;
import com.mycompany.webstore.domain.model.Category;
import com.mycompany.webstore.shared.exception.BusinessRuleException;
import com.mycompany.webstore.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryPortImpl implements CategoryPort {

    private final CategoryRepository categoryRepository;

    public CategoryPortImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> listCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {
        if (categoryRepository.existsBySlug(category.getSlug())) {
            throw new BusinessRuleException("Category slug already exists: " + category.getSlug());
        }
        if (category.getParentId() != null) {
            categoryRepository.findById(category.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent category not found with id: " + category.getParentId()));
        }
        category.setId(UUID.randomUUID());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(UUID id, Category category) {
        Category existing = getCategoryById(id);

        if (!existing.getSlug().equals(category.getSlug())
                && categoryRepository.existsBySlug(category.getSlug())) {
            throw new BusinessRuleException("Category slug already exists: " + category.getSlug());
        }
        if (category.getParentId() != null) {
            if (category.getParentId().equals(id)) {
                throw new BusinessRuleException("A category cannot be its own parent");
            }
            categoryRepository.findById(category.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent category not found with id: " + category.getParentId()));
        }

        category.setId(existing.getId());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        getCategoryById(id);
        if (categoryRepository.hasProducts(id)) {
            throw new BusinessRuleException(
                    "Cannot delete category with assigned products. Reassign products first.");
        }
        categoryRepository.deleteById(id);
    }
}
