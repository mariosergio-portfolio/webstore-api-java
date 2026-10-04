package com.mycompany.webstore.domain.model;

import java.util.UUID;

/** City with its WGS 84 coordinates (read model). */
public record City(UUID id, String name, String state, String country,
                   double latitude, double longitude) {}
