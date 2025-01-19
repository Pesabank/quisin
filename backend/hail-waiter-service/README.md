# Hail Waiter Service

## Overview
The Hail Waiter Service manages real-time waiter requests and assignments in the Quisin restaurant management system. It provides a sophisticated system for customers to request waiter assistance and ensures efficient distribution of service requests among available waiters.

### Core Features
- Real-time waiter hailing system
- Smart waiter assignment with load balancing
- Rate limiting (5-minute cooldown per table)
- Event-driven notifications with sound alerts
- Waiter availability tracking
- Request history management

### Technical Details
- Built with Spring Boot and Kotlin
- WebSocket integration for real-time updates
- Event-driven architecture using Kafka
- JPA for data persistence

## Configuration

```yaml
server:
  port: 8095
spring:
  application:
    name: hail-waiter-service
  datasource:
    url: jdbc:postgresql://localhost:5432/quisin_hailwaiter_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

hail-waiter:
  cooldown:
    duration: 300000  # 5 minutes in milliseconds
  assignment:
    max-orders: 3     # Maximum concurrent orders per waiter
  notification:
    sound:
      enabled: true
      debounce: 2000  # 2 seconds
```

## API Endpoints

### Waiter Requests
- `POST /api/v1/hail-waiter/request/{tableId}`
  * Request waiter assistance
  * Rate limited to one request per 5 minutes
  * Body:
    ```json
    {
      "userId": "uuid",
      "restaurantId": "uuid",
      "reason": "MENU_ASSISTANCE|ORDER_PLACEMENT|ADDITIONAL_SERVICE|BILL_REQUEST|GENERAL_INQUIRY",
      "additionalDetails": "string (optional)"
    }
    ```

- `GET /api/v1/hail-waiter/status/{tableId}`
  * Check request status
  * Returns current request status and assigned waiter

- `GET /api/v1/hail-waiter/waiters/available`
  * List available waiters
  * Returns waiters with less than 3 active orders

- `GET /api/v1/hail-waiter/history/{tableId}`
  * Get request history for a table
  * Includes all past requests and their resolutions

### Waiter Management
- `PUT /api/v1/hail-waiter/request/{requestId}`
  * Update request status
  * Body:
    ```json
    {
      "status": "PENDING|IN_PROGRESS|RESOLVED|CANCELLED",
      "assignedWaiterId": "uuid (optional)"
    }
    ```

## Event System

### Published Events
1. `waiter_hail`
   - Triggered when customer requests assistance
   - Payload includes table number and timestamp
   - Triggers distinct sound notification

2. `order_assigned`
   - Triggered when order is ready for delivery
   - Payload includes order and table details
   - Triggers unique sound notification

### Consumed Events
1. `order_status`
   - Updates waiter assignment based on order status
   - Used for load balancing

2. `waiter_status`
   - Updates waiter availability
   - Affects assignment decisions

## Integration Points
- Order Service
  * Order status updates
  * Waiter assignments
- User Service
  * Waiter management
  * Authentication
- Notification Service
  * Sound alerts
  * Status updates
- WebSocket Service
  * Real-time updates
  * Live status changes

## Rate Limiting
- 5-minute cooldown per table for hail requests
- Debounced sound notifications (2-second interval)
- Maximum 3 concurrent orders per waiter

## Error Handling
- Rate limit exceeded
- No waiters available
- Invalid request status
- Unauthorized access

## Security
- JWT authentication required for all endpoints
- Role-based access control
  * Customers can create requests
  * Waiters can update assigned requests
  * Managers can view all requests and override assignments

## Monitoring
- Request success rate
- Average response time
- Waiter utilization
- Request distribution
- Rate limit hits

## Getting Started

### Prerequisites
1. Java 17
2. PostgreSQL
3. Kafka
4. Redis (for rate limiting)

### Installation
1. Clone the repository
2. Configure application.yml
3. Run database migrations
4. Start the service:
```bash
./mvnw spring-boot:run
```

### Docker Support
```bash
# Build image
docker build -t quisin/hail-waiter-service .

# Run container
docker run -p 8095:8095 quisin/hail-waiter-service
``` 