# Quisin Restaurant Management System

A modern, microservices-based restaurant management system built with Spring Boot and Docker.

## Overview

Quisin is a comprehensive restaurant management system that helps restaurants manage their operations efficiently. The system is built using a microservices architecture, with each service handling specific business functionality.

## Services

### Order Service ✅
- Order management and processing
- Real-time menu validation
- Waiter assignment system
- Event-driven architecture
- Redis caching
- Comprehensive test coverage

### Auth Service ✅
- User authentication and authorization
- JWT token management
- Role-based access control
- Security features

### User Service ✅
- User profile management
- Staff management
- Role management

### Restaurant Service ✅
- Restaurant management
- Chain management
- Location management

### Menu Service ✅
- Menu management
- Inventory tracking
- Real-time availability updates

### Payment Service ✅
- Secure payment processing
- Multiple payment methods support
- Transaction history
- Payment status tracking
- Refund management

### Reservation Service ✅
- Table reservation management
- Capacity management
- Waitlist handling
- Real-time availability updates
- Email notifications

### Review Service ✅
- Customer reviews and ratings
- Review moderation
- Response management
- Analytics integration
- Sentiment analysis

### Analytics Service ✅
- Business intelligence
- Performance metrics
- Customer insights
- Sales analytics
- Trend analysis

## Technical Stack

- Java 17
- Spring Boot 3.2.1
- Spring Cloud
- PostgreSQL
- Redis
- Kafka
- Docker
- Maven

## Getting Started

### Prerequisites
- JDK 17
- Maven
- Docker & Docker Compose
- PostgreSQL
- Redis

### Building the Project

1. Clone the repository:
```bash
git clone https://github.com/yourusername/quisin.git
cd quisin
```

2. Build all services:
```bash
cd backend
mvn clean package
```

3. Build Docker images:
```bash
docker-compose build
```

4. Run the system:
```bash
docker-compose up -d
```

## Architecture

### Event-Driven Communication
- Kafka for asynchronous communication
- Event publishing for order updates
- Real-time notifications

### Caching Strategy
- Redis for high-performance caching
- Menu item caching
- User session caching

### Security
- JWT-based authentication
- Role-based access control
- API Gateway security

## API Documentation

Each service includes its own API documentation. Access the Swagger UI at:
- Order Service: http://localhost:8085/swagger-ui.html
- Auth Service: http://localhost:8081/swagger-ui.html
- User Service: http://localhost:8082/swagger-ui.html
- Restaurant Service: http://localhost:8083/swagger-ui.html
- Menu Service: http://localhost:8084/swagger-ui.html

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is proprietary and confidential. 

## Production Environment

### Monitoring & Observability
- Prometheus for metrics collection
- Grafana dashboards
- Custom business metrics
- Service health monitoring
- Performance tracking

### High Availability
- Service discovery
- Load balancing
- Circuit breakers
- Fallback mechanisms
- Scalable architecture

### Security
- SSL/TLS encryption
- API Gateway security
- Rate limiting
- CORS configuration
- Service authentication

### Data Management
- Automated backups
- Data migration tools
- Cache management
- Message queue system
- Database replication

## Monitoring Dashboard

Access the Grafana dashboard at http://localhost:3000 to view:

### System Metrics
- Service health status
- HTTP response times
- Resource utilization
- Error rates
- System performance

### Business Metrics
- Payment transaction rates
- Order processing times
- Reservation confirmation rates
- Customer satisfaction trends
- Revenue analytics