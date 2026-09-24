package com.mycompany.webstore.infrastructure.rest.cart;

import com.mycompany.webstore.application.port.in.CartPort;
import com.mycompany.webstore.domain.model.Cart;
import com.mycompany.webstore.infrastructure.rest.cart.dto.AddCartItemRequest;
import com.mycompany.webstore.infrastructure.rest.cart.dto.ApplyCouponRequest;
import com.mycompany.webstore.infrastructure.rest.cart.dto.CartResponse;
import com.mycompany.webstore.infrastructure.rest.cart.dto.UpdateCartItemRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Cart")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartPort cartPort;
    private final CartRestMapper mapper;

    public CartController(CartPort cartPort, CartRestMapper mapper) {
        this.cartPort = cartPort;
        this.mapper = mapper;
    }

    @Operation(summary = "Get current cart (session or customer)")
    @GetMapping
    public CartResponse getCart(
            @RequestHeader(value = "X-Customer-Id", required = false) UUID customerId,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        Cart cart = cartPort.getCart(customerId, sessionId);
        return mapper.toResponse(cart);
    }

    @Operation(summary = "Add item to cart")
    @PostMapping("/items")
    public CartResponse addItem(
            @RequestHeader(value = "X-Customer-Id", required = false) UUID customerId,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId,
            @Valid @RequestBody AddCartItemRequest request) {
        Cart cart = cartPort.addItemToCart(customerId, sessionId, request.productId(), request.quantity());
        return mapper.toResponse(cart);
    }

    @Operation(summary = "Update cart item quantity (set to 0 to remove)")
    @PatchMapping("/items/{itemId}")
    public CartResponse updateItem(
            @RequestHeader(value = "X-Customer-Id", required = false) UUID customerId,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId,
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        Cart cart = cartPort.updateCartItemQuantity(customerId, sessionId, itemId, request.quantity());
        return mapper.toResponse(cart);
    }

    @Operation(summary = "Remove item from cart")
    @DeleteMapping("/items/{itemId}")
    public CartResponse removeItem(
            @RequestHeader(value = "X-Customer-Id", required = false) UUID customerId,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId,
            @PathVariable UUID itemId) {
        Cart cart = cartPort.removeCartItem(customerId, sessionId, itemId);
        return mapper.toResponse(cart);
    }

    @Operation(summary = "Apply coupon to cart")
    @PostMapping("/coupon")
    public CartResponse applyCoupon(
            @RequestHeader(value = "X-Customer-Id", required = false) UUID customerId,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId,
            @Valid @RequestBody ApplyCouponRequest request) {
        Cart cart = cartPort.applyCoupon(customerId, sessionId, request.code());
        return mapper.toResponse(cart);
    }

    @Operation(summary = "Remove coupon from cart")
    @DeleteMapping("/coupon")
    public CartResponse removeCoupon(
            @RequestHeader(value = "X-Customer-Id", required = false) UUID customerId,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        Cart cart = cartPort.removeCoupon(customerId, sessionId);
        return mapper.toResponse(cart);
    }

    @Operation(summary = "Refresh prices and stock availability")
    @PostMapping("/refresh")
    public CartResponse refreshPrices(
            @RequestHeader(value = "X-Customer-Id", required = false) UUID customerId,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        Cart cart = cartPort.refreshCartPrices(customerId, sessionId);
        return mapper.toResponse(cart);
    }

    @Operation(summary = "Merge anonymous cart into customer cart on login")
    @PostMapping("/merge")
    public CartResponse mergeCart(
            @RequestHeader("X-Customer-Id") UUID customerId,
            @RequestHeader("X-Session-Id") String sessionId) {
        Cart cart = cartPort.mergeAnonymousCart(customerId, sessionId);
        return mapper.toResponse(cart);
    }

    @Operation(summary = "Clear all items from cart")
    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @RequestHeader(value = "X-Customer-Id", required = false) UUID customerId,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        Cart cart = cartPort.getCart(customerId, sessionId);
        cartPort.clearCart(cart.getId());
        return ResponseEntity.noContent().build();
    }
}
