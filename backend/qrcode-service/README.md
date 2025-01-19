# QR Code Service

## Overview
The QR Code Service handles the automatic generation and management of QR codes for restaurant tables. It integrates with the Table Management Service to create unique QR codes when new tables are added to the system.

### Features
- Automatic QR code generation for new tables
- Smart table numbering detection and auto-completion
- Multiple export formats (PNG, PDF)
- Bulk QR code generation
- Intelligent table label pattern recognition
- Restaurant-specific QR code management

### Table Numbering Intelligence
The service supports various table numbering patterns:
- Numeric patterns (e.g., "01", "02", "03")
- T-prefixed patterns (e.g., "T01", "T02", "T03")
- TABLE-prefixed patterns (e.g., "TABLE 01", "TABLE 02")
- Custom patterns with automatic sequence detection

### Configuration
```yaml
server:
  port: 8093
spring:
  application:
    name: qrcode-service

qrcode:
  export:
    formats:
      - PNG
      - PDF
    resolution: 300
    size: 512
  table:
    pattern-recognition:
      enabled: true
      patterns:
        - "T\\d+"
        - "TABLE \\d+"
        - "\\d+"
```

### API Endpoints
- `POST /api/v1/qrcodes/generate` - Generate QR code for a single table
- `POST /api/v1/qrcodes/bulk` - Generate QR codes for multiple tables
- `GET /api/v1/qrcodes/{tableId}` - Retrieve QR code for specific table
- `GET /api/v1/qrcodes/{tableId}/download` - Download QR code in specified format
- `POST /api/v1/qrcodes/validate` - Validate QR code content

### Integration Points
- Table Management Service for table creation events
- Document Service for file storage and management
- Restaurant Service for restaurant validation

### Usage Example
```bash
# Generate QR code for a new table
curl -X POST http://localhost:8093/api/v1/qrcodes/generate \
  -H "Content-Type: application/json" \
  -d '{
    "restaurantId": "123",
    "tableLabel": "T01",
    "format": "PNG"
  }'
``` 