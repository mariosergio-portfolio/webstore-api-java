package com.mycompany.webstore.infrastructure.persistence.adapter;

import com.mycompany.webstore.domain.model.Coupon;
import com.mycompany.webstore.infrastructure.persistence.entity.CouponJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CouponPersistenceMapper {

    CouponJpaEntity toJpaEntity(Coupon coupon);

    Coupon toDomain(CouponJpaEntity entity);
}
