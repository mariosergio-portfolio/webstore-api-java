package com.mycompany.webstore.application.port.in;

import com.mycompany.webstore.domain.model.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryPort {

    List<Category> listCategories();

    Category getCategoryById(UUID id);

    Category createCategory(Category category);

    Category updateCategory(UUID id, Category category);

    void deleteCategory(UUID id);
}
