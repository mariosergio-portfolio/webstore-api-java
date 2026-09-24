package com.mycompany.webstore.infrastructure.rest.cart.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CartItemResponse(
        UUID id,
        UUID productId,
        String productName,
        String productSku,
        BigDecimal unitPrice,
        String currency,
        int quantity,
        BigDecimal subtotal,
        boolean available,
        Instant addedAt,
        Instant updatedAt
) {}
