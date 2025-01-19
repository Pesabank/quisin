# Build stage
FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder
WORKDIR /build

# Copy the project files
COPY . .

# Build without running tests
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install netcat for health checks
RUN apk add --no-cache netcat-openbsd

# Copy the jar from builder stage
COPY --from=builder /build/target/auth-service.jar app.jar

# Create logs directory with proper permissions
RUN mkdir -p /app/logs && \
    chmod 777 /app/logs

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring && \
    chown -R spring:spring /app/logs
USER spring

# Expose port
EXPOSE 8081

# Run the application with debug options and health check endpoint enabled
ENTRYPOINT ["java", "-jar", "app.jar", \
            "-Dspring.output.ansi.enabled=ALWAYS", \
            "-Dlogging.level.org.springframework=DEBUG", \
            "-Dmanagement.endpoints.web.exposure.include=health", \
            "-Dmanagement.endpoint.health.show-details=ALWAYS"]
