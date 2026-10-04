package com.mycompany.webstore.domain.model;

/** Read model: a product together with its (optional) category. */
public record ProductWithCategory(Product product, Category category) {}
