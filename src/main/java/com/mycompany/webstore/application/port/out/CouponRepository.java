package com.mycompany.webstore.application.port.out;

import com.mycompany.webstore.domain.model.Coupon;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository {

    Optional<Coupon> findByCode(String code);

    Optional<Coupon> findById(UUID id);

    Coupon save(Coupon coupon);
}
