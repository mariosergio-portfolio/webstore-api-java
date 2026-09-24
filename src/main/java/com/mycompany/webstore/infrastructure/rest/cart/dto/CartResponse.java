package com.mycompany.webstore.infrastructure.rest.cart.dto;

import com.mycompany.webstore.domain.model.CartStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CartResponse(
        UUID id,
        UUID customerId,
        String sessionId,
        CartStatus status,
        List<CartItemResponse> items,
        int itemCount,
        String couponCode,
        BigDecimal discountAmount,
        BigDecimal subtotal,
        BigDecimal total,
        boolean hasUnavailableItems,
        Instant createdAt,
        Instant updatedAt,
        Instant expiresAt
) {}
