package com.mycompany.webstore.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Cart {

    private UUID id;
    private UUID customerId;
    private String sessionId;
    private CartStatus status;
    private List<CartItem> items;
    private String couponCode;
    private BigDecimal discountAmount;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant expiresAt;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public Cart(UUID id, UUID customerId, String sessionId, CartStatus status,
                List<CartItem> items, String couponCode, BigDecimal discountAmount,
                Instant createdAt, Instant updatedAt, Instant expiresAt) {
        this.id = id;
        this.customerId = customerId;
        this.sessionId = sessionId;
        this.status = status;
        this.items = items != null ? items : new ArrayList<>();
        this.couponCode = couponCode;
        this.discountAmount = discountAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.expiresAt = expiresAt;
    }

    public BigDecimal getSubtotal() {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotal() {
        BigDecimal subtotal = getSubtotal();
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            return subtotal.subtract(discountAmount).max(BigDecimal.ZERO);
        }
        return subtotal;
    }

    public Optional<CartItem> findItemByProductId(UUID productId) {
        return items.stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst();
    }

    public Optional<CartItem> findItemById(UUID itemId) {
        return items.stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst();
    }

    public boolean hasUnavailableItems() {
        return items.stream().anyMatch(i -> !i.isAvailable());
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public CartStatus getStatus() { return status; }
    public void setStatus(CartStatus status) { this.status = status; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items != null ? items : new ArrayList<>(); }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
