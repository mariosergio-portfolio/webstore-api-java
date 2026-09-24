package com.mycompany.webstore.infrastructure.rest.catalog.dto;

import com.mycompany.webstore.domain.model.ImageUrl;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductRequest(

        @NotBlank(message = "SKU is required")
        String sku,

        @NotBlank(message = "Name is required")
        String name,

        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price,

        @NotBlank(message = "Currency is required")
        String currency,

        int stockQuantity,

        UUID categoryId,

        List<ImageUrl> imageUrls
) {}
