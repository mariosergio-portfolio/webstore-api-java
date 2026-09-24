-- V1__create_catalog_tables.sql
-- Catalog module: products and categories

CREATE TABLE categories (
    id          UUID         PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    slug        VARCHAR(200) NOT NULL UNIQUE,
    parent_id   UUID         REFERENCES categories(id),
    sort_order  INT          NOT NULL DEFAULT 0
);

CREATE TABLE products (
    id             UUID           PRIMARY KEY,
    sku            VARCHAR(100)   NOT NULL UNIQUE,
    name           VARCHAR(255)   NOT NULL,
    description    TEXT,
    price          NUMERIC(19, 4) NOT NULL,
    currency       VARCHAR(3)        NOT NULL,
    stock_quantity INT            NOT NULL DEFAULT 0,
    category_id    UUID           REFERENCES categories(id),
    image_urls     VARCHAR(4000),
    status         VARCHAR(20)    NOT NULL DEFAULT 'DRAFT',
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP    NOT NULL
);

CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_status      ON products(status);
CREATE INDEX idx_products_sku         ON products(sku);
CREATE INDEX idx_categories_slug      ON categories(slug);
CREATE INDEX idx_categories_parent_id ON categories(parent_id);
