package com.mycompany.webstore.domain.model;

import java.util.UUID;

/** Supplier with its address city and number of products it supplies (read model). */
public record Supplier(UUID id, String name, String email, City addressCity, int productCount) {}
