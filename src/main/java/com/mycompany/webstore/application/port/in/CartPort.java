package com.mycompany.webstore.application.port.in;

import com.mycompany.webstore.domain.model.Cart;

import java.util.UUID;

public interface CartPort {

    Cart getCart(UUID customerId, String sessionId);

    Cart addItemToCart(UUID customerId, String sessionId, UUID productId, int quantity);

    Cart updateCartItemQuantity(UUID customerId, String sessionId, UUID itemId, int quantity);

    Cart removeCartItem(UUID customerId, String sessionId, UUID itemId);

    Cart applyCoupon(UUID customerId, String sessionId, String couponCode);

    Cart removeCoupon(UUID customerId, String sessionId);

    Cart refreshCartPrices(UUID customerId, String sessionId);

    Cart mergeAnonymousCart(UUID customerId, String sessionId);

    void clearCart(UUID cartId);
}
