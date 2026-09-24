package com.mycompany.webstore.application.port.out;

import com.mycompany.webstore.domain.model.Product;
import com.mycompany.webstore.domain.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(UUID id);

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    Page<Product> findAll(String query, UUID categoryId, BigDecimal minPrice,
                          BigDecimal maxPrice, ProductStatus status, Pageable pageable);

    void deleteById(UUID id);
}
