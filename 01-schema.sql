-- Vishmi Bakehouse: corrected ER diagram -> MySQL schema
-- Target: MySQL 8.0.16+ (including MySQL 8.4). Run on a NEW database.
-- This script deliberately fails on existing tables; it never drops user data.
-- Opening balances must be entered as purchase/production records.
CREATE DATABASE IF NOT EXISTS vishmi_bakehouse
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE vishmi_bakehouse;

CREATE TABLE employee (
  employee_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  phone VARCHAR(30) NULL,
  address VARCHAR(500) NULL,
  job_role ENUM('OWNER','MANAGER','PRODUCTION','SALES','PACKING','DELIVERY') NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT ck_employee_1 CHECK (CHAR_LENGTH(TRIM(name)) > 0),
  CONSTRAINT ck_employee_2 CHECK (active IN (0,1))
) ENGINE=InnoDB;

CREATE TABLE user_account (
  user_account_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  employee_id BIGINT UNSIGNED NOT NULL,
  username VARCHAR(80) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  access_role ENUM('OWNER','MANAGER','PRODUCTION','SALES','PACKING','DELIVERY') NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_user_account_employee_id FOREIGN KEY (employee_id) REFERENCES employee (employee_id),
  CONSTRAINT ck_user_account_1 CHECK (CHAR_LENGTH(TRIM(username)) > 0),
  CONSTRAINT ck_user_account_2 CHECK (CHAR_LENGTH(password_hash) >= 20),
  CONSTRAINT ck_user_account_3 CHECK (active IN (0,1)),
  CONSTRAINT uq_user_account_1 UNIQUE (employee_id),
  CONSTRAINT uq_user_account_2 UNIQUE (username)
) ENGINE=InnoDB;

CREATE TABLE product (
  product_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  category VARCHAR(80) NOT NULL,
  unit ENUM('PIECE','KG') NOT NULL DEFAULT 'PIECE',
  selling_price DECIMAL(12,2) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT ck_product_1 CHECK (selling_price >= 0),
  CONSTRAINT ck_product_2 CHECK (CHAR_LENGTH(TRIM(name)) > 0),
  CONSTRAINT ck_product_3 CHECK (active IN (0,1)),
  INDEX ix_product_1 (name)
) ENGINE=InnoDB;

CREATE TABLE customer (
  customer_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  phone VARCHAR(30) NULL,
  address VARCHAR(500) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT ck_customer_1 CHECK (CHAR_LENGTH(TRIM(name)) > 0),
  INDEX ix_customer_1 (phone)
) ENGINE=InnoDB;

CREATE TABLE ingredient (
  ingredient_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  unit ENUM('KG','G','L','ML','PIECE') NOT NULL,
  reorder_level DECIMAL(14,3) NOT NULL DEFAULT 0,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT ck_ingredient_1 CHECK (reorder_level >= 0),
  CONSTRAINT ck_ingredient_2 CHECK (CHAR_LENGTH(TRIM(name)) > 0),
  CONSTRAINT ck_ingredient_3 CHECK (active IN (0,1))
) ENGINE=InnoDB;

CREATE TABLE shop (
  shop_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  phone VARCHAR(30) NULL,
  address VARCHAR(500) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT ck_shop_1 CHECK (CHAR_LENGTH(TRIM(name)) > 0),
  CONSTRAINT ck_shop_2 CHECK (active IN (0,1))
) ENGINE=InnoDB;

CREATE TABLE vehicle (
  vehicle_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  registration_no VARCHAR(30) NOT NULL,
  model VARCHAR(80) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT ck_vehicle_1 CHECK (CHAR_LENGTH(TRIM(registration_no)) > 0),
  CONSTRAINT ck_vehicle_2 CHECK (active IN (0,1)),
  CONSTRAINT uq_vehicle_1 UNIQUE (registration_no)
) ENGINE=InnoDB;

CREATE TABLE ingredient_purchase (
  ingredient_purchase_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  ingredient_id BIGINT UNSIGNED NOT NULL,
  recorded_by_employee_id BIGINT UNSIGNED NOT NULL,
  purchased_at DATETIME NOT NULL,
  qty DECIMAL(14,3) NOT NULL,
  unit_cost DECIMAL(12,2) NOT NULL,
  supplier_name VARCHAR(150) NULL,
  total_cost DECIMAL(18,2) GENERATED ALWAYS AS (ROUND(qty * unit_cost, 2)) STORED,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ingredient_purchase_ingredient_id FOREIGN KEY (ingredient_id) REFERENCES ingredient (ingredient_id),
  CONSTRAINT fk_ingredient_purchase_recorded_by_employee_id FOREIGN KEY (recorded_by_employee_id) REFERENCES employee (employee_id),
  CONSTRAINT ck_ingredient_purchase_1 CHECK (qty > 0),
  CONSTRAINT ck_ingredient_purchase_2 CHECK (unit_cost >= 0),
  INDEX ix_ingredient_purchase_1 (ingredient_id, purchased_at),
  INDEX ix_ingredient_purchase_2 (purchased_at)
) ENGINE=InnoDB;

CREATE TABLE production_batch (
  production_batch_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT UNSIGNED NOT NULL,
  recorded_by_employee_id BIGINT UNSIGNED NOT NULL,
  produced_on DATE NOT NULL,
  planned_qty DECIMAL(14,3) NOT NULL,
  actual_qty DECIMAL(14,3) NOT NULL DEFAULT 0,
  status ENUM('PLANNED','IN_PROGRESS','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PLANNED',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_production_batch_product_id FOREIGN KEY (product_id) REFERENCES product (product_id),
  CONSTRAINT fk_production_batch_recorded_by_employee_id FOREIGN KEY (recorded_by_employee_id) REFERENCES employee (employee_id),
  CONSTRAINT ck_production_batch_1 CHECK (planned_qty > 0),
  CONSTRAINT ck_production_batch_2 CHECK (actual_qty >= 0),
  CONSTRAINT ck_production_batch_3 CHECK (status <> 'COMPLETED' OR actual_qty > 0),
  INDEX ix_production_batch_1 (product_id, produced_on),
  INDEX ix_production_batch_2 (produced_on, status)
) ENGINE=InnoDB;

CREATE TABLE ingredient_usage (
  ingredient_usage_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  production_batch_id BIGINT UNSIGNED NOT NULL,
  ingredient_id BIGINT UNSIGNED NOT NULL,
  recorded_by_employee_id BIGINT UNSIGNED NOT NULL,
  used_at DATETIME NOT NULL,
  qty DECIMAL(14,3) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ingredient_usage_production_batch_id FOREIGN KEY (production_batch_id) REFERENCES production_batch (production_batch_id),
  CONSTRAINT fk_ingredient_usage_ingredient_id FOREIGN KEY (ingredient_id) REFERENCES ingredient (ingredient_id),
  CONSTRAINT fk_ingredient_usage_recorded_by_employee_id FOREIGN KEY (recorded_by_employee_id) REFERENCES employee (employee_id),
  CONSTRAINT ck_ingredient_usage_1 CHECK (qty > 0),
  INDEX ix_ingredient_usage_1 (ingredient_id, used_at)
) ENGINE=InnoDB;

CREATE TABLE sale (
  sale_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  cashier_employee_id BIGINT UNSIGNED NOT NULL,
  sold_at DATETIME NOT NULL,
  cash_received DECIMAL(14,2) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_sale_cashier_employee_id FOREIGN KEY (cashier_employee_id) REFERENCES employee (employee_id),
  CONSTRAINT ck_sale_1 CHECK (cash_received >= 0),
  INDEX ix_sale_1 (sold_at)
) ENGINE=InnoDB;

CREATE TABLE sale_item (
  sale_item_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  sale_id BIGINT UNSIGNED NOT NULL,
  product_id BIGINT UNSIGNED NOT NULL,
  quantity DECIMAL(14,3) NOT NULL,
  unit_price DECIMAL(12,2) NOT NULL,
  line_total DECIMAL(18,2) GENERATED ALWAYS AS (ROUND(quantity * unit_price, 2)) STORED,
  CONSTRAINT fk_sale_item_sale_id FOREIGN KEY (sale_id) REFERENCES sale (sale_id),
  CONSTRAINT fk_sale_item_product_id FOREIGN KEY (product_id) REFERENCES product (product_id),
  CONSTRAINT ck_sale_item_1 CHECK (quantity > 0),
  CONSTRAINT ck_sale_item_2 CHECK (unit_price >= 0),
  INDEX ix_sale_item_1 (product_id)
) ENGINE=InnoDB;

CREATE TABLE special_order (
  special_order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  customer_id BIGINT UNSIGNED NOT NULL,
  recorded_by_employee_id BIGINT UNSIGNED NOT NULL,
  order_type ENUM('WEDDING_CAKE','SPECIAL') NOT NULL,
  required_at DATETIME NOT NULL,
  status ENUM('PENDING','CONFIRMED','IN_PROGRESS','READY','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PENDING',
  instructions TEXT NULL,
  deposit_amount DECIMAL(14,2) NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_special_order_customer_id FOREIGN KEY (customer_id) REFERENCES customer (customer_id),
  CONSTRAINT fk_special_order_recorded_by_employee_id FOREIGN KEY (recorded_by_employee_id) REFERENCES employee (employee_id),
  CONSTRAINT ck_special_order_1 CHECK (deposit_amount >= 0),
  INDEX ix_special_order_1 (required_at, status)
) ENGINE=InnoDB;

CREATE TABLE special_order_item (
  special_order_item_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  special_order_id BIGINT UNSIGNED NOT NULL,
  product_id BIGINT UNSIGNED NULL,
  description VARCHAR(500) NOT NULL,
  quantity DECIMAL(14,3) NOT NULL,
  unit_price DECIMAL(12,2) NOT NULL,
  line_total DECIMAL(18,2) GENERATED ALWAYS AS (ROUND(quantity * unit_price, 2)) STORED,
  CONSTRAINT fk_special_order_item_special_order_id FOREIGN KEY (special_order_id) REFERENCES special_order (special_order_id),
  CONSTRAINT fk_special_order_item_product_id FOREIGN KEY (product_id) REFERENCES product (product_id),
  CONSTRAINT ck_special_order_item_1 CHECK (quantity > 0),
  CONSTRAINT ck_special_order_item_2 CHECK (unit_price >= 0),
  CONSTRAINT ck_special_order_item_3 CHECK (CHAR_LENGTH(TRIM(description)) > 0)
) ENGINE=InnoDB;

CREATE TABLE leftover_batch (
  leftover_batch_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  production_batch_id BIGINT UNSIGNED NOT NULL,
  recorded_by_employee_id BIGINT UNSIGNED NOT NULL,
  recorded_on DATE NOT NULL,
  qty_pieces INT UNSIGNED NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_leftover_batch_production_batch_id FOREIGN KEY (production_batch_id) REFERENCES production_batch (production_batch_id),
  CONSTRAINT fk_leftover_batch_recorded_by_employee_id FOREIGN KEY (recorded_by_employee_id) REFERENCES employee (employee_id),
  CONSTRAINT ck_leftover_batch_1 CHECK (qty_pieces > 0),
  INDEX ix_leftover_batch_1 (recorded_on)
) ENGINE=InnoDB;

CREATE TABLE bread_crumb_batch (
  bread_crumb_batch_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  output_product_id BIGINT UNSIGNED NOT NULL,
  recorded_by_employee_id BIGINT UNSIGNED NOT NULL,
  produced_at DATETIME NOT NULL,
  output_qty_kg DECIMAL(14,3) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_bread_crumb_batch_output_product_id FOREIGN KEY (output_product_id) REFERENCES product (product_id),
  CONSTRAINT fk_bread_crumb_batch_recorded_by_employee_id FOREIGN KEY (recorded_by_employee_id) REFERENCES employee (employee_id),
  CONSTRAINT ck_bread_crumb_batch_1 CHECK (output_qty_kg > 0),
  INDEX ix_bread_crumb_batch_1 (produced_at)
) ENGINE=InnoDB;

CREATE TABLE bread_crumb_input (
  bread_crumb_batch_id BIGINT UNSIGNED NOT NULL,
  leftover_batch_id BIGINT UNSIGNED NOT NULL,
  input_qty_pieces INT UNSIGNED NOT NULL,
  PRIMARY KEY (bread_crumb_batch_id, leftover_batch_id),
  CONSTRAINT fk_bread_crumb_input_bread_crumb_batch_id FOREIGN KEY (bread_crumb_batch_id) REFERENCES bread_crumb_batch (bread_crumb_batch_id),
  CONSTRAINT fk_bread_crumb_input_leftover_batch_id FOREIGN KEY (leftover_batch_id) REFERENCES leftover_batch (leftover_batch_id),
  CONSTRAINT ck_bread_crumb_input_1 CHECK (input_qty_pieces > 0)
) ENGINE=InnoDB;

CREATE TABLE delivery (
  delivery_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  shop_id BIGINT UNSIGNED NOT NULL,
  vehicle_id BIGINT UNSIGNED NULL,
  delivery_employee_id BIGINT UNSIGNED NULL,
  packed_by_employee_id BIGINT UNSIGNED NOT NULL,
  delivery_date DATE NOT NULL,
  status ENUM('PACKED','DISPATCHED','DELIVERED','CANCELLED') NOT NULL DEFAULT 'PACKED',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_delivery_shop_id FOREIGN KEY (shop_id) REFERENCES shop (shop_id),
  CONSTRAINT fk_delivery_vehicle_id FOREIGN KEY (vehicle_id) REFERENCES vehicle (vehicle_id),
  CONSTRAINT fk_delivery_delivery_employee_id FOREIGN KEY (delivery_employee_id) REFERENCES employee (employee_id),
  CONSTRAINT fk_delivery_packed_by_employee_id FOREIGN KEY (packed_by_employee_id) REFERENCES employee (employee_id),
  CONSTRAINT ck_delivery_1 CHECK (status NOT IN ('DISPATCHED','DELIVERED') OR (vehicle_id IS NOT NULL AND delivery_employee_id IS NOT NULL)),
  INDEX ix_delivery_1 (delivery_date, status)
) ENGINE=InnoDB;

CREATE TABLE delivery_item (
  delivery_item_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  delivery_id BIGINT UNSIGNED NOT NULL,
  product_id BIGINT UNSIGNED NOT NULL,
  packed_qty DECIMAL(14,3) NOT NULL,
  delivered_qty DECIMAL(14,3) NOT NULL DEFAULT 0,
  CONSTRAINT fk_delivery_item_delivery_id FOREIGN KEY (delivery_id) REFERENCES delivery (delivery_id),
  CONSTRAINT fk_delivery_item_product_id FOREIGN KEY (product_id) REFERENCES product (product_id),
  CONSTRAINT ck_delivery_item_1 CHECK (packed_qty > 0),
  CONSTRAINT ck_delivery_item_2 CHECK (delivered_qty >= 0),
  CONSTRAINT ck_delivery_item_3 CHECK (delivered_qty <= packed_qty),
  CONSTRAINT uq_delivery_item_1 UNIQUE (delivery_id, product_id)
) ENGINE=InnoDB;

-- Views aggregate each child set separately to avoid join multiplication.
CREATE VIEW v_ingredient_stock AS
SELECT i.ingredient_id, i.name, i.unit, i.reorder_level, i.active,
       COALESCE(p.purchased_qty,0) AS purchased_qty,
       COALESCE(u.used_qty,0) AS used_qty,
       COALESCE(p.purchased_qty,0)-COALESCE(u.used_qty,0) AS available_qty,
       (COALESCE(p.purchased_qty,0)-COALESCE(u.used_qty,0) <= i.reorder_level) AS low_stock
FROM ingredient i
LEFT JOIN (SELECT ingredient_id, SUM(qty) purchased_qty FROM ingredient_purchase GROUP BY ingredient_id) p
 ON p.ingredient_id=i.ingredient_id
LEFT JOIN (SELECT ingredient_id, SUM(qty) used_qty FROM ingredient_usage GROUP BY ingredient_id) u
 ON u.ingredient_id=i.ingredient_id;

CREATE VIEW v_sale_totals AS
SELECT s.sale_id, s.sold_at, s.cashier_employee_id, s.cash_received,
       COALESCE(t.item_count,0) AS item_count, COALESCE(t.total_amount,0) AS total_amount,
       s.cash_received-COALESCE(t.total_amount,0) AS change_amount
FROM sale s LEFT JOIN
 (SELECT sale_id, COUNT(*) item_count, SUM(line_total) total_amount FROM sale_item GROUP BY sale_id) t
 ON t.sale_id=s.sale_id;

CREATE VIEW v_special_order_totals AS
SELECT o.special_order_id, o.customer_id, o.required_at, o.status, o.deposit_amount,
       COALESCE(t.item_count,0) AS item_count, COALESCE(t.total_amount,0) AS total_amount,
       COALESCE(t.total_amount,0)-o.deposit_amount AS balance_amount
FROM special_order o LEFT JOIN
 (SELECT special_order_id, COUNT(*) item_count, SUM(line_total) total_amount
  FROM special_order_item GROUP BY special_order_id) t ON t.special_order_id=o.special_order_id;

CREATE VIEW v_leftover_balance AS
SELECT l.leftover_batch_id, l.production_batch_id, l.recorded_on, l.qty_pieces,
       COALESCE(t.used_pieces,0) AS used_pieces,
       l.qty_pieces-COALESCE(t.used_pieces,0) AS remaining_pieces
FROM leftover_batch l LEFT JOIN
 (SELECT leftover_batch_id, SUM(input_qty_pieces) used_pieces
  FROM bread_crumb_input GROUP BY leftover_batch_id) t ON t.leftover_batch_id=l.leftover_batch_id;

CREATE VIEW v_daily_sales AS
SELECT DATE(sold_at) sale_date, COUNT(*) transaction_count, SUM(total_amount) total_sales
FROM v_sale_totals GROUP BY DATE(sold_at);

CREATE VIEW v_delivery_summary AS
SELECT d.delivery_id, sh.name shop_name, v.registration_no,
       e.name delivery_staff, d.delivery_date, d.status,
       COALESCE(t.packed_qty,0) packed_qty, COALESCE(t.delivered_qty,0) delivered_qty
FROM delivery d JOIN shop sh ON sh.shop_id=d.shop_id
LEFT JOIN vehicle v ON v.vehicle_id=d.vehicle_id
LEFT JOIN employee e ON e.employee_id=d.delivery_employee_id
LEFT JOIN (SELECT delivery_id,SUM(packed_qty) packed_qty,SUM(delivered_qty) delivered_qty
           FROM delivery_item GROUP BY delivery_id) t ON t.delivery_id=d.delivery_id;

-- Diagnostic product stock, under this explicit policy:
-- PACKED deliveries reserve stock; DISPATCHED/DELIVERED consume packed_qty ONCE.
-- Cancelled deliveries do not reserve stock. Leftovers leave saleable stock immediately.
-- Special orders are custom-made separately and DO NOT reserve catalog stock here.
-- No returns, wastage, expiry or opening-balance tables are included in the agreed ER.
CREATE VIEW v_product_availability AS
SELECT p.product_id,p.name,p.unit,p.active,
 COALESCE(b.qty,0)+COALESCE(c.qty,0) produced_qty,
 COALESCE(s.qty,0) outlet_sold_qty,
 COALESCE(d.qty,0) shop_allocated_qty,
 COALESCE(l.qty,0) leftover_qty,
 COALESCE(b.qty,0)+COALESCE(c.qty,0)-COALESCE(s.qty,0)-COALESCE(d.qty,0)-COALESCE(l.qty,0) available_qty
FROM product p
LEFT JOIN (SELECT product_id,SUM(actual_qty) qty FROM production_batch
 WHERE status='COMPLETED' GROUP BY product_id) b ON b.product_id=p.product_id
LEFT JOIN (SELECT output_product_id,SUM(output_qty_kg) qty FROM bread_crumb_batch
 GROUP BY output_product_id) c ON c.output_product_id=p.product_id
LEFT JOIN (SELECT product_id,SUM(quantity) qty FROM sale_item GROUP BY product_id) s ON s.product_id=p.product_id
LEFT JOIN (SELECT di.product_id,SUM(di.packed_qty) qty FROM delivery_item di
 JOIN delivery d ON d.delivery_id=di.delivery_id WHERE d.status<>'CANCELLED'
 GROUP BY di.product_id) d ON d.product_id=p.product_id
LEFT JOIN (SELECT b.product_id,SUM(l.qty_pieces) qty FROM leftover_batch l
 JOIN production_batch b ON b.production_batch_id=l.production_batch_id GROUP BY b.product_id) l ON l.product_id=p.product_id;

-- Verify installation (19 base tables, 7 views).
SELECT TABLE_TYPE, COUNT(*) object_count FROM information_schema.tables
WHERE table_schema=DATABASE() GROUP BY TABLE_TYPE;
