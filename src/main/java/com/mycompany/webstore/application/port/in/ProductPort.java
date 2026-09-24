package com.mycompany.webstore.application.port.in;

import com.mycompany.webstore.domain.model.Product;
import com.mycompany.webstore.domain.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductPort {

    Page<Product> listProducts(String query, UUID categoryId, BigDecimal minPrice,
                               BigDecimal maxPrice, ProductStatus status, Pageable pageable);

    Product getProductById(UUID id);

    Product getProductBySku(String sku);

    Product createProduct(Product product);

    Product updateProduct(UUID id, Product product);

    Product patchProduct(UUID id, Product patch);

    void archiveProduct(UUID id);
}
