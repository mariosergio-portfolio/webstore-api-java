package com.mycompany.webstore.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Coupon {

    private UUID id;
    private String code;
    private CouponStatus status;
    private BigDecimal discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal minOrderAmount;
    private Integer maxUses;
    private int usedCount;
    private Instant expiresAt;
    private Instant createdAt;
    private Instant updatedAt;

    public Coupon() {}

    public Coupon(UUID id, String code, CouponStatus status, BigDecimal discountPercent,
                  BigDecimal discountAmount, BigDecimal minOrderAmount, Integer maxUses,
                  int usedCount, Instant expiresAt, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.code = code;
        this.status = status;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.minOrderAmount = minOrderAmount;
        this.maxUses = maxUses;
        this.usedCount = usedCount;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return CouponStatus.ACTIVE == this.status;
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }

    public boolean hasReachedMaxUses() {
        return maxUses != null && usedCount >= maxUses;
    }

    public boolean isApplicableTo(BigDecimal cartSubtotal) {
        return minOrderAmount == null || cartSubtotal.compareTo(minOrderAmount) >= 0;
    }

    public BigDecimal calculateDiscount(BigDecimal subtotal) {
        if (discountPercent != null && discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            return subtotal.multiply(discountPercent).divide(BigDecimal.valueOf(100));
        }
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            return discountAmount.min(subtotal);
        }
        return BigDecimal.ZERO;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public CouponStatus getStatus() { return status; }
    public void setStatus(CouponStatus status) { this.status = status; }

    public BigDecimal getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(BigDecimal discountPercent) { this.discountPercent = discountPercent; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(BigDecimal minOrderAmount) { this.minOrderAmount = minOrderAmount; }

    public Integer getMaxUses() { return maxUses; }
    public void setMaxUses(Integer maxUses) { this.maxUses = maxUses; }

    public int getUsedCount() { return usedCount; }
    public void setUsedCount(int usedCount) { this.usedCount = usedCount; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
