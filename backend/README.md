# Quisin Backend

A microservices-based backend implementation for the Quisin restaurant management system.

## Services

### 1. Order Service ✅
- Order Management
  * Single and group orders
  * Order status tracking
  * Order item management
  * Special instructions handling
- Waiter Assignment
  * Request and assign waiters
  * Track assignment status
  * Waiter-order relationship management
- Menu Integration
  * Real-time menu validation
  * Price validation
  * Item availability checking
- Event System
  * Spring Cloud Stream integration
  * Order event publishing
  * Real-time updates
- Security
  * JWT authentication
  * Role-based access control
  * Input validation

### 2. Auth Service ✅
- User Authentication
  * Secure registration and login
  * JWT token management
  * Password encryption
- Role Management
  * Role-based access control
  * Permission management
- Security Features
  * Rate limiting
  * Comprehensive logging
  * Token blacklisting

### 3. User Service ✅
- User Management
  * Profile management
  * Role assignment
  * Status tracking
- Staff Management
  * Staff profiles
  * Role management
  * Activity tracking
- Security
  * JWT validation
  * Role-based authorization
  * Secure data handling

### 4. Restaurant Service ✅
- Restaurant Management
  * CRUD operations
  * Location management
  * Operating hours
- Chain Management
  * Multi-restaurant chains
  * Chain-level settings
  * Restaurant grouping
- Multi-tenant Support
  * Data isolation
  * Owner verification
  * Access control

### 5. Menu Service ✅
- Menu Management
  * Item CRUD operations
  * Category management
  * Price management
- Inventory Integration
  * Stock tracking
  * Availability updates
  * Low stock alerts
- Image Management
  * Item images
  * Image optimization
  * Storage management

### 6. Payment Service ✅
- Payment Processing
  * Multiple payment methods
    - Credit/Debit Cards (Stripe)
    - Mobile Money (M-PESA)
    - Digital Wallets (PayPal)
    - Cash Payments
  * Transaction management
  * Refund handling
  * Status tracking
  * Event publishing

- Security Features
  * PCI compliance
  * Data encryption
  * Token handling
  * Input validation
  * Error masking

- Integration Points
  * Payment gateway integration
  * Webhook handling
  * Event publishing
  * Status callbacks

- Performance
  * High throughput (50+ concurrent users)
  * Low latency (< 1000ms)
  * 95% success rate
  * Scalable architecture

- Testing
  * Integration tests
  * Performance tests
  * Security tests
  * Gateway simulations
  * Error scenarios

### 7. Reservation Service ✅
- Reservation Management
  * CRUD operations for reservations
  * Status tracking (PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW)
  * Special requests handling
  * Party size validation
  * Operating hours validation

- Table Management
  * Real-time availability checking
  * Capacity validation
  * Concurrent booking prevention
  * Table assignment

- Integration Features
  * Restaurant Service integration for table management
  * Event publishing for system-wide notifications
  * Circuit breaker pattern for resilience
  * Retry mechanisms with fallback strategies

- Security
  * JWT authentication
  * Role-based access control
  * Input validation
  * Error handling

- Event System
  * Reservation events
  * Status updates
  * Integration events
  * Kafka messaging

- Performance
  * Circuit breaker configuration
  * Retry policies
  * Concurrent request handling
  * Database optimization

- Testing
  * Integration tests
  * Event publishing tests
  * Mock implementations
  * Circuit breaker testing

### 8. Review Service ✅
- Review Management
  * Create and manage reviews
  * Rating system
  * Comment moderation
  * Response handling
  * Status tracking (PENDING, APPROVED, REJECTED, FLAGGED, REMOVED, UPDATED)

- Moderation System
  * Manual moderation by moderators
  * Auto-approval configuration
  * Content validation
  * Response management
  * Forbidden words filtering

- Integration Features
  * Restaurant Service integration
  * Event publishing (Kafka)
  * Circuit breaker pattern
  * Retry mechanisms

- Security
  * JWT authentication
  * Role-based access control
  * Input validation
  * Content moderation

### Review Service Configuration
```yaml
server:
  port: 8091
spring:
  application:
    name: review-service
  datasource:
    url: jdbc:postgresql://localhost:5432/quisin_review_db
    username: ${REVIEW_DB_USER}
    password: ${REVIEW_DB_PASSWORD}

review:
  moderation:
    auto-approve: false
    min-rating: 1
    max-rating: 5
    min-comment-length: 10
    max-comment-length: 1000
    forbidden-words: ${FORBIDDEN_WORDS:}
    max-reviews-per-user-per-day: 5
```

### 9. Analytics Service ✅
- Analytics Management
  * Sales analytics
  * Performance metrics
  * Customer insights
  * Real-time processing
  * Historical analysis
  * Data aggregation

### Analytics Service Configuration
```yaml
server:
  port: 8092
spring:
  application:
    name: analytics-service
  datasource:
    url: jdbc:postgresql://localhost:5432/quisin_analytics_db
    username: ${ANALYTICS_DB_USER}
    password: ${ANALYTICS_DB_PASSWORD}

analytics:
  metrics:
    update-interval: 300000 # 5 minutes
    retention-days: 365
  alerts:
    enabled: true
    check-interval: 60000 # 1 minute
```

### Analytics Features
- Sales Analytics
  * Revenue tracking
  * Order statistics
  * Payment analysis
  * Category performance
  * Top sellers
  * Time-based analysis

- Performance Analytics
  * Preparation times
  * Delivery metrics
  * Satisfaction scores
  * Rating analysis
  * Resource utilization
  * Operational efficiency

- Customer Analytics
  * Segmentation
  * Retention analysis
  * Lifetime value
  * Visit patterns
  * Spending analysis
  * Preference tracking

### Integration Points
- Event Processing
  * Order events
  * Payment events
  * Reservation events
  * Review events

- Data Management
  * Real-time updates
  * Historical storage
  * Data aggregation
  * Cleanup routines

### 9. Document Service
- Document Management
  * Multi-format document storage (PNG, PDF, Excel)
  * Secure document access control
  * Document metadata management
  * Export functionality for admin/super admin
  * Integration with other services for document generation
  * Automatic cleanup of temporary files
  * Credentials document generation
    - Restaurant credentials
    - Staff credentials with auto-generated details
    - Secure PDF format with instructions

- Document Types
  * QR Code images (PNG)
  * QR Code documents (PDF)
  * Data exports (Excel)
  * System reports
  * Analytics exports
  * Credentials documents
    - Restaurant access credentials
    - Staff login credentials
    - Security instructions included

- Security Features
  * Role-based access control
  * Document ownership validation
  * Secure file storage
  * Access logging
  * Secure credential delivery

### Document Service Configuration
```yaml
server:
  port: 8094
spring:
  application:
    name: document-service

document:
  storage:
    type: file-system  # or 's3' for production
    base-path: /data/documents
    temp-path: /data/temp
  retention:
    temp-files: 24h
    exports: 7d
  formats:
    - PNG
    - PDF
    - XLSX
  security:
    credentials-expiry: 24h
    force-password-change: true
```

### Integration Points
- QR Code Service for code image storage
- Analytics Service for report generation
- Admin dashboard for data exports
- User Service for credentials generation
- Super admin functionality for system-wide exports

### API Endpoints
- `POST /api/v1/documents/generate` - Generate document
- `GET /api/v1/documents/{id}` - Retrieve document
- `GET /api/v1/documents/{id}/download` - Download document
- `DELETE /api/v1/documents/{id}` - Delete document
- `POST /api/v1/documents/export` - Generate and export data
- `GET /api/v1/documents/types` - List supported document types
- `GET /api/v1/documents/staff/credentials/{restaurantId}/{staffId}` - Generate staff credentials
- `GET /api/v1/documents/credentials/{restaurantId}` - Generate restaurant credentials

## Technical Stack

### Core Technologies
- Java 17
- Spring Boot 3.2.1
- Spring Cloud
- Spring Security
- Spring Data JPA

### Databases
- PostgreSQL (primary data store)
- Redis (caching)

### Messaging
- Spring Cloud Stream
- Kafka

### Security
- JWT authentication
- Role-based access control
- API Gateway security

### Build & Deployment
- Maven
- Docker
- Docker Compose

## Getting Started

### Prerequisites
1. Install required software:
   - JDK 17
   - Maven
   - Docker & Docker Compose
   - PostgreSQL
   - Redis

2. Clone the repository:
```bash
git clone https://github.com/yourusername/quisin.git
cd quisin/backend
```

3. Build all services:
```bash
mvn clean package
```

4. Start required infrastructure:
```bash
docker-compose up -d postgres redis kafka
```

5. Run the services:
```bash
docker-compose up -d
```

## Service Configuration

### Order Service
```yaml
server:
  port: 8085
spring:
  application:
    name: order-service
```

### Auth Service
```yaml
server:
  port: 8081
spring:
  application:
    name: auth-service
```

### User Service
```yaml
server:
  port: 8082
spring:
  application:
    name: user-service
```

### Restaurant Service
```yaml
server:
  port: 8083
spring:
  application:
    name: restaurant-service
```

### Menu Service
```yaml
server:
  port: 8084
spring:
  application:
    name: menu-service
```

### Payment Service
```yaml
server:
  port: 8089
spring:
  application:
    name: payment-service
  datasource:
    url: jdbc:postgresql://localhost:5432/quisin_payment_db
    username: ${PAYMENT_DB_USER}
    password: ${PAYMENT_DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: payment-service
      auto-offset-reset: earliest

payment:
  stripe:
    api-key: ${STRIPE_API_KEY}
    webhook-secret: ${STRIPE_WEBHOOK_SECRET}
  mpesa:
    consumer-key: ${MPESA_CONSUMER_KEY}
    consumer-secret: ${MPESA_CONSUMER_SECRET}
    passkey: ${MPESA_PASSKEY}
    business-shortcode: ${MPESA_SHORTCODE}
    callback-url: ${MPESA_CALLBACK_URL}
  paypal:
    client-id: ${PAYPAL_CLIENT_ID}
    client-secret: ${PAYPAL_CLIENT_SECRET}
    mode: sandbox # or 'live' for production

security:
  jwt:
    secret: ${JWT_SECRET}
    expiration: 86400000 # 24 hours

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    tags:
      application: payment-service
```

### Reservation Service
```yaml
server:
  port: 8090
spring:
  application:
    name: reservation-service
  datasource:
    url: jdbc:postgresql://localhost:5432/quisin_reservation_db
    username: ${RESERVATION_DB_USER}
    password: ${RESERVATION_DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

reservation:
  max-per-slot: 10
  min-notice-minutes: 30
  max-advance-days: 30
  slot-duration-minutes: 30
  operating-hours:
    start: "10:00"
    end: "22:00"
```

## API Documentation

Each service includes Swagger documentation available at:
```
http://localhost:{service-port}/swagger-ui.html
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is proprietary and confidential.

## Testing

To run the tests for all services:
```bash
mvn clean test
```

To run tests for a specific service:
```bash
cd payments-service
mvn clean test
```

### Performance Testing
The payment service includes comprehensive performance tests:
```bash
mvn test -Dtest=PaymentPerformanceTest
mvn test -Dtest=PaymentGatewayPerformanceTest
```

Performance test parameters can be configured in `application-test.yml`.

### 10. Hail Waiter Service
- Core Features
  * Real-time waiter hailing system
  * Smart waiter assignment
    - Load balancing (< 3 orders per waiter)
    - Order-based assignment
  * Rate limiting (5-minute cooldown)
  * Event-driven notifications
  * Sound alert system

- Waiter Management
  * Availability tracking
  * Order load monitoring
  * Assignment history
  * Performance metrics

- Event System
  * Waiter hail events
  * Order assignment events
  * Status updates
  * Sound notifications

### Hail Waiter Service Configuration
```yaml
server:
  port: 8095
spring:
  application:
    name: hail-waiter-service

hail-waiter:
  cooldown:
    duration: 300000  # 5 minutes in milliseconds
  assignment:
    max-orders: 3
  notification:
    sound:
      enabled: true
      debounce: 2000  # 2 seconds
```

### Integration Points
- Order Service for order status
- User Service for waiter management
- Notification Service for alerts
- WebSocket for real-time updates

### API Endpoints
- `POST /api/v1/hail-waiter/request/{tableId}` - Request waiter
- `GET /api/v1/hail-waiter/status/{tableId}` - Check request status
- `GET /api/v1/hail-waiter/waiters/available` - List available waiters
- `GET /api/v1/hail-waiter/history/{tableId}` - Get hail history

### 11. Rating Service
- Core Features
  * 5-star rating system
  * Multi-category ratings
    - Restaurant rating
    - Food rating
    - Service rating
    - Waiter rating
  * Review integration
  * Analytics support

- Rating Management
  * Rating collection
  * Score calculation
  * Review association
  * Analytics generation

- Integration Features
  * Review Service connection
  * Analytics aggregation
  * Admin dashboard integration
  * Export capabilities

### Rating Service Configuration
```yaml
server:
  port: 8096
spring:
  application:
    name: rating-service

rating:
  categories:
    - RESTAURANT
    - FOOD
    - SERVICE
    - WAITER
  scale:
    min: 1
    max: 5
  analytics:
    update-interval: 300000  # 5 minutes
```

### Integration Points
- Review Service for comments
- Analytics Service for metrics
- Admin dashboard for reporting
- Export service for data extraction

### API Endpoints
- `POST /api/v1/ratings/submit` - Submit new rating
- `GET /api/v1/ratings/{type}/{id}` - Get ratings by type
- `GET /api/v1/ratings/average/{type}/{id}` - Get average rating
- `GET /api/v1/ratings/analytics/{restaurantId}` - Get rating analytics
