CREATE TABLE `products` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255) UNIQUE NOT NULL
);

CREATE TABLE `recipes` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `valid_from` datetime NOT NULL,
  `valid_to` datetime
);

CREATE TABLE `ingredients` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255) UNIQUE NOT NULL,
  `stock_quantity` float NOT NULL,
  `unit` varchar(255) NOT NULL
);

CREATE TABLE `recipe_ingredients` (
  `recipe_id` int NOT NULL,
  `ingredient_id` int NOT NULL,
  `amount_per_100_cookies` float NOT NULL,
  PRIMARY KEY (`recipe_id`, `ingredient_id`)
);

CREATE TABLE `ingredient_deliveries` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `ingredient_id` int NOT NULL,
  `quantity` float NOT NULL,
  `delivery_time` datetime NOT NULL
);

CREATE TABLE `pallets` (
  `id` int PRIMARY KEY,
  `product_id` int NOT NULL,
  `production_time` datetime NOT NULL,
  `status` varchar(255) NOT NULL
);

CREATE TABLE `customers` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL
);

CREATE TABLE `orders` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `order_time` datetime NOT NULL,
  `delivery_time` datetime
);

CREATE TABLE `order_items` (
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity_pallets` int NOT NULL,
  PRIMARY KEY (`order_id`, `product_id`)
);

CREATE TABLE `deliveries` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `delivery_time` datetime NOT NULL
);

CREATE TABLE `delivery_pallets` (
  `delivery_id` int NOT NULL,
  `pallet_id` int NOT NULL,
  PRIMARY KEY (`delivery_id`, `pallet_id`)
);

CREATE TABLE `blocked_intervals` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL
);

ALTER TABLE recipes ADD FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE recipe_ingredients ADD FOREIGN KEY (recipe_id) REFERENCES recipes (id);

ALTER TABLE recipe_ingredients ADD FOREIGN KEY (ingredient_id) REFERENCES ingredients (id);

ALTER TABLE ingredient_deliveries ADD FOREIGN KEY (ingredient_id) REFERENCES ingredients (id);

ALTER TABLE pallets ADD FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE orders ADD FOREIGN KEY (customer_id) REFERENCES customers (id);

ALTER TABLE order_items ADD FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE order_items ADD FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE deliveries ADD FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE delivery_pallets ADD FOREIGN KEY (delivery_id) REFERENCES deliveries (id);

ALTER TABLE delivery_pallets ADD FOREIGN KEY (pallet_id) REFERENCES pallets (id);

ALTER TABLE blocked_intervals ADD FOREIGN KEY (product_id) REFERENCES products (id);
