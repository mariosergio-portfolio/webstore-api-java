package com.mycompany.webstore.domain.model;

import java.util.UUID;

public class Category {

    private UUID id;
    private String name;
    private String slug;
    private UUID parentId;
    private int sortOrder;

    public Category() {}

    public Category(UUID id, String name, String slug, UUID parentId, int sortOrder) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.parentId = parentId;
        this.sortOrder = sortOrder;
    }

    public boolean isRoot() {
        return this.parentId == null;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
