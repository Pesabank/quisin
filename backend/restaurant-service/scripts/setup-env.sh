#!/bin/bash

# Usage: ./setup-env.sh <environment>
# Example: ./setup-env.sh dev

ENV=$1
CONFIG_DIR="/etc/quisin/restaurant-service"
LOG_DIR="/var/log/quisin"
BACKUP_DIR="/backup/quisin/restaurant-service"

# Validate environment argument
if [[ ! "$ENV" =~ ^(dev|staging|prod)$ ]]; then
    echo "Invalid environment. Use: dev, staging, or prod"
    exit 1
fi

# Create necessary directories
echo "Creating directories..."
sudo mkdir -p "$CONFIG_DIR"
sudo mkdir -p "$LOG_DIR"
sudo mkdir -p "$BACKUP_DIR"

# Set correct permissions
sudo chown -R quisin:quisin "$CONFIG_DIR"
sudo chown -R quisin:quisin "$LOG_DIR"
sudo chown -R quisin:quisin "$BACKUP_DIR"

# Copy environment-specific configuration
echo "Copying $ENV configuration..."
sudo cp "src/main/resources/application-$ENV.yml" "$CONFIG_DIR/application.yml"

# Create environment file
echo "Creating .env file..."
cat > "$CONFIG_DIR/.env.$ENV" << EOF
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=quisin_restaurant_db
DB_USER=postgres
DB_PASSWORD=postgres

# Auth Service Configuration
AUTH_SERVICE_URL=http://auth-service:8081

# AWS Configuration (for backups)
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
S3_BUCKET=quisin-backups-$ENV

# Monitoring Configuration
PROMETHEUS_USER=admin
PROMETHEUS_PASSWORD=admin
EOF

# Setup logging configuration
echo "Setting up logback configuration..."
cat > "$CONFIG_DIR/logback-$ENV.xml" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property name="LOG_FILE" value="${LOG_DIR}/restaurant-service-${ENV}.log"/>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <include resource="org/springframework/boot/logging/logback/console-appender.xml"/>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE}</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE}.%d{yyyy-MM-dd}.gz</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
EOF

echo "Environment setup complete for $ENV" 