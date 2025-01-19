# Reservation Service

A microservice responsible for managing restaurant reservations in the Quisin system.

## Features

### Core Functionality
- Create, update, and cancel reservations
- Track reservation status (PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW)
- Manage table availability
- Handle special requests
- Support for party size validation
- Operating hours validation

### Integration Points
- Restaurant Service integration for table management
- Event publishing for system-wide notifications
- Support for distributed transactions
- Circuit breaker pattern for resilience

## Technical Stack

### Core Technologies
- Java 17
- Spring Boot 3.2.1
- Spring Cloud
- Spring Data JPA
- PostgreSQL

### Messaging & Events
- Spring Cloud Stream
- Apache Kafka
- Event-driven architecture

### Resilience
- Resilience4j Circuit Breaker
- Retry mechanisms
- Fallback strategies

### Testing
- JUnit 5
- Embedded Kafka
- H2 Database
- Spring Boot Test

## Getting Started

### Prerequisites
1. JDK 17
2. Maven
3. Docker & Docker Compose
4. PostgreSQL
5. Kafka

### Configuration
The service can be configured through `application.yml`:

```yaml
spring:
  application:
    name: reservation-service
  datasource:
    url: jdbc:postgresql://localhost:5432/quisin_reservation_db
    username: ${RESERVATION_DB_USER}
    password: ${RESERVATION_DB_PASSWORD}

reservation:
  min-notice-minutes: 30
  max-per-slot: 10
  slot-duration-minutes: 30
  max-advance-days: 30
  operating-hours:
    start: "10:00"
    end: "22:00"
```

### Building
```bash
mvn clean package
```

### Running
```bash
# Start required infrastructure
docker-compose up -d postgres kafka

# Run the service
java -jar target/reservation-service.jar
```

## API Documentation

### Endpoints

#### Reservation Management
```
POST    /api/v1/reservations              Create reservation
PUT     /api/v1/reservations/{id}         Update reservation
GET     /api/v1/reservations/{id}         Get reservation
DELETE  /api/v1/reservations/{id}         Cancel reservation
PATCH   /api/v1/reservations/{id}/status  Update status
```

#### User Reservations
```
GET     /api/v1/reservations/user         Get user's reservations
```

#### Restaurant Reservations
```
GET     /api/v1/reservations/restaurant/{restaurantId}                Get all reservations
GET     /api/v1/reservations/restaurant/{restaurantId}/status/{status}  Get by status
GET     /api/v1/reservations/restaurant/{restaurantId}/time-range    Get by time range
```

#### Table Management
```
GET     /api/v1/reservations/restaurant/{restaurantId}/table/{tableId}/availability  Check table availability
GET     /api/v1/reservations/restaurant/{restaurantId}/available-tables             Get available tables
GET     /api/v1/reservations/restaurant/{restaurantId}/active-count                 Get active reservations count
```

### Sample Requests

#### Create Reservation
```json
POST /api/v1/reservations
{
  "restaurantId": "123",
  "tableId": "table-1",
  "partySize": 4,
  "reservationTime": "2024-01-20T19:00:00",
  "specialRequests": "Window seat preferred"
}
```

#### Update Status
```json
PATCH /api/v1/reservations/123/status?status=CONFIRMED
```

## Event System

### Published Events
- `RESERVATION_CREATED`
- `RESERVATION_UPDATED`
- `RESERVATION_CANCELLED`
- `RESERVATION_STATUS_CHANGED`

### Event Format
```json
{
  "eventId": "uuid",
  "eventType": "RESERVATION_CREATED",
  "reservationId": 123,
  "restaurantId": "rest-123",
  "userId": "user-123",
  "reservationTime": "2024-01-20T19:00:00",
  "partySize": 4,
  "tableId": "table-1",
  "status": "PENDING",
  "timestamp": "2024-01-10T15:30:00"
}
```

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify -P integration-test
```

### Test Coverage
```bash
mvn clean verify jacoco:report
```

## Monitoring

### Health Check
```
GET /actuator/health
```

### Metrics
```
GET /actuator/metrics
GET /actuator/prometheus
```

## Circuit Breaker Configuration

```yaml
resilience4j:
  circuitbreaker:
    instances:
      restaurantService:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 5
        minimumNumberOfCalls: 5
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is proprietary and confidential. 