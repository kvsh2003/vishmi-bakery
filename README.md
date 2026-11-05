# Vishmi Bakehouse Management System

**Academic Coursework:** Higher Diploma in Software Engineering (HDSE 77) - Institute of Software Engineering (IJSE)  
**Module:** ITS1066 - Professional Software Project in IT  
**Student:** P.A. Naveesha Niyumal (ID: 252751007)  
**Evaluation:** 50% Practical Implementation + 50% Viva Examination  

---

## 1. Project Overview & Business Scope

**Vishmi Bakehouse** is a comprehensive standalone desktop software application designed to computerize and streamline daily bakery operations:
- **Raw Material & Inventory:** Ingredient purchases, reorder thresholds, live available stock tracking (`v_ingredient_stock`), and batch usage.
- **Daily Production:** Batch planning, baking status workflow (`PLANNED` -> `IN_PROGRESS` -> `COMPLETED`), and finished product yields.
- **Retail Outlet Point of Sale (POS):** Fast cashier counter sales with live product search, receipt generation, and change calculation.
- **Shop Distribution & Logistics:** Three-wheeler fleet allocation, delivery packing, and status tracking (`PACKED` -> `DISPATCHED` -> `DELIVERED`).
- **Special Custom Orders:** Wedding cakes, tiered celebration cakes, customer instructions, advance deposit logging, and balance tracking.
- **Waste Reduction & By-Products:** Repurposing unsold leftover bread pieces into packaged bread crumbs with strict lot validation to prevent double-consumption.
- **Staff & Security:** Role-based access control (RBAC) with BCrypt salted password hashing.
- **Business Intelligence & Reporting:** Dynamic JasperReports summaries (Daily Sales, Low Stock Alerts, Deliveries, Production & Leftovers) with instant PDF export.

---

## 2. Technology Stack & Enterprise Architecture

- **Language:** Java 21 (OpenJDK)
- **GUI Toolkit:** JavaFX 21 + FXML (Scene Builder compatible)
- **Database Engine:** MySQL 8.0+ / 9.x
- **Database Connectivity:** JDBC via `mysql-connector-j` with Singleton Connection Pool
- **Reporting Engine:** JasperReports 6.21+ / OpenPDF
- **Security:** BCrypt salted cryptographic hashing (`org.mindrot:jbcrypt`)
- **Testing:** JUnit 5 (Unit tests & Transaction integrity rollback tests)
- **Build Tool:** Apache Maven 3.9+ with included `./mvnw` wrapper

### Layered Architecture (IJSE Standard)
```
View (FXML + CSS) ──> Controller ──> BO (Service Layer) ──> DAO (Data Access) ──> DBConnection ──> MySQL
       ^                   │                   │                    │
       │                   ▼                   ▼                    ▼
       └─────────────── DTOs ───────────── Entities ────────── SQL PreparedStatement
```

---

## 3. Database Architecture (19 Base Tables + 7 Views)

1. `employee` - Staff records (10 bakery employees across roles)
2. `user_account` - Staff login accounts with BCrypt password hashes
3. `product` - Bakery product catalog with pricing and unit types (PIECE, KG)
4. `customer` - Retail & custom cake customers
5. `ingredient` - Raw material master with reorder alert levels
6. `shop` - Wholesale distribution shop clients
7. `vehicle` - Bakery delivery three-wheeler fleet
8. `ingredient_purchase` - Raw material purchase invoices
9. `production_batch` - Daily baking batch schedules
10. `ingredient_usage` - Raw ingredients consumed per batch
11. `sale` - Retail outlet sale transactions
12. `sale_item` - Sale line items with snapshot unit prices
13. `special_order` - Custom cake & wedding cake orders
14. `special_order_item` - Itemized cake specifications
15. `leftover_batch` - Unsold leftover bread lots
16. `bread_crumb_batch` - Waste reduction bread crumb production
17. `bread_crumb_input` - Leftover lot consumption links (composite PK)
18. `delivery` - Wholesale dispatch headers
19. `delivery_item` - Products packed and delivered per shop

### Analytical Views
- `v_ingredient_stock`: Purchased minus used quantities; flags `low_stock`.
- `v_product_availability`: Live net stock subtracting outlet sales, shop allocations, and leftover lots.
- `v_leftover_balance`: Unconsumed leftover pieces available for bread-crumb reuse.
- `v_daily_sales`: Daily transaction count and revenue totals.
- `v_delivery_summary`: Dispatch and delivered quantities per shop and three-wheeler.
- `v_sale_totals`: Total sales amounts and change returned.
- `v_special_order_totals`: Total order value, deposit, and remaining balance.

---

## 4. Default Seed Accounts (For Evaluation & Viva)

| Username | Password | Role | Permissions |
|---|---|---|---|
| `admin` | `admin123` | **OWNER** | Full system access to all 14 screens & reports |
| `manager` | `manager123` | **MANAGER** | Full system access |
| `production` | `prod123` | **PRODUCTION** | Products, Ingredients, Purchases, Production, Usage, Leftovers |
| `sales` | `sales123` | **SALES** | Outlet Sales (POS), Special Orders, Dashboard |
| `packing` | `pack123` | **PACKING** | Shop Packing & Delivery Dispatch, Dashboard |
| `delivery` | `del123` | **DELIVERY** | Delivery Routes & Status confirmation, Dashboard |

---

## 5. How to Build, Test, and Run

### Run Automated Tests
```bash
./mvnw test
```

### Launch JavaFX Desktop Application
```bash
./mvnw clean javafx:run
```

---

## 6. Dynamic JasperReports

Templates located in `src/main/resources/reports/`:
1. `daily_sales_report.jrxml` - Daily revenue and transaction volume
2. `ingredient_stock_report.jrxml` - Raw material inventory & low stock warning alerts
3. `delivery_summary_report.jrxml` - Shop deliveries and three-wheeler logistics checklist
4. `production_summary_report.jrxml` - Finished goods production and leftover conversion
