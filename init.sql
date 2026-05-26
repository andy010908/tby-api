CREATE DATABASE IF NOT EXISTS tby_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tby_db;

CREATE TABLE IF NOT EXISTS users (
    user_id   BIGINT       NOT NULL AUTO_INCREMENT,
    username  VARCHAR(100) NOT NULL,
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS product_category (
    category_id   BIGINT         NOT NULL AUTO_INCREMENT,
    category_name VARCHAR(100)   NOT NULL,
    tax_rate      DECIMAL(6, 4)  NOT NULL DEFAULT 0.0000,
    PRIMARY KEY (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS product (
    product_id          BIGINT         NOT NULL AUTO_INCREMENT,
    product_category_id BIGINT         NOT NULL,
    product_name        VARCHAR(200)   NOT NULL,
    unit_price          DECIMAL(10, 2) NOT NULL,
    stock               INT            NOT NULL DEFAULT 0,
    PRIMARY KEY (product_id),
    CONSTRAINT fk_product_category FOREIGN KEY (product_category_id)
        REFERENCES product_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS orders (
    order_id         BIGINT         NOT NULL AUTO_INCREMENT,
    user_id          BIGINT         NOT NULL,
    product_id       BIGINT         NOT NULL,
    order_amount     INT            NOT NULL,
    total_price      DECIMAL(10, 2) NOT NULL,
    created_at       DATETIME       NOT NULL,
    idempotency_key  VARCHAR(64)    NULL,
    PRIMARY KEY (order_id),
    UNIQUE KEY uk_idempotency_key (idempotency_key),
    CONSTRAINT fk_order_user    FOREIGN KEY (user_id)    REFERENCES users   (user_id),
    CONSTRAINT fk_order_product FOREIGN KEY (product_id) REFERENCES product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Sample seed data
INSERT INTO users (username) VALUES ('alice'), ('bob');

INSERT INTO product_category (category_name, tax_rate) VALUES
    ('Electronics', 0.0500),
    ('Food',        0.0800);

INSERT INTO product (product_category_id, product_name, unit_price, stock) VALUES
    (1, 'Wireless Mouse',  29.99, 10000),
    (1, 'USB-C Hub',       49.99, 10000),
    (2, 'Organic Coffee',  15.00, 10000);
