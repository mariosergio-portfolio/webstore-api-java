package com.mycompany.webstore.infrastructure.persistence.adapter;

import com.mycompany.webstore.domain.model.Cart;
import com.mycompany.webstore.domain.model.CartItem;
import com.mycompany.webstore.infrastructure.persistence.entity.CartItemJpaEntity;
import com.mycompany.webstore.infrastructure.persistence.entity.CartJpaEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartPersistenceMapper {

    @Mapping(target = "items", ignore = true)
    CartJpaEntity toJpaEntity(Cart cart);

    @AfterMapping
    default void linkItems(@MappingTarget CartJpaEntity entity, Cart cart) {
        entity.getItems().clear();
        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                CartItemJpaEntity itemEntity = toItemJpaEntity(item);
                itemEntity.setCart(entity);
                entity.getItems().add(itemEntity);
            }
        }
    }

    @Mapping(target = "cartId", source = "cart.id")
    CartItem toItemDomain(CartItemJpaEntity entity);

    @Mapping(target = "cart", ignore = true)
    CartItemJpaEntity toItemJpaEntity(CartItem item);

    @Mapping(target = "items", expression = "java(toItemDomainList(entity.getItems()))")
    Cart toDomain(CartJpaEntity entity);

    List<CartItem> toItemDomainList(List<CartItemJpaEntity> entities);
}
