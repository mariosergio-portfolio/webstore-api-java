package com.mycompany.webstore.domain.event;

import java.time.Instant;
import java.util.UUID;

public record ProductArchived(UUID productId, String sku, Instant occurredAt) {

    public ProductArchived(UUID productId, String sku) {
        this(productId, sku, Instant.now());
    }
}
