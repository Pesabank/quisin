# Quisin - Modern Restaurant Management System

## Overview
Quisin is a comprehensive restaurant management system built with a microservices architecture, offering role-based dashboards and real-time features for restaurant operations.

### Key Features
- Multi-tenant architecture supporting multiple restaurants
- Role-based access control (Superadmin, Admin, Kitchen, Waiter, Customer)
- Real-time order management and tracking
- Integrated payment processing (Multiple payment methods)
- Advanced analytics and reporting
- Inventory management
- Customer loyalty program
- QR code-based ordering system

## Technology Stack

### Backend
- **Core**: Java 17, Kotlin
- **Framework**: Spring Boot 3.2.1, Spring Cloud
- **Database**: PostgreSQL (primary), Redis (caching)
- **Messaging**: Kafka
- **Security**: JWT, OAuth2
- **Documentation**: OpenAPI/Swagger
- **Build**: Maven
- **Containerization**: Docker & Docker Compose

### Frontend
- **Framework**: Nuxt.js 3 (Vue 3)
- **Language**: TypeScript
- **State Management**: Pinia
- **Styling**: Tailwind CSS
- **Testing**: Vitest, Vue Test Utils
- **Real-time**: WebSocket

## System Architecture

### Microservices
1. **API Gateway**: Entry point and routing
2. **Auth Service**: Authentication and authorization
3. **User Service**: User management and profiles
4. **Restaurant Service**: Restaurant management
5. **Menu Service**: Menu and inventory management
6. **Order Service**: Order processing and tracking
7. **Payment Service**: Payment processing (Multiple gateways)
8. **Analytics Service**: Business intelligence and reporting
9. **Notification Service**: Real-time notifications
10. **QR Code Service**: QR code generation and management
11. **Document Service**: Document generation and management

## Getting Started

### Prerequisites
1. Install required software:
   ```bash
   - JDK 17
   - Node.js 18+
   - Docker & Docker Compose
   - PostgreSQL 14+
   - Redis 6+
   - Kafka
   ```

### Backend Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/Pesabank/quisin.git
   cd quisin/backend
   ```

2. Configure environment variables:
   ```bash
   cp .env.example .env
   # Edit .env with your configurations
   ```

3. Build all services:
   ```bash
   ./build-services.sh
   ```

4. Start the infrastructure:
   ```bash
   docker-compose up -d postgres redis kafka
   ```

5. Run the services:
   ```bash
   docker-compose up -d
   ```

### Frontend Setup
1. Navigate to frontend directory:
   ```bash
   cd ../frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Configure environment:
   ```bash
   cp .env.example .env
   ```

4. Run development server:
   ```bash
   npm run dev
   ```

## Role-Based Dashboards

### Superadmin Dashboard
- Global restaurant management
- System-wide analytics
- Restaurant onboarding
- Global settings management

### Restaurant Admin Dashboard
- Restaurant-specific management
- Menu and inventory control
- Staff management
- Analytics and reporting

### Kitchen Dashboard
- Real-time order management
- Order prioritization
- Preparation time tracking
- Inventory alerts

### Waiter Dashboard
- Table management
- Order taking
- Bill management
- Customer service requests

### Customer Dashboard
- Menu browsing
- Order placement
- Payment processing
- Order tracking

## API Documentation
- API Gateway: http://localhost:8080/swagger-ui.html
- Individual service Swagger docs available at: http://localhost:{port}/swagger-ui.html

## Testing
```bash
# Backend
cd backend
./mvnw test        # Unit tests
./mvnw verify      # Integration tests

# Frontend
cd frontend
npm run test       # All tests
npm run test:unit  # Unit tests only
```

## Monitoring
- Health checks: `/actuator/health`
- Metrics: `/actuator/metrics`
- Prometheus: `/actuator/prometheus`

## Security Features
- JWT authentication
- Role-based access control
- API rate limiting
- Input validation
- XSS protection
- CORS configuration

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License
This project is licensed under the MIT License.

## Support
For support and queries, please create an issue in the repository.