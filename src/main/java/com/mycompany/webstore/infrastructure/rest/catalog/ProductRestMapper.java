package com.mycompany.webstore.infrastructure.rest.catalog;

import com.mycompany.webstore.domain.model.Product;
import com.mycompany.webstore.infrastructure.rest.catalog.dto.ProductRequest;
import com.mycompany.webstore.infrastructure.rest.catalog.dto.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toDomain(ProductRequest request);

    ProductResponse toResponse(Product product);
}
