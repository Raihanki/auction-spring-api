-- auction_app.categories definition

CREATE TABLE categories (
  id bigint NOT NULL AUTO_INCREMENT,
  name varchar(200) NOT NULL,
  is_active tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=824 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Insert initial categories
INSERT INTO `categories` (`name`, `is_active`) VALUES ('Electronics', 1);
INSERT INTO `categories` (`name`, `is_active`) VALUES ('Books', 1);


-- auction_app.users definition

CREATE TABLE users (
  id bigint NOT NULL AUTO_INCREMENT,
  name varchar(200) NOT NULL,
  email varchar(100) NOT NULL,
  password varchar(200) NOT NULL,
  is_verified tinyint(1) DEFAULT '0',
  role enum('ADMIN','OWNER','USER') DEFAULT 'USER',
  created_at timestamp NULL DEFAULT NULL,
  updated_at timestamp NULL DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY email (email)
) ENGINE=InnoDB AUTO_INCREMENT=1092 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- auction_app.products definition

CREATE TABLE products (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL,
  category_id bigint NOT NULL,
  winner_user_id bigint DEFAULT NULL,
  name varchar(200) NOT NULL,
  description text NOT NULL,
  start_price decimal(12,2) NOT NULL,
  price_multiples decimal(12,2) NOT NULL,
  end_price decimal(12,2) DEFAULT NULL,
  currency enum('IDR','USD') NOT NULL,
  end_auction_date timestamp NOT NULL,
  created_at timestamp NULL DEFAULT NULL,
  updated_at timestamp NULL DEFAULT NULL,
  PRIMARY KEY (id),
  KEY user_id (user_id),
  KEY category_id (category_id),
  KEY winner_user_id (winner_user_id),
  CONSTRAINT products_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT products_ibfk_2 FOREIGN KEY (category_id) REFERENCES categories (id),
  CONSTRAINT products_ibfk_3 FOREIGN KEY (winner_user_id) REFERENCES users (id)
) ENGINE=InnoDB AUTO_INCREMENT=444 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- auction_app.auctions definition

CREATE TABLE auctions (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL,
  product_id bigint NOT NULL,
  price decimal(12,2) NOT NULL,
  created_at timestamp NULL DEFAULT NULL,
  updated_at timestamp NULL DEFAULT NULL,
  PRIMARY KEY (id),
  KEY user_id (user_id),
  KEY product_id (product_id),
  CONSTRAINT auctions_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT auctions_ibfk_2 FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB AUTO_INCREMENT=130 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- auction_app.images definition

CREATE TABLE images (
  id bigint NOT NULL AUTO_INCREMENT,
  product_id bigint NOT NULL,
  image_url text NOT NULL,
  created_at timestamp NULL DEFAULT NULL,
  updated_at timestamp NULL DEFAULT NULL,
  PRIMARY KEY (id),
  KEY product_id (product_id),
  CONSTRAINT images_ibfk_1 FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB AUTO_INCREMENT=273 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;