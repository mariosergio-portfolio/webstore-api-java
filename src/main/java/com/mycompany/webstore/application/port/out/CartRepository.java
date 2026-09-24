package com.mycompany.webstore.application.port.out;

import com.mycompany.webstore.domain.model.Cart;
import com.mycompany.webstore.domain.model.CartStatus;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository {

    Cart save(Cart cart);

    Optional<Cart> findById(UUID id);

    Optional<Cart> findActiveByCustomerId(UUID customerId);

    Optional<Cart> findActiveBySessionId(String sessionId);

    void deleteById(UUID id);

    void updateStatus(UUID id, CartStatus status);
}
