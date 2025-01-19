# Rating Service

## Overview
The Rating Service manages customer ratings and feedback in the Quisin restaurant management system. It provides a comprehensive system for collecting, analyzing, and managing ratings across different aspects of the restaurant experience, with integration to the Review Service for detailed feedback.

### Core Features
- 5-star rating system
- Multi-category ratings
  * Restaurant rating
  * Food rating
  * Service rating
  * Waiter rating
- Review integration for comments
- Analytics and reporting
- Rating summaries and trends

### Technical Details
- Built with Spring Boot and Kotlin
- JPA for data persistence
- Integration with Review Service
- Analytics support

## Configuration

```yaml
server:
  port: 8096
spring:
  application:
    name: rating-service
  datasource:
    url: jdbc:postgresql://localhost:5432/quisin_rating_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

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

## API Endpoints

### Rating Management
- `POST /api/v1/ratings`
  * Submit new rating
  * Body:
    ```json
    {
      "userId": "uuid",
      "entityId": "uuid",
      "type": "RESTAURANT|FOOD|SERVICE|WAITER",
      "score": 1-5,
      "comment": "string (optional)",
      "tags": ["string"] (optional)
    }
    ```

- `PUT /api/v1/ratings/{ratingId}`
  * Update existing rating
  * Body:
    ```json
    {
      "score": 1-5 (optional),
      "comment": "string (optional)",
      "tags": ["string"] (optional),
      "status": "ACTIVE|HIDDEN|REPORTED" (optional)
    }
    ```

- `DELETE /api/v1/ratings/{ratingId}`
  * Delete rating

### Rating Retrieval
- `GET /api/v1/ratings/entity/{entityId}`
  * Get ratings for specific entity
  * Query params:
    - type: Rating type

- `GET /api/v1/ratings/summary/{entityId}`
  * Get rating summary statistics
  * Query params:
    - type: Rating type

### Analytics
- `POST /api/v1/ratings/analytics`
  * Get detailed rating analytics
  * Body:
    ```json
    {
      "entityId": "uuid",
      "type": "RESTAURANT|FOOD|SERVICE|WAITER",
      "startDate": "ISO date (optional)",
      "endDate": "ISO date (optional)"
    }
    ```

## Integration Points

### Review Service Integration
- Comments from ratings are synchronized with reviews
- Review service handles detailed feedback
- Bi-directional updates between ratings and reviews

### Analytics Service Integration
- Rating metrics for analytics dashboards
- Trend analysis
- Performance insights

### Admin Dashboard Integration
- Rating summaries and reports
- Management interface for ratings
- Export capabilities

## Rating Types
1. Restaurant Rating
   - Overall experience
   - Ambiance
   - Value for money

2. Food Rating
   - Taste
   - Presentation
   - Portion size

3. Service Rating
   - Speed
   - Friendliness
   - Efficiency

4. Waiter Rating
   - Individual performance
   - Service quality
   - Responsiveness

## Analytics Features
- Average ratings by category
- Trend analysis
- Rating distribution
- Time-based analysis
- Weighted scoring system
  * Recent ratings weighted higher
  * Score normalization
  * Outlier handling

## Security
- JWT authentication required
- Role-based access control
  * Customers can submit ratings
  * Staff can view ratings
  * Managers can manage ratings
- Input validation
- Rate limiting

## Monitoring
- Rating submission rate
- Average scores
- Response times
- Error rates
- Integration health

## Getting Started

### Prerequisites
1. Java 17
2. PostgreSQL
3. Review Service
4. Analytics Service

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
docker build -t quisin/rating-service .

# Run container
docker run -p 8096:8096 quisin/rating-service
```

## Database Schema

### Ratings Table
```sql
CREATE TABLE ratings (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    entity_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    score INTEGER NOT NULL,
    comment TEXT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

### Rating Summary Table
```sql
CREATE TABLE rating_summaries (
    id UUID PRIMARY KEY,
    entity_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    average_score DECIMAL NOT NULL,
    total_ratings INTEGER NOT NULL,
    weighted_score DECIMAL NOT NULL
);
``` 