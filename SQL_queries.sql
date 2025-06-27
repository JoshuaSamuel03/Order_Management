create database ordermanagement;
use ordermanagement;

CREATE TABLE IF NOT EXISTS users (
    userId INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(10) NOT NULL CHECK (role IN ('Admin', 'User'))
);

CREATE TABLE products (
    productId INT AUTO_INCREMENT PRIMARY KEY,
    productName VARCHAR(100) NOT NULL,
    description TEXT,
    price DOUBLE NOT NULL CHECK (price >= 0),
    quantityInStock INT NOT NULL CHECK (quantityInStock >= 0),
    type VARCHAR(20) NOT NULL CHECK (type IN ('Electronics', 'Clothing')),
    brand VARCHAR(50),
    warrantyPeriod INT,
    size VARCHAR(10),
    color VARCHAR(20)
);


CREATE TABLE IF NOT EXISTS orders (
    orderId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    productId INT NOT NULL,
    orderDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE,
    FOREIGN KEY (productId) REFERENCES products(productId) ON DELETE CASCADE
);

INSERT INTO users (userId, username, password, role) VALUES
(101, 'alice_admin', 'alice123', 'Admin'),
(102, 'bob_admin', 'bob456', 'Admin'),
(103, 'carol_admin', 'carol789', 'Admin'),
(201, 'dave_user', 'dave123', 'User'),
(202, 'emma_user', 'emma123', 'User'),
(203, 'frank_user', 'frank123', 'User'),
(204, 'grace_user', 'grace123', 'User'),
(205, 'henry_user', 'henry123', 'User'),
(206, 'irene_user', 'irene123', 'User'),
(207, 'john_user', 'john123', 'User');


INSERT INTO products (productId, productName, description, price, quantityInStock, type, brand, warrantyPeriod, size, color) VALUES
(301, 'Samsung Galaxy S23', 'Flagship 5G Android smartphone', 74999.00, 25, 'Electronics', 'Samsung', 24, NULL, NULL),
(302, 'Apple MacBook Air M2', '13-inch Apple laptop', 99999.00, 10, 'Electronics', 'Apple', 12, NULL, NULL),
(303, 'Sony WH-1000XM5', 'Noise-cancelling headphones', 29999.00, 30, 'Electronics', 'Sony', 18, NULL, NULL),
(304, 'Dell XPS 15', 'High-end 15-inch Windows laptop', 124999.00, 8, 'Electronics', 'Dell', 36, NULL, NULL),
(305, 'iPad Pro 11"', 'Apple tablet with M2 chip', 85999.00, 15, 'Electronics', 'Apple', 12, NULL, NULL),
(306, 'Nike Air Max', 'Running shoes, size 9', 7999.00, 20, 'Clothing', NULL, NULL, '9', 'Black'),
(307, 'Levi\'s 511 Jeans', 'Slim-fit jeans, size 32', 3199.00, 35, 'Clothing', NULL, NULL, '32', 'Dark Blue'),
(308, 'Adidas T-shirt', 'Cotton t-shirt, size M', 1499.00, 50, 'Clothing', NULL, NULL, 'M', 'White'),
(309, 'Puma Hoodie', 'Winter hoodie, size L', 2499.00, 25, 'Clothing', NULL, NULL, 'L', 'Grey'),
(310, 'Zara Jacket', 'Men\'s casual jacket, size XL', 4999.00, 12, 'Clothing', NULL, NULL, 'XL', 'Olive Green');


INSERT INTO orders (userId, productId) VALUES
(201, 301),
(201, 306),
(202, 302),
(202, 307),
(203, 303),
(203, 308),
(204, 304),
(204, 309),
(205, 305),
(205, 310);
