package com.mycompany.webstore.infrastructure.persistence.repository;

import com.mycompany.webstore.domain.model.CartStatus;
import com.mycompany.webstore.infrastructure.persistence.entity.CartJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartJpaRepository extends JpaRepository<CartJpaEntity, UUID> {

    Optional<CartJpaEntity> findByCustomerIdAndStatus(UUID customerId, CartStatus status);

    Optional<CartJpaEntity> findBySessionIdAndStatus(String sessionId, CartStatus status);
}
