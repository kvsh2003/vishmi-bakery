-- Seed data for Vishmi Bakehouse Management System
USE vishmi_bakehouse;

-- 1. Employees (10 Employees matching proposal structure)
INSERT INTO employee (employee_id, name, phone, address, job_role, active) VALUES
(1, 'Naveesha Niyumal', '0771234001', '12 Temple Road, Maharagama', 'OWNER', 1),
(2, 'Sanduni Wickramasinghe', '0771234002', '45 Station Road, Nugegoda', 'MANAGER', 1),
(3, 'Sunil Perera', '0771234003', '88 Highlevel Road, Pannipitiya', 'PRODUCTION', 1),
(4, 'Kamal Silva', '0771234004', '15 Lake Road, Boralesgamuwa', 'PRODUCTION', 1),
(5, 'Chaminda Fernando', '0771234005', '23 Chapel Lane, Nugegoda', 'PRODUCTION', 1),
(6, 'Nimal Rathnayake', '0771234006', '67 Kottawa Road, Kottawa', 'PRODUCTION', 1),
(7, 'Ruwan Wickrama', '0771234007', '09 Old Road, Maharagama', 'SALES', 1),
(8, 'Kasun Dissanayake', '0771234008', '34 Railway Ave, Maharagama', 'PACKING', 1),
(9, 'Nuwan Jayasinghe', '0771234009', '51 School Lane, Pannipitiya', 'PACKING', 1),
(10, 'Samantha Kumara', '0771234010', '19 Main Street, Homagama', 'DELIVERY', 1);

-- 2. User Accounts with BCrypt hashed passwords
INSERT INTO user_account (user_account_id, employee_id, username, password_hash, access_role, active) VALUES
(1, 1, 'admin', '$2a$10$KM6Vzl8XZd5HQQBGcsvw3uPtRR4sHcJBakfadVB5p1Cp.sLRIFBpO', 'OWNER', 1),
(2, 2, 'manager', '$2a$10$Y4NtKzwlt9vTQAt1c6akNOgViEBvNpJ5RQ.Al9g0WVU34ZxmJAyDa', 'MANAGER', 1),
(3, 3, 'production', '$2a$10$uUHakGUNuRjYXU4xQWE3ee7UX2f.R70wNgOFFmgKSkyegCD30niAm', 'PRODUCTION', 1),
(4, 7, 'sales', '$2a$10$Ef/0bnsx34wFzHfnvoT8o.mAr7X6sncTZl2mvT811XpbpxX6kORjC', 'SALES', 1),
(5, 8, 'packing', '$2a$10$JadpTMMxZBqC4bUOAwBT2uHeDKL6ovdTJl8iqgVFeskPL7BfM7xGi', 'PACKING', 1),
(6, 10, 'delivery', '$2a$10$QTuR5gBu/VjF3HiCecSEz.sjhmJIttRObLN41JCJZUru05td7FIWW', 'DELIVERY', 1);

-- 3. Products
INSERT INTO product (product_id, name, category, unit, selling_price, active) VALUES
(1, 'White Bread 450g', 'Bread', 'PIECE', 180.00, 1),
(2, 'Roast Bread', 'Bread', 'PIECE', 200.00, 1),
(3, 'Tea Bun', 'Buns', 'PIECE', 80.00, 1),
(4, 'Fish Bun (Malu Paan)', 'Short Eats', 'PIECE', 120.00, 1),
(5, 'Jam Bun (Jam Paan)', 'Buns', 'PIECE', 100.00, 1),
(6, 'Piyan Roll', 'Pastry', 'PIECE', 110.00, 1),
(7, 'Bread Crumbs 500g', 'By-Products', 'KG', 450.00, 1),
(8, 'Butter Cake 500g', 'Cakes', 'PIECE', 650.00, 1),
(9, 'Chocolate Ribbon Cake 1kg', 'Cakes', 'PIECE', 2200.00, 1);

-- 4. Customers
INSERT INTO customer (customer_id, name, phone, address) VALUES
(1, 'Anura Senanayake', '0771234567', '45 Galle Road, Colombo'),
(2, 'Malini Fonseka', '0719876543', '12 High Level Rd, Nugegoda'),
(3, 'Dinesh Weerasinghe', '0765544332', '89 Kandy Rd, Kiribathgoda');

-- 5. Ingredients
INSERT INTO ingredient (ingredient_id, name, unit, reorder_level, active) VALUES
(1, 'Wheat Flour', 'KG', 50.000, 1),
(2, 'White Sugar', 'KG', 20.000, 1),
(3, 'Instant Yeast', 'KG', 5.000, 1),
(4, 'Iodized Salt', 'KG', 10.000, 1),
(5, 'Bakery Margarine / Butter', 'KG', 15.000, 1),
(6, 'Vegetable Cooking Oil', 'L', 20.000, 1),
(7, 'Canned Fish Filling Mix', 'KG', 10.000, 1),
(8, 'Mixed Fruit Jam', 'KG', 10.000, 1),
(9, 'Baking Powder', 'KG', 3.000, 1),
(10, 'Cocoa Powder', 'KG', 5.000, 1);

-- 6. Shops (Wholesale distribution outlets)
INSERT INTO shop (shop_id, name, phone, address, active) VALUES
(1, 'Jayasiri Grocery', '0112854321', '102 Stanley Thilakarathne Mw, Nugegoda', 1),
(2, 'Lanka Supermart', '0112845678', '55 Old Road, Maharagama', 1),
(3, 'City Mart', '0112891234', '18 Highlevel Road, Pannipitiya', 1),
(4, 'Kottawa Fresh Corner', '0112789456', '34 Dharmapala Mawatha, Kottawa', 1);

-- 7. Vehicles (4 three-wheelers)
INSERT INTO vehicle (vehicle_id, registration_no, model, active) VALUES
(1, 'WP AB-1234', 'Bajaj RE 4-Stroke Green', 1),
(2, 'WP AB-5678', 'Bajaj Maxima Cargo Red', 1),
(3, 'WP BC-9012', 'Piaggio Ape City Blue', 1),
(4, 'WP BC-3456', 'Bajaj RE Compact Yellow', 1);

-- 8. Ingredient Purchases (stock initialization)
INSERT INTO ingredient_purchase (ingredient_purchase_id, ingredient_id, recorded_by_employee_id, purchased_at, qty, unit_cost, supplier_name) VALUES
(1, 1, 1, '2026-11-01 08:30:00', 500.000, 160.00, 'Serendib Flour Mills'),
(2, 2, 1, '2026-11-01 08:45:00', 100.000, 240.00, 'Ceylon Sugar Co.'),
(3, 3, 1, '2026-11-01 09:00:00', 25.000, 1200.00, 'Prima Ceylon'),
(4, 4, 1, '2026-11-01 09:15:00', 50.000, 90.00, 'Raigam Salt Mills'),
(5, 5, 1, '2026-11-01 09:30:00', 80.000, 850.00, 'MeadowLea Lanka'),
(6, 6, 1, '2026-11-01 09:45:00', 60.000, 620.00, 'Fortune Oils'),
(7, 7, 1, '2026-11-01 10:00:00', 30.000, 780.00, 'Ocean Foods'),
(8, 8, 1, '2026-11-01 10:15:00', 25.000, 480.00, 'MD Gampaha'),
(9, 9, 1, '2026-11-01 10:30:00', 10.000, 650.00, 'Edinborough Foods');

-- 9. Production Batches
INSERT INTO production_batch (production_batch_id, product_id, recorded_by_employee_id, produced_on, planned_qty, actual_qty, status) VALUES
(1, 1, 3, '2026-11-04', 300.000, 295.000, 'COMPLETED'),
(2, 2, 3, '2026-11-04', 150.000, 150.000, 'COMPLETED'),
(3, 3, 4, '2026-11-04', 200.000, 200.000, 'COMPLETED'),
(4, 4, 4, '2026-11-04', 150.000, 148.000, 'COMPLETED'),
(5, 1, 3, '2026-11-05', 350.000, 350.000, 'COMPLETED'),
(6, 4, 4, '2026-11-05', 180.000, 180.000, 'COMPLETED');

-- 10. Ingredient Usage for Batches
INSERT INTO ingredient_usage (ingredient_usage_id, production_batch_id, ingredient_id, recorded_by_employee_id, used_at, qty) VALUES
(1, 1, 1, 3, '2026-11-04 04:00:00', 120.000),
(2, 1, 2, 3, '2026-11-04 04:00:00', 8.000),
(3, 1, 3, 3, '2026-11-04 04:00:00', 2.500),
(4, 1, 4, 3, '2026-11-04 04:00:00', 2.000),
(5, 4, 1, 4, '2026-11-04 05:00:00', 30.000),
(6, 4, 7, 4, '2026-11-04 05:00:00', 12.000);

-- 11. Retail Sales at Bakery Outlet
INSERT INTO sale (sale_id, cashier_employee_id, sold_at, cash_received) VALUES
(1, 7, '2026-11-04 16:30:00', 1000.00),
(2, 7, '2026-11-05 10:15:00', 2000.00);

INSERT INTO sale_item (sale_item_id, sale_id, product_id, quantity, unit_price) VALUES
(1, 1, 1, 2.000, 180.00),
(2, 1, 4, 3.000, 120.00),
(3, 1, 3, 2.000, 80.00),
(4, 2, 1, 4.000, 180.00),
(5, 2, 2, 2.000, 200.00),
(6, 2, 4, 5.000, 120.00);

-- 12. Leftover Batches from Unsold Bread
INSERT INTO leftover_batch (leftover_batch_id, production_batch_id, recorded_by_employee_id, recorded_on, qty_pieces) VALUES
(1, 1, 3, '2026-11-04', 25);

-- 13. Bread Crumb Batch (Waste reduction reuse)
INSERT INTO bread_crumb_batch (bread_crumb_batch_id, output_product_id, recorded_by_employee_id, produced_at, output_qty_kg) VALUES
(1, 7, 3, '2026-11-05 07:30:00', 9.500);

INSERT INTO bread_crumb_input (bread_crumb_batch_id, leftover_batch_id, input_qty_pieces) VALUES
(1, 1, 20);

-- 14. Special Customer Orders (Wedding / Custom Cake)
INSERT INTO special_order (special_order_id, customer_id, recorded_by_employee_id, order_type, required_at, status, instructions, deposit_amount) VALUES
(1, 1, 2, 'WEDDING_CAKE', '2026-11-15 11:00:00', 'CONFIRMED', '3-tier white and gold floral wedding cake with butter icing', 15000.00),
(2, 2, 2, 'SPECIAL', '2026-11-08 15:00:00', 'PENDING', 'Birthday Chocolate drip cake with greeting: Happy 10th Birthday Kevin!', 2000.00);

INSERT INTO special_order_item (special_order_item_id, special_order_id, product_id, description, quantity, unit_price) VALUES
(1, 1, NULL, 'Tier 1 & 2 Rich Fruit Cake with almond paste & royal icing', 1.000, 35000.00),
(2, 1, NULL, 'Tier 3 Decorative Dummy with fresh flowers', 1.000, 10000.00),
(3, 2, 9, 'Chocolate Ribbon Cake 1kg with drip styling', 2.000, 2500.00);

-- 15. Deliveries to Wholesale Shops
INSERT INTO delivery (delivery_id, shop_id, vehicle_id, delivery_employee_id, packed_by_employee_id, delivery_date, status) VALUES
(1, 1, 1, 10, 8, '2026-11-04', 'DELIVERED'),
(2, 2, 2, 10, 8, '2026-11-05', 'DISPATCHED'),
(3, 3, 1, 10, 9, '2026-11-05', 'PACKED');

INSERT INTO delivery_item (delivery_item_id, delivery_id, product_id, packed_qty, delivered_qty) VALUES
(1, 1, 1, 80.000, 80.000),
(2, 1, 4, 40.000, 40.000),
(3, 2, 1, 100.000, 0.000),
(4, 2, 2, 50.000, 0.000),
(5, 3, 1, 60.000, 0.000);
