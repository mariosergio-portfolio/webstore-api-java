package com.mycompany.webstore.infrastructure.persistence.adapter;

import com.mycompany.webstore.application.port.out.CouponRepository;
import com.mycompany.webstore.domain.model.Coupon;
import com.mycompany.webstore.infrastructure.persistence.repository.CouponJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CouponPersistenceAdapterImpl implements CouponRepository {

    private final CouponJpaRepository jpaRepository;
    private final CouponPersistenceMapper mapper;

    public CouponPersistenceAdapterImpl(CouponJpaRepository jpaRepository, CouponPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        return jpaRepository.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public Optional<Coupon> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Coupon save(Coupon coupon) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(coupon)));
    }
}
