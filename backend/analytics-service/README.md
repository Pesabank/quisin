# Analytics Service

A microservice responsible for collecting, processing, and providing analytics data for the Quisin restaurant management system.

## Features

### Core Analytics
- Sales Analytics
  * Revenue tracking
  * Order statistics
  * Payment method analysis
  * Category-wise sales
  * Top-selling items
  * Hourly sales distribution

- Performance Analytics
  * Order preparation times
  * Delivery metrics
  * Customer satisfaction scores
  * Rating distributions
  * Table utilization rates
  * Reservation statistics

- Customer Analytics
  * Customer segmentation
  * Retention rates
  * Customer lifetime value
  * Visit frequency
  * Spending patterns
  * Customer preferences

### Integration Features
- Event-driven architecture
- Real-time analytics updates
- Historical data analysis
- Data aggregation
- Scheduled analytics generation
- Automated data cleanup

## Technical Stack

### Core Technologies
- Java 17
- Spring Boot 3.2.1
- Spring Cloud
- Spring Data JPA
- PostgreSQL

### Event Processing
- Spring Cloud Stream
- Apache Kafka
- Event-driven architecture

### Caching & Performance
- Spring Cache
- Redis (optional)
- Query optimization
- Data aggregation

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

### Building
```bash
mvn clean package
```

### Running
```bash
# Start required infrastructure
docker-compose up -d postgres kafka

# Run the service
java -jar target/analytics-service.jar
```

## API Documentation

### Endpoints

#### Analytics Retrieval
```
POST    /api/v1/analytics              Get analytics data
GET     /api/v1/analytics/{id}/history Get historical data
POST    /api/v1/analytics/{id}/generate Generate analytics
```

### Sample Requests

#### Get Analytics
```json
POST /api/v1/analytics
{
  "restaurantId": "123",
  "period": "DAILY",
  "startDate": "2024-01-01T00:00:00",
  "endDate": "2024-01-31T23:59:59",
  "includeSales": true,
  "includePerformance": true,
  "includeCustomer": true
}
```

#### Get Historical Data
```
GET /api/v1/analytics/123/history?period=MONTHLY&startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59
```

## Event System

### Consumed Events
- `ORDER_CREATED`, `ORDER_UPDATED`, `ORDER_COMPLETED`
- `PAYMENT_COMPLETED`, `PAYMENT_FAILED`
- `RESERVATION_CREATED`, `RESERVATION_CANCELLED`
- `REVIEW_CREATED`, `REVIEW_UPDATED`

### Event Processing
The service processes events from various sources:
- Order events for sales analytics
- Payment events for revenue analytics
- Reservation events for performance metrics
- Review events for satisfaction scores

## Data Management

### Data Retention
- Configurable retention period
- Automated cleanup of old data
- Data aggregation for historical analysis

### Caching Strategy
- In-memory caching for frequent queries
- Configurable cache TTL
- Cache invalidation on updates

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

### Available Metrics
- Request counts and latencies
- Event processing rates
- Cache hit/miss rates
- Database operation metrics

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify -P integration-test
```

### Performance Tests
```bash
mvn verify -P performance-test
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is proprietary and confidential. 