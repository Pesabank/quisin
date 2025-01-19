#!/bin/bash

echo "Testing API Gateway..."

# Test health endpoint
echo "\nTesting Health Endpoint..."
curl -X GET http://localhost:8080/actuator/health

# Test Auth Service Route
echo "\nTesting Auth Service Route..."
curl -X GET http://localhost:8080/api/auth/health

# Test User Service Route
echo "\nTesting User Service Route..."
curl -X GET http://localhost:8080/api/users/health

# Test Restaurant Service Route
echo "\nTesting Restaurant Service Route..."
curl -X GET http://localhost:8080/api/restaurants/health

# Test Menu Service Route
echo "\nTesting Menu Service Route..."
curl -X GET http://localhost:8080/api/menus/health

# Test Order Service Route
echo "\nTesting Order Service Route..."
curl -X GET http://localhost:8080/api/orders/health

# Test Fallback endpoints
echo "\nTesting Fallback Endpoints..."
curl -X GET http://localhost:8080/fallback/auth
curl -X GET http://localhost:8080/fallback/user
curl -X GET http://localhost:8080/fallback/restaurant

# Test CORS (requires a browser or specific curl command)
echo "\nTesting CORS Headers..."
curl -X OPTIONS http://localhost:8080/api/auth/health -H "Origin: http://localhost:3000" -H "Access-Control-Request-Method: GET" -v 