package com.mycompany.webstore.infrastructure.persistence.adapter;

import com.mycompany.webstore.domain.model.Product;
import com.mycompany.webstore.infrastructure.persistence.entity.ProductJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductPersistenceMapper {

    ProductJpaEntity toJpaEntity(Product product);

    Product toDomain(ProductJpaEntity entity);
}
