package com.mycompany.webstore.application.port.out;

import com.mycompany.webstore.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(UUID id);

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByParentId(UUID parentId);

    List<Category> findAll();

    List<Category> findByCategoryId(UUID categoryId);

    boolean hasProducts(UUID categoryId);

    void deleteById(UUID id);
}
