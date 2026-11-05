# Vishmi Bakehouse - proposal review and JavaFX UI kit

Reviewed source: Proposal-Vishmi-Bakehouse.pdf, dated 23 October 2026; requirements FR-01 through FR-13; original diagrams on pages 7 and 9.

## Deliverables

- 14 desktop UI screens, each 1366 x 900, in PNG and SVG.
- Corrected use case diagram: 15-corrected-use-case.png / .svg.
- Corrected relational ER diagram: 16-corrected-erd.png / .svg.
- 00-ui-overview.png: overview of all UI screens.
- SVG files are vector import sources, not native .fig files. Drag them into Figma. Font substitution or SVG importer behavior may require text recreation. The PNG versions are the visual references.
- The Figma file was created, but the Starter plan tool-call limit prevented adding the screens. No completed editable Figma canvas is claimed.
- These are visual specifications, not working JavaFX code or a functional prototype.

## What was wrong in the original use case diagram

1. Production Staff is connected to Record Sales, which does not match the role responsibilities in the proposal.
2. Packing Staff is connected to Manage Vehicles. The packing role should prepare and record shop quantities; vehicle administration belongs to Owner/Manager unless explicitly delegated.
3. Login is not consistently represented for all staff roles. Every account holder needs authentication.
4. Ingredient usage and packing tasks are not sufficiently explicit for FR-05 and the packing workflow.
5. Leftover reuse needs an authorized operating role, rather than only the owner connection.

The replacement uses role-specific views of one system so lines do not obscure the use cases. Owner/Manager also has all staff operations; operational use cases require an authenticated active account. Do not add an include relationship from every operation to Login: a session precondition is more suitable here.

## What was wrong or incomplete in the original ER diagram

| Original issue | Correction |
|---|---|
| Production records are absent | ProductionBatch stores product, date, planned and actual quantity, responsible employee |
| Leftover and bread-crumb entities are absent | LeftoverBatch, BreadCrumbBatch and BreadCrumbInput preserve input lots and output weight |
| IngredientPurchase is linked directly to IngredientUsage; ingredient reference is absent from the purchase | Both purchase and usage reference Ingredient independently; usage also references a production batch |
| Product ID on Ingredient implies one ingredient belongs to one product | Remove that dependency; actual usage links ingredients to product production batches |
| SaleItem lacks quantity and historical price | Add quantity and unit_price to line items |
| Delivery quantity alone cannot describe multiple products | Add DeliveryItem with product, packed_qty and delivered_qty |
| Static Product-Shop association does not record individual dispatches | Use Delivery and DeliveryItem, linked to shop and vehicle |
| Customer has a user reference despite public ordering being out of scope | Customer is an internal business record, without a login requirement |
| Special orders lack clear item structure | SpecialOrderItem supports custom descriptions and optional catalog product references |
| Plain password field and repeated roles are unclear | password_hash on UserAccount; employee job_role and account access_role have separate meanings |
| Cardinalities and ownership are difficult to follow | PK/FK attributes and explicit min/max relationship multiplicities |

This is a relational redesign suitable for MySQL, rather than a cosmetic redrawing of the original Chen-style diagram. In the ER image, shared Product and Employee cards represent the same tables, not duplicate tables.

## ER relationships and constraints

- Employee 1 -> UserAccount 0..1. employee_id is unique in UserAccount; every account references exactly one employee. An employee can exist without a system account.
- Employee 1 -> Sale 0..many through cashier_employee_id.
- Sale 1 -> SaleItem 1..many once a sale is completed. Each line references exactly one Product. A product can occur in zero or many sale lines.
- Customer 1 -> SpecialOrder 0..many. Each order belongs to exactly one customer and records the employee who entered it.
- SpecialOrder 1 -> SpecialOrderItem 1..many. An item optionally references one Product, allowing custom cakes without manufacturing a catalog product. Description is required for custom items.
- Ingredient 1 -> IngredientPurchase 0..many; Ingredient 1 -> IngredientUsage 0..many.
- Product 1 -> ProductionBatch 0..many; ProductionBatch 1 -> IngredientUsage 0..many.
- ProductionBatch 1 -> LeftoverBatch 0..many; each leftover lot refers to one production batch.
- BreadCrumbBatch 1 -> BreadCrumbInput 1..many for a completed reuse batch. LeftoverBatch 1 -> BreadCrumbInput 0..many. The pair (crumb_batch_id, leftover_id) is the composite primary key. A leftover lot can be used across several reuse batches.
- Product 1 -> BreadCrumbBatch 0..many via output_product_id. Select the bread-crumb product, whose stock unit is kg.
- Shop 1 -> Delivery 0..many; Vehicle 1 -> Delivery 0..many.
- Employee 1 -> Delivery 0..many, separately through delivery_employee_id and packed_by_employee_id. The image consolidates those two employee links to reduce clutter; both FKs are required in the logical model.
- Delivery 1 -> DeliveryItem 1..many when dispatched. Draft delivery headers can initially have no items. Product 1 -> DeliveryItem 0..many.
- Employee 1 -> purchases, batches, ingredient usage, special orders, leftover lots and crumb batches 0..many through recorded_by_employee_id. Several employee relationships are expressed by FK attributes/notes rather than extra crossing lines.
- Numeric 0..* multiplicity in the drawing means zero or many; the completed-record minimums above are application constraints enforced at save/dispatch time.

## Assumptions to confirm before implementation

1. Purchases are individual ingredient entries, not supplier invoices with multiple lines. If invoice-level processing is required, split into Purchase and PurchaseItem; supplier accounts are not in the proposal.
2. Packing and dispatch are stages of the same Delivery. Vehicle and staff assignment are required before dispatch, rather than during initial drafting.
3. The proposed access allocation gives authorized production staff leftover reuse entry. The owner should confirm this allocation with the bakery.
4. Bread-crumb inputs use pieces of bread; outputs use kg. Record actual output weight; do not assume a fixed conversion rate.
5. Only outlet sales are sales transactions in this scope. Shop credit, returns, payments and outstanding balances need additional requirements and tables if required.
6. A special-order deposit is a recorded amount, not a full payment ledger. Add OrderPayment if multiple deposits/refunds must be audited.
7. Bread-crumb stock comes from BreadCrumbBatch. Do not also record the same output in ProductionBatch, which would double-count it.
8. Finished-product availability must subtract outlet sales, dispatches, leftover allocations and special-order allocations as applicable. Ingredient stock alone is not sufficient to validate product availability. Decide whether special orders reserve catalog stock; custom cakes can be separate.
9. Users see only permitted navigation/actions; all mockups show the owner's full navigation. Enforce permissions in the service layer as well as the UI.

## Suggested permissions

| Operation | Owner/Manager | Production | Sales | Packing | Delivery |
|---|---|---|---|---|---|
| Login/logout | Yes | Yes | Yes | Yes | Yes |
| Products, ingredient master, purchases | Yes | Delegated only | No | No | No |
| Production and ingredient usage | Yes | Yes | No | No | No |
| Outlet sales and special orders | Yes | No | Yes | No | No |
| Record packing and prepare dispatch | Yes | No | No | Yes | No |
| View assigned delivery/update status | Yes | No | No | No | Yes |
| Manage shops/vehicles | Yes | No | No | No | No |
| Leftover and crumb reuse | Yes | Authorized only | No | No | No |
| Employees/accounts/reports | Yes | No | No | No | No |

## JavaFX implementation guide

| Visual element | JavaFX control/layout |
|---|---|
| App shell | BorderPane |
| Left navigation | VBox with Buttons |
| Title and user strip | HBox |
| Form | GridPane with Labels and inputs |
| Free text / money / quantity | TextField plus validation |
| Password | PasswordField |
| Product/employee/status selection | ComboBox |
| Date | DatePicker |
| Recent records and line items | TableView with TableColumn |
| Save/add/clear/dispatch | Button |
| Manage shops, vehicles, reset passwords | Dialog with GridPane forms |
| Error/success message | Alert or inline Label |
| Resizable content | AnchorPane / ScrollPane as needed |

Use a simple light stylesheet: green #276247, page #F4F6F3, white surfaces, border #DCE3DC, body text #28352F. Base text 14px; page titles 28px; fields/buttons about 38-40px high. Use available system fonts consistently. All examples contain fictional sample data.

## Essential validation

- Positive quantities; ingredient usage cannot exceed available ingredient stock.
- Preserve ingredient units; use DECIMAL for currency and fractional weights.
- Snapshot selling prices in sale/order line items rather than recalculating old records from current product prices.
- Save headers, items and stock-affecting records in a single JDBC database transaction.
- Store salted password hashes, never plain passwords. Use parameterized SQL.
- Do not subtract packed quantities and delivered quantities twice. Define one consistent stock reservation/dispatch policy.
- Bread-crumb input cannot exceed the remaining quantity of each leftover lot. Reject duplicate/double-consumption using transaction-safe checks.
- Prevent deleting products, employees or vehicles referenced by historical records; mark inactive instead.
- The proposal says ten employees but its activity counts add to ten only if the first production count is four; correct the typo 'for employees' to 'four employees'. It also mentions three delivery staff and four vehicles, which is possible and does not require four drivers.
