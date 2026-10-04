package com.mycompany.webstore.infrastructure.rest.catalog.dto;

import java.util.UUID;

public record SupplierResponse(UUID id, String name, String email,
                               CityResponse addressCity, int productCount) {}
