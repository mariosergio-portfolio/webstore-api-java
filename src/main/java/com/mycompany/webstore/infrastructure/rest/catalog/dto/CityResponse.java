package com.mycompany.webstore.infrastructure.rest.catalog.dto;

import java.util.UUID;

public record CityResponse(UUID id, String name, String state, String country,
                           double latitude, double longitude) {}
