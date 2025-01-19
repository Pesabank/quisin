#!/bin/bash

# Array of services
services=(
    "auth-service"
    "user-service"
    "restaurant-service"
    "menu-service"
    "order-service"
    "payment-service"
    "reservation-service"
    "review-service"
    "analytics-service"
    "qrcode-service"
    "document-service"
    "hail-waiter-service"
    "rating-service"
    "notification-service"
)

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

# Function to build a service
build_service() {
    local service=$1
    echo -e "\n${GREEN}Building ${service}...${NC}"
    
    if [ ! -d "$service" ]; then
        echo -e "${RED}Directory not found: ${service}${NC}"
        return 1
    fi

    # Build with Maven
    cd "$service"
    if ! mvn clean package -DskipTests; then
        echo -e "${RED}Maven build failed for ${service}${NC}"
        cd ..
        return 1
    fi

    # Build Docker image
    if ! docker build -t "quisin/${service}:latest" .; then
        echo -e "${RED}Docker build failed for ${service}${NC}"
        cd ..
        return 1
    fi

    cd ..
    echo -e "${GREEN}Successfully built ${service}${NC}"
    return 0
}

# Main build process
echo -e "${GREEN}Starting build process for all services...${NC}"

# Create Dockerfiles if needed
if [ -f "create-dockerfiles.sh" ]; then
    chmod +x create-dockerfiles.sh
    ./create-dockerfiles.sh
fi

# Build each service
failed_services=()
for service in "${services[@]}"; do
    if ! build_service "$service"; then
        failed_services+=("$service")
    fi
done

# Summary
echo -e "\n${GREEN}Build process completed${NC}"
if [ ${#failed_services[@]} -eq 0 ]; then
    echo -e "${GREEN}All services built successfully${NC}"
else
    echo -e "${RED}The following services failed to build:${NC}"
    for service in "${failed_services[@]}"; do
        echo -e "${RED}- ${service}${NC}"
    done
fi

# Instructions for running
echo -e "\n${GREEN}To run all services:${NC}"
echo "docker-compose up -d" 