#!/bin/bash

# Usage: ./rollback.sh <version> <environment>
# Example: ./rollback.sh 1 staging

VERSION=$1
ENV=$2
CONFIG_DIR="/etc/quisin/restaurant-service"

# Validate arguments
if [ -z "$VERSION" ] || [ -z "$ENV" ]; then
    echo "Usage: ./rollback.sh <version> <environment>"
    exit 1
fi

if [[ ! "$ENV" =~ ^(dev|staging|prod)$ ]]; then
    echo "Invalid environment. Use: dev, staging, or prod"
    exit 1
fi

# Load environment variables
source "$CONFIG_DIR/.env.$ENV"

echo "Starting rollback to version $VERSION in $ENV environment..."

# Backup database before rollback
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="/backup/quisin/restaurant-service/pre_rollback_${ENV}_${TIMESTAMP}.sql"

echo "Creating backup before rollback..."
PGPASSWORD=$DB_PASSWORD pg_dump \
    -h $DB_HOST \
    -U $DB_USER \
    -d $DB_NAME \
    -F c \
    -f "$BACKUP_FILE"

if [ $? -ne 0 ]; then
    echo "❌ Backup failed, aborting rollback"
    exit 1
fi

echo "✅ Backup created successfully"

# Execute rollback
echo "Executing rollback..."
mvn flyway:undo \
    -Dflyway.url=jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME \
    -Dflyway.user=$DB_USER \
    -Dflyway.password=$DB_PASSWORD \
    -Dflyway.target=$VERSION

if [ $? -eq 0 ]; then
    echo "✅ Rollback completed successfully"
else
    echo "❌ Rollback failed"
    echo "Attempting to restore from backup..."
    
    PGPASSWORD=$DB_PASSWORD pg_restore \
        -h $DB_HOST \
        -U $DB_USER \
        -d $DB_NAME \
        -c \
        "$BACKUP_FILE"
    
    if [ $? -eq 0 ]; then
        echo "✅ Database restored from backup"
    else
        echo "❌ Database restore failed"
        echo "Manual intervention required!"
        exit 1
    fi
fi

# Verify database state
echo "Verifying database state..."
./verify-deployment.sh $ENV

echo "Rollback process completed" 