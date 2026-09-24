package com.mycompany.webstore.infrastructure.rest.catalog.dto;

import com.mycompany.webstore.domain.model.ImageUrl;
import com.mycompany.webstore.domain.model.ProductStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        String currency,
        int stockQuantity,
        UUID categoryId,
        List<ImageUrl> imageUrls,
        ProductStatus status,
        Instant createdAt,
        Instant updatedAt
) {}
