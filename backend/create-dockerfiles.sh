#!/bin/bash

# Array of services and their ports
declare -A services=(
    ["auth-service"]=8081
    ["user-service"]=8082
    ["restaurant-service"]=8083
    ["menu-service"]=8084
    ["order-service"]=8085
    ["payment-service"]=8089
    ["reservation-service"]=8090
    ["review-service"]=8091
    ["analytics-service"]=8092
    ["qrcode-service"]=8093
    ["document-service"]=8094
    ["hail-waiter-service"]=8095
    ["rating-service"]=8096
    ["notification-service"]=8097
)

# Function to create Dockerfile
create_dockerfile() {
    local service=$1
    local port=$2
    local dockerfile="${service}/Dockerfile"

    # Skip if Dockerfile already exists
    if [ -f "$dockerfile" ]; then
        echo "Dockerfile already exists for ${service}"
        return
    }

    echo "Creating Dockerfile for ${service}"
    cat > "$dockerfile" << EOF
# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE ${port}
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

    echo "Created Dockerfile for ${service}"
}

# Create Dockerfiles for all services
for service in "${!services[@]}"; do
    if [ -d "$service" ]; then
        create_dockerfile "$service" "${services[$service]}"
    else
        echo "Directory not found: ${service}"
    fi
done

echo "Dockerfile creation completed" 