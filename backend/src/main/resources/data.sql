INSERT INTO users (email, password, first_name, last_name, role, active, created_at)
SELECT 'admin@shopflow.com', '{noop}Admin123!', 'System', 'Admin', 'ADMIN', true, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@shopflow.com'
);

INSERT INTO users (email, password, first_name, last_name, role, active, created_at)
SELECT 'client@shopflow.com', '{noop}Client123!', 'Demo', 'Client', 'CUSTOMER', true, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'client@shopflow.com'
);

INSERT INTO categories (name, description)
SELECT 'Electronics', 'Basic tech items for the demo'
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = 'Electronics'
);

INSERT INTO categories (name, description)
SELECT 'Clothes', 'Simple clothing products'
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = 'Clothes'
);

INSERT INTO categories (name, description)
SELECT 'Books', 'Books and study material'
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = 'Books'
);

INSERT INTO products (name, description, price, promo_price, stock, active, created_at, category_id)
SELECT
    'Wireless Mouse',
    'Comfortable wireless mouse for daily work.',
    25.00,
    19.99,
    20,
    true,
    CURRENT_TIMESTAMP,
    c.id
FROM categories c
WHERE c.name = 'Electronics'
  AND NOT EXISTS (
      SELECT 1 FROM products WHERE name = 'Wireless Mouse'
  );

INSERT INTO products (name, description, price, promo_price, stock, active, created_at, category_id)
SELECT
    'USB Keyboard',
    'Compact keyboard with USB connection.',
    35.00,
    NULL,
    15,
    true,
    CURRENT_TIMESTAMP,
    c.id
FROM categories c
WHERE c.name = 'Electronics'
  AND NOT EXISTS (
      SELECT 1 FROM products WHERE name = 'USB Keyboard'
  );

INSERT INTO products (name, description, price, promo_price, stock, active, created_at, category_id)
SELECT
    'Java Book',
    'Beginner friendly Java programming book.',
    29.90,
    24.90,
    12,
    true,
    CURRENT_TIMESTAMP,
    c.id
FROM categories c
WHERE c.name = 'Books'
  AND NOT EXISTS (
      SELECT 1 FROM products WHERE name = 'Java Book'
  );

INSERT INTO products (name, description, price, promo_price, stock, active, created_at, category_id)
SELECT
    'T-Shirt',
    'Minimal cotton t-shirt for the clothes category.',
    15.00,
    12.50,
    30,
    true,
    CURRENT_TIMESTAMP,
    c.id
FROM categories c
WHERE c.name = 'Clothes'
  AND NOT EXISTS (
      SELECT 1 FROM products WHERE name = 'T-Shirt'
  );

INSERT INTO carts (user_id, updated_at)
SELECT u.id, CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'admin@shopflow.com'
  AND NOT EXISTS (
      SELECT 1 FROM carts c WHERE c.user_id = u.id
  );

INSERT INTO carts (user_id, updated_at)
SELECT u.id, CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'client@shopflow.com'
  AND NOT EXISTS (
      SELECT 1 FROM carts c WHERE c.user_id = u.id
  );
