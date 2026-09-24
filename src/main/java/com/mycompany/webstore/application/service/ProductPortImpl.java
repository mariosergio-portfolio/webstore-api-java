package com.mycompany.webstore.application.service;

import com.mycompany.webstore.application.port.in.ProductPort;
import com.mycompany.webstore.application.port.out.ProductRepository;
import com.mycompany.webstore.domain.model.Product;
import com.mycompany.webstore.domain.model.ProductStatus;
import com.mycompany.webstore.shared.exception.BusinessRuleException;
import com.mycompany.webstore.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class ProductPortImpl implements ProductPort {

    private final ProductRepository productRepository;

    public ProductPortImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Page<Product> listProducts(String query, UUID categoryId, BigDecimal minPrice,
                                      BigDecimal maxPrice, ProductStatus status, Pageable pageable) {
        return productRepository.findAll(query, categoryId, minPrice, maxPrice, status, pageable);
    }

    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    public Product getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        if (productRepository.existsBySku(product.getSku())) {
            throw new BusinessRuleException("SKU already exists: " + product.getSku());
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Product price must be greater than 0");
        }
        product.setId(UUID.randomUUID());
        product.setStatus(ProductStatus.DRAFT);
        product.setCreatedAt(Instant.now());
        product.setUpdatedAt(Instant.now());
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(UUID id, Product product) {
        Product existing = getProductById(id);
        product.setId(existing.getId());
        product.setSku(existing.getSku()); // SKU is immutable after creation
        product.setCreatedAt(existing.getCreatedAt());
        product.setUpdatedAt(Instant.now());
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Product price must be greater than 0");
        }
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product patchProduct(UUID id, Product patch) {
        Product existing = getProductById(id);

        if (patch.getName() != null) existing.setName(patch.getName());
        if (patch.getDescription() != null) existing.setDescription(patch.getDescription());
        if (patch.getPrice() != null) {
            if (patch.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessRuleException("Product price must be greater than 0");
            }
            existing.setPrice(patch.getPrice());
        }
        if (patch.getCurrency() != null) existing.setCurrency(patch.getCurrency());
        if (patch.getCategoryId() != null) existing.setCategoryId(patch.getCategoryId());
        if (patch.getImageUrls() != null) existing.setImageUrls(patch.getImageUrls());
        if (patch.getStatus() != null) existing.setStatus(patch.getStatus());
        existing.setUpdatedAt(Instant.now());

        return productRepository.save(existing);
    }

    @Override
    @Transactional
    public void archiveProduct(UUID id) {
        Product product = getProductById(id);
        product.archive();
        productRepository.save(product);
    }
}
