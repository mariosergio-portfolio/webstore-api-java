package com.mycompany.webstore.infrastructure.rest.catalog;

import com.mycompany.webstore.domain.model.Category;
import com.mycompany.webstore.infrastructure.rest.catalog.dto.CategoryRequest;
import com.mycompany.webstore.infrastructure.rest.catalog.dto.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryRestMapper {

    @Mapping(target = "id", ignore = true)
    Category toDomain(CategoryRequest request);

    CategoryResponse toResponse(Category category);
}
