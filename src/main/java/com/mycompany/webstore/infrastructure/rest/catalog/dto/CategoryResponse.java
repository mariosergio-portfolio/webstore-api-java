package com.mycompany.webstore.infrastructure.rest.catalog.dto;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String slug,
        UUID parentId,
        int sortOrder
) {}
