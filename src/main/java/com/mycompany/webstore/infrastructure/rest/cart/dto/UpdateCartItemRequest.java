package com.mycompany.webstore.infrastructure.rest.cart.dto;

import jakarta.validation.constraints.Min;

public record UpdateCartItemRequest(

        @Min(value = 0, message = "Quantity must be 0 or greater")
        int quantity
) {}
