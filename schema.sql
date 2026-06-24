-- E-Commerce Cart & Checkout Module - Database Schema
-- Run this manually OR let JPA auto-create via ddl-auto=update

CREATE DATABASE IF NOT EXISTS ecommerce_db;
USE ecommerce_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    description VARCHAR(500),
    category VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT fk_cartitem_cart FOREIGN KEY (cart_id) REFERENCES carts(id),
    CONSTRAINT fk_cartitem_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    discount_type ENUM('PERCENTAGE', 'FLAT') NOT NULL,
    discount_value DECIMAL(10, 2) NOT NULL,
    expiry_date DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    final_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED') NOT NULL,
    coupon_code VARCHAR(50),
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_orderitem_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Sample seed data
INSERT INTO users (name, email) VALUES
('Alice Smith', 'alice@example.com'),
('Bob Jones',   'bob@example.com');

INSERT INTO products (name, price, stock_quantity, description, category) VALUES
('Laptop Pro',      50000.00, 10, '15-inch laptop with 16GB RAM', 'Electronics'),
('Wireless Mouse',    799.00, 50, 'Ergonomic wireless mouse',     'Electronics'),
('Mechanical Keyboard', 2999.00, 20, 'RGB mechanical keyboard',  'Electronics'),
('Running Shoes',    3500.00, 30, 'Lightweight running shoes',    'Footwear'),
('Water Bottle',      499.00, 100,'BPA-free 1L water bottle',    'Sports');

INSERT INTO coupons (code, discount_type, discount_value, expiry_date, active) VALUES
('SAVE10',  'PERCENTAGE', 10.00, '2026-12-31', TRUE),
('FLAT500', 'FLAT',      500.00, '2026-12-31', TRUE),
('WELCOME20','PERCENTAGE',20.00, '2026-06-30', TRUE);
