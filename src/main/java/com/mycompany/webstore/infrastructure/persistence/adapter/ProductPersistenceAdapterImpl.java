package com.mycompany.webstore.infrastructure.persistence.adapter;

import com.mycompany.webstore.application.port.out.ProductRepository;
import com.mycompany.webstore.domain.model.Product;
import com.mycompany.webstore.domain.model.ProductStatus;
import com.mycompany.webstore.infrastructure.persistence.repository.ProductJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductPersistenceAdapterImpl implements ProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final ProductPersistenceMapper mapper;

    public ProductPersistenceAdapterImpl(ProductJpaRepository jpaRepository,
                                         ProductPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Product save(Product product) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(product)));
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return jpaRepository.findBySku(sku).map(mapper::toDomain);
    }

    @Override
    public boolean existsBySku(String sku) {
        return jpaRepository.existsBySku(sku);
    }

    @Override
    public Page<Product> findAll(String query, UUID categoryId, BigDecimal minPrice,
                                 BigDecimal maxPrice, ProductStatus status, Pageable pageable) {
        return jpaRepository.findAllFiltered(query, categoryId, minPrice, maxPrice, status, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
