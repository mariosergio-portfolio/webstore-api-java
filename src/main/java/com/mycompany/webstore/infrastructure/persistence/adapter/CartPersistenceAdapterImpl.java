package com.mycompany.webstore.infrastructure.persistence.adapter;

import com.mycompany.webstore.application.port.out.CartRepository;
import com.mycompany.webstore.domain.model.Cart;
import com.mycompany.webstore.domain.model.CartStatus;
import com.mycompany.webstore.infrastructure.persistence.entity.CartJpaEntity;
import com.mycompany.webstore.infrastructure.persistence.repository.CartJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CartPersistenceAdapterImpl implements CartRepository {

    private final CartJpaRepository jpaRepository;
    private final CartPersistenceMapper mapper;

    public CartPersistenceAdapterImpl(CartJpaRepository jpaRepository, CartPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Cart save(Cart cart) {

        CartJpaEntity cartSaved = jpaRepository.save(mapper.toJpaEntity(cart));

        return mapper.toDomain(cartSaved);
    }

    @Override
    public Optional<Cart> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Cart> findActiveByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Cart> findActiveBySessionId(String sessionId) {
        return jpaRepository.findBySessionIdAndStatus(sessionId, CartStatus.ACTIVE)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void updateStatus(UUID id, CartStatus status) {
        jpaRepository.findById(id).ifPresent(entity -> {
            entity.setStatus(status);
            jpaRepository.save(entity);
        });
    }
}
