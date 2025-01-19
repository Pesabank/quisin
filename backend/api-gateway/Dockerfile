# Build stage
FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder
WORKDIR /build

# Create Maven settings directory and add permissions
USER root
RUN mkdir -p /root/.m2 && \
    chmod -R 777 /root/.m2

# Copy the project files
COPY pom.xml .
COPY src ./src/

# Build the application
RUN mvn clean package -DskipTests --no-transfer-progress

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install netcat for health checks
RUN apk add --no-cache netcat-openbsd

# Copy the jar from builder stage
COPY --from=builder /build/target/*.jar app.jar

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]