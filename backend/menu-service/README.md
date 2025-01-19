# Menu Service

## Implementation Status

### Core Features
✅ Menu CRUD Operations
✅ Menu Item Management
✅ Search and Filtering
✅ Rate Limiting (100 req/min)
✅ Metrics Collection
✅ Health Checks
✅ Notification System
✅ Async Image Processing
✅ Inventory Management
✅ Stock Tracking
✅ Transaction Logging

### Integration Features
✅ JWT-based Authentication
✅ Role-based Access Control
✅ Event-driven Architecture
✅ Circuit Breaker Implementation
✅ Redis Caching
✅ Docker Configuration
✅ Integration Tests
✅ Kafka Integration
✅ Notification Service Integration

### Pending Features
⏳ Analytics Dashboard Integration
⏳ Advanced Search Capabilities
⏳ Batch Operations
⏳ Menu Version Control

## Overview
The Menu Service is a core microservice in the Quisin platform that manages restaurant menus, menu items, inventory, and related operations. It provides comprehensive functionality for menu management, including support for categories, items, allergens, dietary information, and inventory tracking.

## Features

### Core Features
- ✅ Menu CRUD Operations
  * Create, read, update, and delete menu entities
  * Menu categorization and item management
  * Price and availability management
  * Image handling for menu items
  * Allergen and dietary information tracking

- ✅ Menu Item Management
  * Comprehensive item details
  * Price variations and options
  * Allergen tracking
  * Dietary information
  * Preparation time tracking
  * Availability status

- ✅ Inventory Management
  * Real-time stock tracking
  * Multiple transaction types:
    - STOCK_IN: Adding new inventory
    - STOCK_OUT: Removing items from inventory
    - SPOILAGE: Recording spoiled items
    - ADJUSTMENT: Manual corrections
  * Low stock alerts
  * Transaction history
  * Employee tracking

- ✅ Search and Filtering
  * Search by name and description
  * Filter by allergens
  * Filter by dietary requirements
  * Price range filtering
  * Availability filtering
  * Stock level filtering

### Integration Features
- ✅ JWT-based authentication
- ✅ Role-based access control
- ✅ Event-driven architecture with Kafka
- ✅ Circuit breaker implementation
- ✅ Redis caching
- ✅ Rate limiting (100 req/min)
- ✅ Metrics collection
- ✅ Health monitoring
- ✅ Notification system
- ✅ Async image processing

## Tech Stack
- Java 17
- Spring Boot 3.2.0
- Spring Security with OAuth2 Resource Server
- Spring Data JPA
- PostgreSQL
- Redis (for caching)
- Kafka (for event streaming)
- Maven
- Docker & Docker Compose

## API Endpoints

### Menu Management
```
POST /api/v1/menus
GET /api/v1/menus/{id}
PUT /api/v1/menus/{id}
DELETE /api/v1/menus/{id}
POST /api/v1/menus/search
GET /api/v1/menus/restaurant/{restaurantId}
```

### Inventory Management
```
POST /api/v1/inventory/transaction
GET /api/v1/inventory/restaurant/{restaurantId}
GET /api/v1/inventory/menu-item/{menuItemId}
GET /api/v1/inventory/restaurant/{restaurantId}/date-range
GET /api/v1/inventory/restaurant/{restaurantId}/type
```

### Image Management
```
POST /api/v1/menu-items/{menuItemId}/images
DELETE /api/v1/menu-items/{menuItemId}/images/{imageId}
```

## Data Models

### Menu
```typescript
{
  id: string;
  name: string;
  description: string;
  restaurantId: string;
  categories: MenuCategory[];
  active: boolean;
  createdAt: string;
  updatedAt: string;
}
```

### Menu Category
```typescript
{
  id: string;
  name: string;
  description: string;
  items: MenuItem[];
  displayOrder: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}
```

### Menu Item
```typescript
{
  id: string;
  name: string;
  description: string;
  price: number;
  images: string[];
  options: MenuItemOption[];
  allergens: Allergen[];
  dietaryInfo: DietaryInfo[];
  calories: number;
  preparationTime: number;
  available: boolean;
  displayOrder: number;
  createdAt: string;
  updatedAt: string;
}
```

## Setup Instructions

1. Prerequisites:
   - JDK 17
   - Maven
   - Docker & Docker Compose
   - PostgreSQL
   - Redis
   - Kafka

2. Environment Variables:
```properties
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/quisin_menu_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# Server
SERVER_PORT=8084

# Redis
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Security
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=http://localhost:8081
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=http://localhost:8081/.well-known/jwks.json

# Notification Service
QUISIN_SERVICES_NOTIFICATION_URL=http://localhost:8085

# Image Processing
QUISIN_IMAGES_PATH=/path/to/images
QUISIN_IMAGES_MAX_WIDTH=1200
QUISIN_IMAGES_MAX_HEIGHT=1200
QUISIN_IMAGES_QUALITY=0.85
```

3. Local Development:
```bash
# Start required services
docker-compose up -d

# Build the application
./mvnw clean package

# Run the application
./mvnw spring-boot:run
```

## Security
- JWT-based authentication
- Role-based access control (ADMIN, RESTAURANT_OWNER)
- Rate limiting (100 requests per minute)
- Input validation
- Error handling

## Caching
- Menu details caching with Redis
- Configurable TTL
- Cache invalidation on updates

## Rate Limiting
- Global rate limit: 100 requests per minute
- Per-endpoint configuration available
- Headers for rate limit information:
  * X-Rate-Limit-Remaining
  * X-Rate-Limit-Retry-After-Seconds

## Metrics
- Menu creation/update/deletion counts
- Operation timing metrics
- Active menu counts
- Menu item counts
- Custom business metrics
- Image processing metrics
- Notification delivery metrics

## Health Checks
- Application health status
- Database connectivity
- Redis connectivity
- Kafka connectivity
- Custom menu service health indicators
- Notification service health
- Image storage health

## Notification System
The service integrates with the notification service to send alerts for:
- Menu creation
- Menu updates
- Menu deletion
- Image processing completion
- Error notifications

### Notification Types
```typescript
{
  MENU_CREATED: {
    priority: "NORMAL",
    message: "New menu has been created"
  },
  MENU_UPDATED: {
    priority: "NORMAL",
    message: "Menu has been updated"
  },
  MENU_DELETED: {
    priority: "HIGH",
    message: "Menu has been deleted"
  }
}
```

## Image Processing
The service includes asynchronous image processing capabilities:

### Features
- Async processing using dedicated thread pool
- Image validation
  * Size limit: 10MB
  * Formats: JPG, JPEG, PNG
- Image optimization
  * Configurable max dimensions
  * Quality preservation
  * Format conversion
- Secure storage
  * UUID-based naming
  * Path sanitization
  * Automatic cleanup

### Configuration
```yaml
quisin:
  images:
    storage-path: /path/to/images
    max-width: 1200
    max-height: 1200
    quality: 0.85
```

### Usage Example
```kotlin
// Upload image
@PostMapping("/{menuItemId}/images")
fun uploadImage(@PathVariable menuItemId: String, @RequestParam("file") file: MultipartFile): String {
    return imageProcessingService.processAndStoreImage(file, menuItemId).get()
}

// Delete image
@DeleteMapping("/{menuItemId}/images/{imageId}")
fun deleteImage(@PathVariable imageId: String): Boolean {
    return imageProcessingService.deleteImage(imageId).get()
}
```

## Event System
The service publishes the following events:
- MenuCreatedEvent
- MenuUpdatedEvent
- MenuDeletedEvent
- MenuItemCreatedEvent
- MenuItemUpdatedEvent
- MenuItemDeletedEvent
- ImageProcessedEvent
- NotificationSentEvent

## Docker Support

### Building the Image
```bash
docker build -t quisin/menu-service .
```

### Running with Docker Compose
```bash
docker-compose up -d
```

The service includes:
- Multi-stage build
- Health checks
- JVM optimization
- Wait-for-it script for dependency handling
- Volume management for image storage
- Network configuration
- Notification service integration

## Monitoring
- Health check endpoint: `/actuator/health`
- Metrics endpoint: `/actuator/metrics`
- Info endpoint: `/actuator/info`
- Custom metrics for business operations
- Image processing metrics
- Notification delivery metrics

## Testing

### Unit Tests
```bash
# Run unit tests
./mvnw test
```

### Integration Tests
```bash
# Run integration tests
./mvnw verify -P integration-test
```

### Component Tests
```bash
# Test notification system
./mvnw test -Dtest=NotificationServiceTest

# Test image processing
./mvnw test -Dtest=ImageProcessingServiceTest
```

## Error Handling
The service includes comprehensive error handling for:
- Image processing errors
- Notification delivery failures
- Storage issues
- Invalid file types
- Size limit violations
- Network connectivity issues

## Performance Considerations
- Async image processing to prevent blocking
- Configurable thread pool for image operations
- Optimized image storage and retrieval
- Efficient notification delivery
- Caching of processed images
- Rate limiting for resource protection

## Recent Implementations

### 1. Notification System
- ✅ Notification service configuration
- ✅ Event-based notifications
- ✅ Priority-based handling
- ✅ Error handling and retries
- ✅ Integration tests
```kotlin
// Example usage
notificationService.sendMenuCreatedNotification(menuId, restaurantId)
notificationService.sendMenuUpdatedNotification(menuId, restaurantId)
notificationService.sendMenuDeletedNotification(menuId, restaurantId)
```

### 2. Image Processing
- ✅ Async processing configuration
- ✅ Image validation and optimization
- ✅ Secure storage management
- ✅ Integration tests
```kotlin
// Example usage
imageProcessingService.processAndStoreImage(file, menuItemId)
imageProcessingService.deleteImage(imagePath)
```

### 3. Rate Limiting
- ✅ Global rate limit configuration
- ✅ Per-endpoint limits
- ✅ Custom headers
```properties
quisin.rate-limit.requests=100
quisin.rate-limit.duration=60
```

### 4. Metrics Collection
- ✅ Business metrics
- ✅ Performance metrics
- ✅ Custom indicators
```kotlin
// Available metrics
menu.created.count
menu.updated.count
menu.deleted.count
menu.search.count
menu.creation.time
menu.update.time
menu.search.time
menu.active.count
menu.items.count
```

### 5. Health Monitoring
- ✅ Service health indicators
- ✅ Dependency checks
- ✅ Custom health metrics
```bash
# Health check endpoint
curl http://localhost:8084/actuator/health
```

## Testing Coverage

### Unit Tests
- ✅ NotificationServiceTest
- ✅ ImageProcessingServiceTest
- ✅ Rate limiting tests
- ✅ Metrics tests

### Integration Tests
- ✅ Menu CRUD operations
- ✅ Image processing workflow
- ✅ Notification delivery
- ✅ Cache operations

### Performance Tests
- ✅ Rate limiting behavior
- ✅ Image processing performance
- ✅ Concurrent operations

## Docker Support

### Local Development
```bash
# Build and run with dependencies
docker-compose up -d

# Build individual service
docker build -t quisin/menu-service .

# Run tests in container
docker-compose -f docker-compose.test.yml up
```

### Production Deployment
```bash
# Build production image
docker build -t quisin/menu-service:prod -f Dockerfile.prod .

# Run with production config
docker-compose -f docker-compose.prod.yml up -d
```