# E-Commerce Cart & Checkout Module

A production-ready REST API built with **Java Spring Boot** for managing cart operations, checkout workflow, order creation, inventory validation, and payment simulation.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.0 |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL 8 |
| Boilerplate | Lombok |
| Validation | Jakarta Validation (Bean Validation 3.0) |
| Logging | SLF4J + Logback |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Auditing | Spring Data Auditing (`@CreatedDate`, `@LastModifiedDate`) |
| Testing | JUnit 5 + Mockito |
| Build Tool | Maven 3.8+ |

---

## Project Structure

```
src/
├── main/
│   ├── java/com/ecommerce/
│   │   ├── EcommerceApplication.java
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   ├── Product.java
│   │   │   ├── Cart.java
│   │   │   ├── CartItem.java
│   │   │   ├── Order.java
│   │   │   ├── OrderItem.java
│   │   │   └── Coupon.java
│   │   ├── enums/
│   │   │   ├── OrderStatus.java
│   │   │   └── DiscountType.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── ProductRepository.java
│   │   │   ├── CartRepository.java
│   │   │   ├── CartItemRepository.java
│   │   │   ├── OrderRepository.java
│   │   │   ├── OrderItemRepository.java
│   │   │   └── CouponRepository.java
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   │   ├── CreateUserRequest.java
│   │   │   │   ├── CreateProductRequest.java
│   │   │   │   ├── AddToCartRequest.java
│   │   │   │   ├── UpdateCartRequest.java
│   │   │   │   ├── CheckoutRequest.java
│   │   │   │   └── CreateCouponRequest.java
│   │   │   └── response/
│   │   │       ├── UserResponse.java
│   │   │       ├── ProductResponse.java
│   │   │       ├── CartResponse.java
│   │   │       ├── CartItemResponse.java
│   │   │       ├── OrderResponse.java
│   │   │       └── OrderItemResponse.java
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   ├── ProductService.java
│   │   │   ├── CartService.java
│   │   │   ├── OrderService.java
│   │   │   └── CouponService.java
│   │   ├── controller/
│   │   │   ├── UserController.java
│   │   │   ├── ProductController.java
│   │   │   ├── CartController.java
│   │   │   ├── OrderController.java
│   │   │   └── CouponController.java
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java
│   │       ├── ResourceNotFoundException.java
│   │       ├── InsufficientStockException.java
│   │       ├── InvalidCouponException.java
│   │       ├── CartEmptyException.java
│   │       └── DuplicateResourceException.java
│   └── resources/
│       ├── application.properties
│       └── schema.sql
└── test/
    └── java/com/ecommerce/service/
        └── CheckoutServiceTest.java
```

---

## Setup & Run

### 1. Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8 running locally

### 2. Clone / Create the Project
Create the project in IntelliJ IDEA or VS Code and place all files following the structure above.

### 3. Configure Database
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db?createDatabaseIfNotExist=true
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### 4. Run the Application
```bash
mvn spring-boot:run
```
Server starts at: `http://localhost:8080`

### 5. Seed Sample Data (Optional)
Run `src/main/resources/schema.sql` in MySQL Workbench or CLI.
It inserts 2 sample users, 5 products, and 3 coupons (`SAVE10`, `FLAT500`, `WELCOME20`).

### 6. Run Unit Tests
```bash
mvn test
```

---

## API Reference

### Users — `/api/users`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users` | Create a new user |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users` | Get all users |

### Products — `/api/products`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/products` | Create a new product |
| GET | `/api/products/{id}` | Get product by ID |
| GET | `/api/products?page=0&size=10&sortBy=id` | All products (paginated) |
| GET | `/api/products/category/{category}` | Products by category |
| PATCH | `/api/products/{id}/stock?quantity=50` | Update stock quantity |

### Cart — `/api/cart`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/cart/{userId}/add` | Add item to cart |
| PUT | `/api/cart/{userId}/item/{cartItemId}` | Update item quantity |
| DELETE | `/api/cart/{userId}/item/{cartItemId}` | Remove item from cart |
| GET | `/api/cart/{userId}` | View cart with total |
| DELETE | `/api/cart/{userId}/clear` | Clear entire cart |

### Orders & Checkout — `/api/orders`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders/{userId}/checkout` | Checkout (coupon optional) |
| GET | `/api/orders/{orderId}` | Get order by ID |
| GET | `/api/orders/user/{userId}?page=0&size=10` | Order history (paginated) |
| GET | `/api/orders/user/{userId}/status?status=SUCCESS` | Filter orders by status |

### Coupons — `/api/coupons`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/coupons` | Create a coupon |
| GET | `/api/coupons` | List all coupons |
| DELETE | `/api/coupons/{id}/deactivate` | Deactivate a coupon |

---

## Sample Requests

### Create User
```json
POST /api/users
Content-Type: application/json

{
  "name": "Alice Smith",
  "email": "alice@example.com"
}
```

### Create Product
```json
POST /api/products
Content-Type: application/json

{
  "name": "Laptop Pro",
  "price": 50000.00,
  "stockQuantity": 10,
  "description": "15-inch laptop with 16GB RAM",
  "category": "Electronics"
}
```

### Add to Cart
```json
POST /api/cart/1/add
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

### Update Cart Item
```json
PUT /api/cart/1/item/1
Content-Type: application/json

{
  "quantity": 3
}
```

### Create Coupon
```json
POST /api/coupons
Content-Type: application/json

{
  "code": "SAVE10",
  "discountType": "PERCENTAGE",
  "discountValue": 10,
  "expiryDate": "2026-12-31"
}
```

### Checkout with Coupon
```json
POST /api/orders/1/checkout
Content-Type: application/json

{
  "couponCode": "SAVE10"
}
```

### Checkout without Coupon
```json
POST /api/orders/1/checkout
Content-Type: application/json

{}
```

---

## Checkout Flow

The entire checkout process runs inside a single `@Transactional` method. If any step fails, the full transaction is rolled back automatically.

```
1. Validate user exists
        ↓
2. Fetch cart → validate not empty
        ↓
3. Validate stock for ALL items (before touching anything)
        ↓
4. Calculate total amount = Σ (price × quantity)
        ↓
5. Apply coupon (if provided)
   → Check coupon exists and is active
   → Check expiry date
   → PERCENTAGE: discount = total × value / 100
   → FLAT: discount = min(value, total)
        ↓
6. Create Order (status = PENDING)
        ↓
7. Create OrderItems (price snapshot from product at checkout time)
        ↓
8. Simulate payment (70% SUCCESS / 30% FAILED)
        ↓
   ┌────────────────────┬──────────────────────┐
   │     SUCCESS        │       FAILED         │
   │ Decrease inventory │ Order status = FAILED│
   │ Order status =     │ No inventory change  │
   │   SUCCESS          │ @Transactional       │
   │ Clear cart         │ handles rollback     │
   └────────────────────┴──────────────────────┘
```

---

## Logging (SLF4J + Logback)

All service classes use `LoggerFactory.getLogger()` for structured logging.

Log levels used across the application:

| Level | Usage |
|-------|-------|
| `INFO` | Request received, order created, payment result |
| `DEBUG` | Cart item operations, coupon validation steps |
| `WARN` | Stock running low, coupon near expiry |
| `ERROR` | Payment failure, unexpected exceptions |

Log format configured in `logback-spring.xml`:
```
[timestamp] [LEVEL] [thread] ClassName - message
```

Logs are written to both console and a rolling file at `logs/ecommerce.log` (daily rotation, 30-day retention).

---

## Swagger / OpenAPI Docs

Once the application is running, access the interactive API documentation at:

```
http://localhost:8080/swagger-ui/index.html
```

The raw OpenAPI JSON spec is available at:
```
http://localhost:8080/v3/api-docs
```

Dependency added in `pom.xml`:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

All controllers are annotated with `@Tag`, all endpoints with `@Operation` and `@ApiResponse` for full documentation coverage.

---

## Spring Data Auditing

Entities that extend `BaseEntity` automatically track creation and modification timestamps.

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

Auditing is enabled in the main application class:
```java
@SpringBootApplication
@EnableJpaAuditing
public class EcommerceApplication { ... }
```

Entities with auditing: `User`, `Product`, `Order`, `Coupon`

---

## Exception Handling

All exceptions are handled globally via `@RestControllerAdvice`.

| Exception | HTTP Status | Scenario |
|-----------|-------------|---------|
| `ResourceNotFoundException` | 404 Not Found | User / Product / Order / Cart not found |
| `InsufficientStockException` | 409 Conflict | Cart item quantity exceeds available stock |
| `InvalidCouponException` | 400 Bad Request | Coupon not found, inactive, or expired |
| `CartEmptyException` | 400 Bad Request | Checkout attempted on empty cart |
| `DuplicateResourceException` | 409 Conflict | Duplicate email or coupon code |
| `MethodArgumentNotValidException` | 400 Bad Request | Bean validation failure on request body |
| `Exception` (fallback) | 500 Internal Server Error | Any unhandled exception |

Error response format:
```json
{
  "timestamp": "2026-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 99"
}
```

---

## Database Schema

**Tables:** `users`, `products`, `carts`, `cart_items`, `orders`, `order_items`, `coupons`

**Key design decisions:**
- `cart_items.quantity` tracks how many of a product are in a cart
- `order_items.price` is a snapshot of the product price at checkout time — immune to future price changes
- `coupons` are applied by code lookup at checkout — no FK constraint on orders
- `orders.discount_amount` and `orders.final_amount` are stored explicitly for order history accuracy

---

## Unit Test Coverage

File: `src/test/java/com/ecommerce/service/CheckoutServiceTest.java`

| Test | Scenario |
|------|---------|
| `checkout_UserNotFound_ThrowsException` | Invalid user ID |
| `checkout_CartNotFound_ThrowsException` | No cart for user |
| `checkout_CartEmpty_ThrowsException` | Empty cart on checkout |
| `checkout_InsufficientStock_ThrowsException` | Stock less than requested qty |
| `checkout_InvalidCoupon_ThrowsException` | Non-existent coupon code |
| `checkout_ExpiredCoupon_ThrowsException` | Coupon past expiry date |
| `checkout_TotalAmountCalculation_IsCorrect` | Correct total = price × qty |
| `checkout_PercentageCoupon_DiscountAppliedCorrectly` | 10% off applied correctly |
| `checkout_FlatCoupon_DiscountAppliedCorrectly` | Flat ₹500 off applied correctly |
| `getOrdersByUser_ReturnsPagedOrders` | Paginated order history |

---

## Order Status Values

| Status | Description |
|--------|-------------|
| `PENDING` | Order created, payment being processed |
| `SUCCESS` | Payment successful, inventory deducted |
| `FAILED` | Payment simulation failed |
| `CANCELLED` | Order manually cancelled |

## Coupon Discount Types

| Type | Example | Effect |
|------|---------|--------|
| `PERCENTAGE` | `SAVE10` → 10% | Deducts 10% of total |
| `FLAT` | `FLAT500` → ₹500 | Deducts flat ₹500 from total |
