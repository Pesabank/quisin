#!/bin/bash

# Load environment variables
source .env

# Set variables
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backup/database/$TIMESTAMP"
LOG_FILE="/var/log/quisin/backup_$TIMESTAMP.log"

# Create backup directory
mkdir -p "$BACKUP_DIR"

# Backup database
echo "Starting database backup at $(date)" >> "$LOG_FILE"
PGPASSWORD=$DB_PASSWORD pg_dump \
  -h $DB_HOST \
  -U $DB_USER \
  -d $DB_NAME \
  -F c \
  -f "$BACKUP_DIR/database.backup" \
  >> "$LOG_FILE" 2>&1

# Check if backup was successful
if [ $? -eq 0 ]; then
  echo "Database backup completed successfully" >> "$LOG_FILE"
else
  echo "Database backup failed" >> "$LOG_FILE"
  exit 1
fi

# Compress backup
tar -czf "$BACKUP_DIR.tar.gz" "$BACKUP_DIR"
rm -rf "$BACKUP_DIR"

# Upload to cloud storage (example with AWS S3)
aws s3 cp "$BACKUP_DIR.tar.gz" \
  "s3://$S3_BUCKET/backups/restaurant-service/$TIMESTAMP/" \
  >> "$LOG_FILE" 2>&1

# Cleanup old backups (keep last 30 days)
find /backup/database -type f -name "*.tar.gz" -mtime +30 -delete

echo "Backup process completed at $(date)" >> "$LOG_FILE" 