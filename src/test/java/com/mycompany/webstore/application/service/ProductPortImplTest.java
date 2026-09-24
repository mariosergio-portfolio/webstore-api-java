package com.mycompany.webstore.application.service;

import com.mycompany.webstore.application.port.out.ProductRepository;
import com.mycompany.webstore.domain.model.Product;
import com.mycompany.webstore.domain.model.ProductStatus;
import com.mycompany.webstore.shared.exception.BusinessRuleException;
import com.mycompany.webstore.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductPortImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductPortImpl productPort;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product(
                UUID.randomUUID(), "SKU-001", "Test Product", "A description",
                new BigDecimal("29.99"), "USD", 10,
                null, List.of(), ProductStatus.ACTIVE, Instant.now(), Instant.now());
    }

    // ── getProductById ────────────────────────────────────────────────────────

    @Test
    void should_return_product_when_id_exists() {
        // Arrange
        when(productRepository.findById(sampleProduct.getId())).thenReturn(Optional.of(sampleProduct));

        // Act
        Product result = productPort.getProductById(sampleProduct.getId());

        // Assert
        assertThat(result).isEqualTo(sampleProduct);
    }

    @Test
    void should_throw_not_found_when_product_id_does_not_exist() {
        // Arrange
        UUID unknownId = UUID.randomUUID();
        when(productRepository.findById(unknownId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productPort.getProductById(unknownId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(unknownId.toString());
    }

    // ── createProduct ─────────────────────────────────────────────────────────

    @Test
    void should_create_product_when_sku_is_unique_and_price_is_valid() {
        // Arrange
        Product input = new Product();
        input.setSku("SKU-NEW");
        input.setName("New Product");
        input.setPrice(new BigDecimal("9.99"));
        input.setCurrency("USD");

        when(productRepository.existsBySku("SKU-NEW")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Product result = productPort.createProduct(input);

        // Assert
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ProductStatus.DRAFT);
        assertThat(result.getCreatedAt()).isNotNull();
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void should_throw_business_rule_when_sku_already_exists() {
        // Arrange
        Product input = new Product();
        input.setSku("DUPLICATE");
        input.setPrice(new BigDecimal("9.99"));
        when(productRepository.existsBySku("DUPLICATE")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> productPort.createProduct(input))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("SKU already exists");
    }

    @Test
    void should_throw_business_rule_when_price_is_zero_on_create() {
        // Arrange
        Product input = new Product();
        input.setSku("SKU-ZERO");
        input.setPrice(BigDecimal.ZERO);
        when(productRepository.existsBySku("SKU-ZERO")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> productPort.createProduct(input))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("price must be greater than 0");
    }

    // ── archiveProduct ────────────────────────────────────────────────────────

    @Test
    void should_archive_product_when_product_exists() {
        // Arrange
        when(productRepository.findById(sampleProduct.getId())).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        productPort.archiveProduct(sampleProduct.getId());

        // Assert
        assertThat(sampleProduct.getStatus()).isEqualTo(ProductStatus.ARCHIVED);
        verify(productRepository).save(sampleProduct);
    }

    @Test
    void should_throw_not_found_when_archiving_non_existing_product() {
        // Arrange
        UUID unknownId = UUID.randomUUID();
        when(productRepository.findById(unknownId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productPort.archiveProduct(unknownId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
