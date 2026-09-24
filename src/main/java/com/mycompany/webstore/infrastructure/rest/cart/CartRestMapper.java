package com.mycompany.webstore.infrastructure.rest.cart;

import com.mycompany.webstore.domain.model.Cart;
import com.mycompany.webstore.domain.model.CartItem;
import com.mycompany.webstore.infrastructure.rest.cart.dto.CartItemResponse;
import com.mycompany.webstore.infrastructure.rest.cart.dto.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartRestMapper {

    @Mapping(target = "subtotal", expression = "java(item.getSubtotal())")
    CartItemResponse toItemResponse(CartItem item);

    @Mapping(target = "items", expression = "java(cart.getItems().stream().map(this::toItemResponse).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "itemCount", expression = "java(cart.getItems().size())")
    @Mapping(target = "subtotal", expression = "java(cart.getSubtotal())")
    @Mapping(target = "total", expression = "java(cart.getTotal())")
    @Mapping(target = "hasUnavailableItems", expression = "java(cart.hasUnavailableItems())")
    CartResponse toResponse(Cart cart);
}
