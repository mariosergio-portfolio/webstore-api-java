-- V2__create_cart_tables.sql
-- Cart module: carts, cart_items, coupons

CREATE TABLE coupons (
    id               UUID           PRIMARY KEY,
    code             VARCHAR(50)    NOT NULL UNIQUE,
    status           VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    discount_percent NUMERIC(5, 2),
    discount_amount  NUMERIC(19, 4),
    min_order_amount NUMERIC(19, 4),
    max_uses         INT,
    used_count       INT            NOT NULL DEFAULT 0,
    expires_at       TIMESTAMP,
    created_at       TIMESTAMP      NOT NULL,
    updated_at       TIMESTAMP      NOT NULL
);

CREATE TABLE carts (
    id               UUID           PRIMARY KEY,
    customer_id      UUID,
    session_id       VARCHAR(255),
    status           VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    coupon_code      VARCHAR(50),
    discount_amount  NUMERIC(19, 4),
    created_at       TIMESTAMP      NOT NULL,
    updated_at       TIMESTAMP      NOT NULL,
    expires_at       TIMESTAMP      NOT NULL
);

CREATE TABLE cart_items (
    id           UUID           PRIMARY KEY,
    cart_id      UUID           NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id   UUID           NOT NULL,
    product_name VARCHAR(255)   NOT NULL,
    product_sku  VARCHAR(100)   NOT NULL,
    unit_price   NUMERIC(19, 4) NOT NULL,
    currency     VARCHAR(3)        NOT NULL,
    quantity     INT            NOT NULL,
    available    BOOLEAN        NOT NULL DEFAULT TRUE,
    added_at     TIMESTAMP      NOT NULL,
    updated_at   TIMESTAMP      NOT NULL
);

CREATE INDEX idx_carts_customer_id      ON carts(customer_id);
CREATE INDEX idx_carts_session_id       ON carts(session_id);
CREATE INDEX idx_carts_status           ON carts(status);
CREATE INDEX idx_cart_items_cart_id     ON cart_items(cart_id);
CREATE INDEX idx_cart_items_product_id  ON cart_items(product_id);
CREATE INDEX idx_coupons_code           ON coupons(code);
CREATE INDEX idx_coupons_status         ON coupons(status);
