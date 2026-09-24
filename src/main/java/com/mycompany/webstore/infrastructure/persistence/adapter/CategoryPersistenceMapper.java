package com.mycompany.webstore.infrastructure.persistence.adapter;

import com.mycompany.webstore.domain.model.Category;
import com.mycompany.webstore.infrastructure.persistence.entity.CategoryJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryPersistenceMapper {

    CategoryJpaEntity toJpaEntity(Category category);

    Category toDomain(CategoryJpaEntity entity);
}
