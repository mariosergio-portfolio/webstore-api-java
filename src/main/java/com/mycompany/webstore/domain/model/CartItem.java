package com.mycompany.webstore.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class CartItem {

    private UUID id;
    private UUID cartId;
    private UUID productId;
    private String productName;
    private String productSku;
    private BigDecimal unitPrice;
    private String currency;
    private int quantity;
    private boolean available;
    private Instant addedAt;
    private Instant updatedAt;

    public CartItem() {}

    public CartItem(UUID id, UUID cartId, UUID productId, String productName, String productSku,
                    BigDecimal unitPrice, String currency, int quantity, boolean available,
                    Instant addedAt, Instant updatedAt) {
        this.id = id;
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.productSku = productSku;
        this.unitPrice = unitPrice;
        this.currency = currency;
        this.quantity = quantity;
        this.available = available;
        this.addedAt = addedAt;
        this.updatedAt = updatedAt;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCartId() { return cartId; }
    public void setCartId(UUID cartId) { this.cartId = cartId; }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public Instant getAddedAt() { return addedAt; }
    public void setAddedAt(Instant addedAt) { this.addedAt = addedAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
