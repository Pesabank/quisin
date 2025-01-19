# Restaurant Service

## Overview
The Restaurant Service is a core microservice in the Quisin platform that manages restaurant-related operations. It provides comprehensive functionality for restaurant management, chain operations, and location services.

## Features
- ✅ Restaurant CRUD Operations
  * Create, read, update, and delete restaurant entities
  * Comprehensive validation and ownership verification
  * Location and operating hours management
  * Feature and amenity tracking
  * Image management for restaurants

- ✅ Chain Management
  * Support for multi-restaurant chains
  * Chain ownership verification
  * Restaurant-chain relationship management

- ✅ Multi-tenant Support
  * Secure data isolation between owners
  * Role-based access control
  * Owner verification system

## Tech Stack
- Java 17
- Spring Boot 3.2.0
- Spring Security with OAuth2 Resource Server
- Spring Data JPA
- PostgreSQL
- Redis (for caching)
- Flyway (for database migrations)
- Maven

## API Endpoints

### Restaurant Management
```
POST /api/v1/restaurants
- Create new restaurant
- Required fields: name, description, location, operatingHours, features, cuisineTypes
- Authorization: Bearer token required
- Returns: Created restaurant details

GET /api/v1/restaurants/{id}
- Get restaurant details
- Authorization: Bearer token required
- Returns: Complete restaurant information

PUT /api/v1/restaurants/{id}
- Update restaurant details
- Authorization: Bearer token required (owner only)
- Returns: Updated restaurant details

DELETE /api/v1/restaurants/{id}
- Delete restaurant
- Authorization: Bearer token required (owner only)
- Returns: 204 No Content

POST /api/v1/restaurants/search
- Search restaurants with filters
- Body: SearchRestaurantRequest
- Returns: RestaurantPageResponse

GET /api/v1/restaurants/owner
- Get restaurants owned by authenticated user
- Authorization: Bearer token required
- Returns: List of RestaurantResponse
```

### Chain Management
```
POST /api/v1/chains
- Create new restaurant chain
- Required fields: name, description
- Authorization: Bearer token required
- Returns: Created chain details

GET /api/v1/chains/{id}
- Get chain details
- Authorization: Bearer token required
- Returns: Chain information

PUT /api/v1/chains/{id}
- Update chain details
- Authorization: Bearer token required (owner only)
- Returns: Updated chain details

DELETE /api/v1/chains/{id}
- Delete chain
- Authorization: Bearer token required (owner only)
- Returns: 204 No Content
```

## Data Models

### Restaurant
```typescript
{
  id: string;
  name: string;
  description: string;
  location: {
    address: string;
    city: string;
    country: string;
    postalCode: string;
    latitude?: number;
    longitude?: number;
  };
  operatingHours: {
    [day: string]: {
      open: string;  // HH:mm format
      close: string; // HH:mm format
    }
  };
  features: RestaurantFeature[];
  cuisineTypes: CuisineType[];
  status: "PENDING" | "ACTIVE" | "INACTIVE" | "SUSPENDED";
  chainId?: string;
  ownerId: string;
  imagePaths: string[];
  createdAt: string;
  updatedAt: string;
}
```

### Chain
```typescript
{
  id: string;
  name: string;
  description: string;
  ownerId: string;
  createdAt: string;
  updatedAt: string;
}
```

## Setup Instructions

1. Prerequisites:
   - JDK 17
   - Maven
   - PostgreSQL
   - Redis (optional, for caching)

2. Database Setup:
```sql
CREATE DATABASE quisin_restaurant_db;
```

3. Environment Variables:
```properties
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/quisin_restaurant_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# Server
SERVER_PORT=8083

# Security
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=http://localhost:8081
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=http://localhost:8081/.well-known/jwks.json

# Redis (optional)
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379
```

4. Build and Run:
```bash
# Build
./mvnw clean package

# Run
./mvnw spring-boot:run
```

## Security
- JWT-based authentication
- Role-based access control (ADMIN, RESTAURANT_OWNER)
- Owner verification for restaurant operations
- Input validation
- Rate limiting

## Caching
- Restaurant details caching with Redis
- Configurable TTL
- Cache invalidation on updates

## Event System
The service publishes the following events:
- RestaurantCreatedEvent
- RestaurantUpdatedEvent
- RestaurantDeletedEvent
- RestaurantStatusChangedEvent
- ChainCreatedEvent
- ChainUpdatedEvent
- ChainDeletedEvent

## Testing
```bash
# Run tests
./mvnw test

# Run integration tests
./mvnw verify -P integration-test
```

## Docker Support
```bash
# Build image
docker build -t quisin/restaurant-service .

# Run container
docker run -d --name restaurant-service \
  -p 8083:8083 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/quisin_restaurant_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  quisin/restaurant-service
```

## Monitoring
- Health check endpoint: `/actuator/health`
- Metrics endpoint: `/actuator/metrics`
- Info endpoint: `/actuator/info`