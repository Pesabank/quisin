#!/bin/bash

# Set variables
JMETER_HOME="/opt/apache-jmeter"
TEST_PLAN="restaurant-service-test-plan.jmx"
RESULTS_DIR="results"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
RESULTS_FILE="$RESULTS_DIR/results_$TIMESTAMP.jtl"
REPORT_DIR="$RESULTS_DIR/report_$TIMESTAMP"

# Create results directory if it doesn't exist
mkdir -p "$RESULTS_DIR"

# Get JWT token for authentication
JWT_TOKEN=$(curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}' \
  | jq -r '.token')

# Run JMeter test
$JMETER_HOME/bin/jmeter -n \
  -t $TEST_PLAN \
  -l $RESULTS_FILE \
  -e -o $REPORT_DIR \
  -Jjwt_token=$JWT_TOKEN \
  -Jhost=localhost \
  -Jport=8083

# Print summary
echo "Test completed. Results saved to: $RESULTS_FILE"
echo "HTML report generated in: $REPORT_DIR" 