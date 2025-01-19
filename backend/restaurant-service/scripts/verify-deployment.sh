#!/bin/bash

# Usage: ./verify-deployment.sh <environment>
# Example: ./verify-deployment.sh staging

ENV=$1
SERVICE_URL="http://localhost:8083"
if [ "$ENV" = "prod" ]; then
    SERVICE_URL="https://api.quisin.com/restaurants"
elif [ "$ENV" = "staging" ]; then
    SERVICE_URL="https://staging-api.quisin.com/restaurants"
fi

echo "Verifying deployment for $ENV environment..."

# Check if service is running
echo "Checking service health..."
HEALTH_CHECK=$(curl -s "$SERVICE_URL/actuator/health")
if [[ $HEALTH_CHECK == *"UP"* ]]; then
    echo "✅ Service is healthy"
else
    echo "❌ Service health check failed"
    exit 1
fi

# Check database connection
echo "Checking database connection..."
DB_CHECK=$(curl -s "$SERVICE_URL/actuator/health/db")
if [[ $DB_CHECK == *"UP"* ]]; then
    echo "✅ Database connection is healthy"
else
    echo "❌ Database connection check failed"
    exit 1
fi

# Check metrics endpoint
echo "Checking metrics endpoint..."
METRICS_CHECK=$(curl -s "$SERVICE_URL/actuator/metrics")
if [ $? -eq 0 ]; then
    echo "✅ Metrics endpoint is accessible"
else
    echo "❌ Metrics endpoint check failed"
    exit 1
fi

# Check memory usage
echo "Checking memory usage..."
MEMORY_USED=$(curl -s "$SERVICE_URL/actuator/metrics/jvm.memory.used" | jq '.measurements[0].value')
MEMORY_MAX=$(curl -s "$SERVICE_URL/actuator/metrics/jvm.memory.max" | jq '.measurements[0].value')
MEMORY_PERCENT=$(echo "scale=2; $MEMORY_USED/$MEMORY_MAX * 100" | bc)

if (( $(echo "$MEMORY_PERCENT < 90" | bc -l) )); then
    echo "✅ Memory usage is within acceptable range ($MEMORY_PERCENT%)"
else
    echo "⚠️ High memory usage detected ($MEMORY_PERCENT%)"
fi

echo "Deployment verification completed for $ENV environment" 