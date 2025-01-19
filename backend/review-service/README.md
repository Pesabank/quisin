# Review Service

A microservice responsible for managing restaurant reviews in the Quisin system.

## Features

### Core Functionality
- Create, update, and delete reviews
- Rating system (1-5 stars)
- Comment moderation
- Response management
- Status tracking
- Restaurant rating calculations

### Moderation System
- Manual moderation by moderators
- Auto-approval configuration
- Content validation
- Response management
- Forbidden words filtering
- Daily review limits

### Integration Features
- Restaurant Service integration
- Event publishing via Kafka
- Circuit breaker pattern
- Retry mechanisms
- Cross-service communication

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

### Security
- JWT authentication
- Role-based access control
- Input validation
- Content moderation

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

### Building
```bash
mvn clean package
```

### Running
```bash
# Start required infrastructure
docker-compose up -d postgres kafka

# Run the service
java -jar target/review-service.jar
```

## API Documentation

### Endpoints

#### Review Management
```
POST    /api/v1/reviews              Create review
PUT     /api/v1/reviews/{id}         Update review
GET     /api/v1/reviews/{id}         Get review
DELETE  /api/v1/reviews/{id}         Delete review
```

#### Restaurant Reviews
```
GET     /api/v1/reviews/restaurant/{restaurantId}          Get restaurant reviews
GET     /api/v1/reviews/restaurant/{restaurantId}/rating   Get average rating
```

#### User Reviews
```
GET     /api/v1/reviews/user         Get user's reviews
```

#### Moderation
```
PATCH   /api/v1/reviews/{id}/moderate    Moderate review
```

### Sample Requests

#### Create Review
```json
POST /api/v1/reviews
{
  "restaurantId": "123",
  "orderId": "order-456",
  "rating": 5,
  "comment": "Excellent food and service!"
}
```

#### Moderate Review
```json
PATCH /api/v1/reviews/123/moderate?status=APPROVED
{
  "response": "Thank you for your feedback!"
}
```

## Event System

### Published Events
- `REVIEW_CREATED`
- `REVIEW_UPDATED`
- `REVIEW_MODERATED`
- `REVIEW_DELETED`

### Event Format
```json
{
  "eventId": "uuid",
  "eventType": "REVIEW_CREATED",
  "reviewId": 123,
  "restaurantId": "rest-123",
  "userId": "user-123",
  "orderId": "order-456",
  "rating": 5,
  "status": "PENDING",
  "timestamp": "2024-01-10T15:30:00"
}
```

## Security

### Role-Based Access
- `USER`: Create, update, delete own reviews
- `MODERATOR`: Moderate reviews, manage responses
- `ADMIN`: Full access to all operations

### Validation Rules
- Rating must be between 1 and 5
- Comment length: 10-1000 characters
- Maximum reviews per user per day: 5
- Content moderation for forbidden words
- One review per order

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

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is proprietary and confidential. 