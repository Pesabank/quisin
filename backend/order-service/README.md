# Quisin Order Service

The Order Service is a crucial component of the Quisin restaurant management system, responsible for handling all order-related operations. It provides functionality for creating, managing, and tracking orders across different user roles (customers, waiters, kitchen staff).

## Features

### Order Management
- Create new orders (single and group orders)
- Update order status
- Cancel orders
- Track order lifecycle
- Manage order items
- Handle special instructions

### Waiter Assignment
- Request waiter assignment
- Assign waiters to orders
- Track waiter assignment status
- Manage waiter-order relationships

### Menu Integration
- Real-time menu item validation
- Price validation
- Restaurant ownership verification
- Item availability checking
- Redis caching for menu items

### Event Publishing
The service publishes order events to Kafka using Spring Cloud Stream:
- Order creation events
- Status update events
- Waiter assignment events

Event payload example:
```json
{
  "orderId": 1,
  "restaurantId": 1,
  "customerId": 1,
  "waiterId": 2,
  "status": "CONFIRMED",
  "totalAmount": 45.90,
  "items": [
    {
      "menuItemId": 1,
      "menuItemName": "Spring Rolls",
      "quantity": 2,
      "unitPrice": 10.95,
      "subtotal": 21.90,
      "status": "PENDING"
    }
  ]
}
```

### Caching
- Menu item caching using Redis
- Cache TTL: 1 hour
- Automatic cache invalidation
- Cache-aside pattern implementation

### Order Types
- DINE_IN: Orders for in-restaurant dining
- TAKEAWAY: Orders for pickup
- DELIVERY: Orders for delivery
- GROUP_ORDER: Collaborative orders for groups

### Order Status Tracking
- PENDING: Initial order state
- CONFIRMED: Order verified and accepted
- IN_PREPARATION: Being prepared in kitchen
- READY: Ready for pickup/delivery/serving
- DELIVERED: Successfully delivered to customer
- COMPLETED: Order fulfilled
- CANCELLED: Order cancelled
- REFUNDED: Payment refunded

## API Endpoints

### Order Creation and Management
```
POST /api/v1/orders
- Create new order
- Required fields: restaurantId, customerId, type, items
- Optional fields: tableId, specialInstructions, groupOrderId, isWaiterAssignmentRequested

GET /api/v1/orders/{orderId}
- Get order details
- Returns complete order information

PATCH /api/v1/orders/{orderId}/status
- Update order status
- Required field: status

POST /api/v1/orders/{orderId}/assign-waiter
- Assign waiter to order
- Required field: waiterId

DELETE /api/v1/orders/{orderId}
- Cancel order
```

### Order Queries
```
GET /api/v1/orders/customer/{customerId}
- Get customer's orders
- Supports pagination

GET /api/v1/orders/restaurant/{restaurantId}
- Get restaurant's orders
- Supports pagination

GET /api/v1/orders/waiter/{waiterId}
- Get waiter's assigned orders
- Supports pagination

GET /api/v1/orders/group/{groupOrderId}
- Get all orders in a group

GET /api/v1/orders/restaurant/{restaurantId}/by-status
- Get restaurant orders by status
- Supports multiple statuses

GET /api/v1/orders/waiter/{waiterId}/by-status
- Get waiter orders by status
- Supports multiple statuses

GET /api/v1/orders/restaurant/{restaurantId}/by-date
- Get restaurant orders by date range
- Required parameters: startDate, endDate
```

## Security

### Authentication
- JWT-based authentication
- Token validation
- Role-based access control

### Authorization
Endpoints are secured based on user roles:
- CUSTOMER: Can create, view, and cancel their own orders
- WAITER: Can manage orders assigned to them
- KITCHEN_STAFF: Can update order status
- ADMIN: Has full access to all endpoints

## Dependencies

### Core Dependencies
- Spring Boot 3.2.1
- Spring Security
- Spring Data JPA
- Spring Cloud (Netflix Eureka Client)
- Spring Cloud Stream
- PostgreSQL
- Redis (for caching)
- Lombok
- JWT for authentication

### Build and Runtime
- Java 17
- Maven
- Docker

## Configuration

### Application Properties
```yaml
server:
  port: 8085
  servlet:
    context-path: /api/v1

spring:
  application:
    name: order-service
  
  datasource:
    url: jdbc:postgresql://localhost:5432/quisin_order_db
    username: postgres
    password: postgres

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  redis:
    host: localhost
    port: 6379

  cloud:
    stream:
      bindings:
        orderEvents-out-0:
          destination: order-events
          content-type: application/json

jwt:
  secret: your-secret-key-here
  expiration: 86400000
```

## Setup Instructions

1. Prerequisites:
   - JDK 17
   - Maven
   - Docker
   - PostgreSQL
   - Redis

2. Database Setup:
```sql
CREATE DATABASE quisin_order_db;
```

3. Build and Run:
```bash
# Build the application
mvn clean package

# Run the application
mvn spring-boot:run

# Build Docker image
docker build -t quisin/order-service .

# Run Docker container
docker run -d --name order-service \
  -p 8085:8085 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/quisin_order_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -e JWT_SECRET=your-secret-key-here \
  quisin/order-service
```

## Testing

### Unit Tests
The service includes comprehensive unit tests for:
- Order Service implementation
- Menu validation logic
- Event publishing
- Caching behavior

Run tests with:
```bash
mvn test
```

### Integration Tests
Integration tests cover:
- Database operations
- Cache integration
- Event publishing
- External service communication

## Monitoring

### Health Checks
```
GET /actuator/health
```

### Metrics
```
GET /actuator/metrics
GET /actuator/prometheus
```

Available metrics:
- Order creation rate
- Order processing time
- Cache hit/miss ratio
- Event publishing success rate

## Error Handling

### Error Response Format
```json
{
  "message": "Error message describing what went wrong"
}
```

### Common Exceptions
- MenuItemNotFoundException
- MenuItemNotAvailableException
- RestaurantMenuItemMismatchException
- OrderNotFoundException
- InvalidOrderStatusException
- WaiterAssignmentException

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is proprietary and confidential. 