package com.mycompany.webstore.application.service;

import com.mycompany.webstore.application.port.in.CartPort;
import com.mycompany.webstore.application.port.out.CartRepository;
import com.mycompany.webstore.application.port.out.CouponRepository;
import com.mycompany.webstore.application.port.out.ProductRepository;
import com.mycompany.webstore.domain.model.*;
import com.mycompany.webstore.shared.exception.BusinessRuleException;
import com.mycompany.webstore.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartPortImpl implements CartPort {

    private static final int ANONYMOUS_CART_TTL_MINUTES = 30;
    private static final int CUSTOMER_CART_TTL_DAYS = 7;

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;

    public CartPortImpl(CartRepository cartRepository,
                        ProductRepository productRepository,
                        CouponRepository couponRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.couponRepository = couponRepository;
    }

    @Override
    public Cart getCart(UUID customerId, String sessionId) {
        return resolveOrCreateCart(customerId, sessionId);
    }

    @Override
    @Transactional
    public Cart addItemToCart(UUID customerId, String sessionId, UUID productId, int quantity) {
        if (quantity <= 0) {
            throw new BusinessRuleException("Quantity must be greater than 0");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        if (product.isArchived()) {
            throw new BusinessRuleException("Cannot add archived product to cart");
        }
        if (product.getStockQuantity() <= 0) {
            throw new BusinessRuleException("Product is out of stock: " + product.getName());
        }

        Cart cart = resolveOrCreateCart(customerId, sessionId);

        Optional<CartItem> existingItem = cart.findItemByProductId(productId);
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQty = item.getQuantity() + quantity;
            if (newQty > product.getStockQuantity()) {
                throw new BusinessRuleException("Requested quantity exceeds available stock for: " + product.getName());
            }
            item.setQuantity(newQty);
            item.setUpdatedAt(Instant.now());
        } else {
            if (quantity > product.getStockQuantity()) {
                throw new BusinessRuleException("Requested quantity exceeds available stock for: " + product.getName());
            }
            CartItem newItem = new CartItem(
                    UUID.randomUUID(), cart.getId(), productId,
                    product.getName(), product.getSku(),
                    product.getPrice(), product.getCurrency(),
                    quantity, true,
                    Instant.now(), Instant.now()
            );
            cart.getItems().add(newItem);
        }

        cart.setUpdatedAt(Instant.now());
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart updateCartItemQuantity(UUID customerId, String sessionId, UUID itemId, int quantity) {
        Cart cart = resolveOrCreateCart(customerId, sessionId);

        CartItem item = cart.findItemById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + itemId));

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductId()));
            if (quantity > product.getStockQuantity()) {
                throw new BusinessRuleException("Requested quantity exceeds available stock for: " + product.getName());
            }
            item.setQuantity(quantity);
            item.setUpdatedAt(Instant.now());
        }

        cart.setUpdatedAt(Instant.now());
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart removeCartItem(UUID customerId, String sessionId, UUID itemId) {
        Cart cart = resolveOrCreateCart(customerId, sessionId);

        CartItem item = cart.findItemById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + itemId));

        cart.getItems().remove(item);
        cart.setUpdatedAt(Instant.now());
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart applyCoupon(UUID customerId, String sessionId, String couponCode) {
        Cart cart = resolveOrCreateCart(customerId, sessionId);

        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found: " + couponCode));

        if (!coupon.isActive()) {
            throw new BusinessRuleException("Coupon is not active: " + couponCode);
        }
        if (coupon.isExpired()) {
            throw new BusinessRuleException("Coupon has expired: " + couponCode);
        }
        if (coupon.hasReachedMaxUses()) {
            throw new BusinessRuleException("Coupon has reached its maximum usage limit: " + couponCode);
        }

        BigDecimal subtotal = cart.getSubtotal();
        if (!coupon.isApplicableTo(subtotal)) {
            throw new BusinessRuleException(
                    "Cart subtotal does not meet the minimum order amount for coupon: " + couponCode);
        }

        BigDecimal discount = coupon.calculateDiscount(subtotal);
        cart.setCouponCode(couponCode);
        cart.setDiscountAmount(discount);
        cart.setUpdatedAt(Instant.now());
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart removeCoupon(UUID customerId, String sessionId) {
        Cart cart = resolveOrCreateCart(customerId, sessionId);
        cart.setCouponCode(null);
        cart.setDiscountAmount(null);
        cart.setUpdatedAt(Instant.now());
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart refreshCartPrices(UUID customerId, String sessionId) {
        Cart cart = resolveOrCreateCart(customerId, sessionId);

        for (CartItem item : cart.getItems()) {
            Optional<Product> productOpt = productRepository.findById(item.getProductId());
            if (productOpt.isEmpty() || productOpt.get().isArchived()
                    || productOpt.get().getStockQuantity() <= 0) {
                item.setAvailable(false);
            } else {
                Product product = productOpt.get();
                item.setUnitPrice(product.getPrice());
                item.setAvailable(true);
            }
            item.setUpdatedAt(Instant.now());
        }

        if (cart.getCouponCode() != null) {
            couponRepository.findByCode(cart.getCouponCode()).ifPresentOrElse(
                    coupon -> {
                        if (!coupon.isActive() || coupon.isExpired() || coupon.hasReachedMaxUses()) {
                            cart.setCouponCode(null);
                            cart.setDiscountAmount(null);
                        } else {
                            cart.setDiscountAmount(coupon.calculateDiscount(cart.getSubtotal()));
                        }
                    },
                    () -> {
                        cart.setCouponCode(null);
                        cart.setDiscountAmount(null);
                    }
            );
        }

        cart.setUpdatedAt(Instant.now());
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart mergeAnonymousCart(UUID customerId, String sessionId) {
        Optional<Cart> anonymousCartOpt = cartRepository.findActiveBySessionId(sessionId);
        if (anonymousCartOpt.isEmpty()) {
            return resolveOrCreateCart(customerId, null);
        }

        Cart anonymousCart = anonymousCartOpt.get();
        Cart customerCart = resolveOrCreateCart(customerId, null);

        for (CartItem anonymousItem : anonymousCart.getItems()) {
            Product product = productRepository.findById(anonymousItem.getProductId()).orElse(null);
            if (product == null || product.isArchived()) continue;

            Optional<CartItem> existingItem = customerCart.findItemByProductId(anonymousItem.getProductId());
            if (existingItem.isPresent()) {
                int mergedQty = Math.min(
                        existingItem.get().getQuantity() + anonymousItem.getQuantity(),
                        product.getStockQuantity()
                );
                existingItem.get().setQuantity(mergedQty);
                existingItem.get().setUpdatedAt(Instant.now());
            } else {
                int qty = Math.min(anonymousItem.getQuantity(), product.getStockQuantity());
                if (qty > 0) {
                    CartItem merged = new CartItem(
                            UUID.randomUUID(), customerCart.getId(),
                            anonymousItem.getProductId(),
                            anonymousItem.getProductName(),
                            anonymousItem.getProductSku(),
                            product.getPrice(), product.getCurrency(),
                            qty, true,
                            Instant.now(), Instant.now()
                    );
                    customerCart.getItems().add(merged);
                }
            }
        }

        anonymousCart.setStatus(CartStatus.MERGED);
        cartRepository.save(anonymousCart);

        customerCart.setUpdatedAt(Instant.now());
        return cartRepository.save(customerCart);
    }

    @Override
    @Transactional
    public void clearCart(UUID cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found: " + cartId));
        cart.setItems(new ArrayList<>());
        cart.setCouponCode(null);
        cart.setDiscountAmount(null);
        cart.setStatus(CartStatus.CHECKED_OUT);
        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);
    }

    private Cart resolveOrCreateCart(UUID customerId, String sessionId) {
        if (customerId != null) {
            return cartRepository.findActiveByCustomerId(customerId)
                    .orElseGet(() -> createCartForCustomer(customerId));
        }
        if (sessionId != null) {
            return cartRepository.findActiveBySessionId(sessionId)
                    .orElseGet(() -> createCartForSession(sessionId));
        }
        throw new BusinessRuleException("Either customerId or sessionId must be provided");
    }

    private Cart createCartForCustomer(UUID customerId) {
        Cart cart = new Cart();
        cart.setId(UUID.randomUUID());
        cart.setCustomerId(customerId);
        cart.setStatus(CartStatus.ACTIVE);
        cart.setItems(new ArrayList<>());
        cart.setCreatedAt(Instant.now());
        cart.setUpdatedAt(Instant.now());
        cart.setExpiresAt(Instant.now().plus(CUSTOMER_CART_TTL_DAYS, ChronoUnit.DAYS));
        return cartRepository.save(cart);
    }

    private Cart createCartForSession(String sessionId) {
        Cart cart = new Cart();
        cart.setId(UUID.randomUUID());
        cart.setSessionId(sessionId);
        cart.setStatus(CartStatus.ACTIVE);
        cart.setItems(new ArrayList<>());
        cart.setCreatedAt(Instant.now());
        cart.setUpdatedAt(Instant.now());
        cart.setExpiresAt(Instant.now().plus(ANONYMOUS_CART_TTL_MINUTES, ChronoUnit.MINUTES));
        return cartRepository.save(cart);
    }
}
