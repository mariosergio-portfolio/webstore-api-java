package com.mycompany.webstore.infrastructure.rest.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record CategoryRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Slug is required")
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must be URL-safe (lowercase, hyphens only)")
        String slug,

        UUID parentId,

        int sortOrder
) {}
