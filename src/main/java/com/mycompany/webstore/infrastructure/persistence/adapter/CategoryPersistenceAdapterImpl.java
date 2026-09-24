package com.mycompany.webstore.infrastructure.persistence.adapter;

import com.mycompany.webstore.application.port.out.CategoryRepository;
import com.mycompany.webstore.domain.model.Category;
import com.mycompany.webstore.infrastructure.persistence.repository.CategoryJpaRepository;
import com.mycompany.webstore.infrastructure.persistence.repository.ProductJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CategoryPersistenceAdapterImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final CategoryPersistenceMapper mapper;

    public CategoryPersistenceAdapterImpl(CategoryJpaRepository categoryJpaRepository,
                                          ProductJpaRepository productJpaRepository,
                                          CategoryPersistenceMapper mapper) {
        this.categoryJpaRepository = categoryJpaRepository;
        this.productJpaRepository = productJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Category save(Category category) {
        return mapper.toDomain(categoryJpaRepository.save(mapper.toJpaEntity(category)));
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return categoryJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Category> findBySlug(String slug) {
        return categoryJpaRepository.findBySlug(slug).map(mapper::toDomain);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return categoryJpaRepository.existsBySlug(slug);
    }

    @Override
    public boolean existsByParentId(UUID parentId) {
        return categoryJpaRepository.existsByParentId(parentId);
    }

    @Override
    public List<Category> findAll() {
        return categoryJpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Category> findByCategoryId(UUID categoryId) {
        return categoryJpaRepository.findByParentId(categoryId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean hasProducts(UUID categoryId) {
        return productJpaRepository.existsByCategoryId(categoryId);
    }

    @Override
    public void deleteById(UUID id) {
        categoryJpaRepository.deleteById(id);
    }
}
