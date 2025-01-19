# Quisin Payment Service

A comprehensive payment processing service that handles multiple payment methods for the Quisin restaurant management system.

## Features

### 1. Payment Methods ✅
- Credit/Debit Cards (via Stripe)
- Mobile Money (M-PESA)
- Digital Wallets
  * PayPal
  * Apple Pay
- Cryptocurrency (via Coinbase)
- Cash Payments
- Bank Transfers

### 2. Payment Types ✅
- Single Order Payments
- Group Order Payments
- Split Bill Payments
- Reservation Fees

### 3. Core Features ✅
- Multi-currency Support
- Real-time Payment Processing
- Payment Status Tracking
- Transaction History
- Refund Management
- Split Payment Handling

### 4. Security Features ✅
- Fraud Detection
- PCI Compliance
- Secure Token Management
- Encryption
- Rate Limiting

### 5. Integration Features ✅
- Payment Gateway Integration
- Webhook Handling
- Event Publishing
- Notification System
- Error Handling

## Technical Stack

### Core Technologies
- Kotlin
- Spring Boot
- Spring Data JPA
- Spring Security

### Databases
- PostgreSQL (primary data store)
- Redis (caching)

### Payment Gateways
- Stripe
- PayPal
- M-PESA
- Coinbase Commerce
- Apple Pay

## Getting Started

### Prerequisites
1. JDK 17
2. Maven
3. Docker & Docker Compose
4. PostgreSQL
5. Redis

### Configuration

1. Database Setup
```bash
# Create payment database
docker exec -it postgres psql -U postgres -c "CREATE DATABASE quisin_payment;"
```

2. Environment Variables
```env
# Payment Service
PAYMENT_DB_NAME=quisin_payment
PAYMENT_SERVICE_PORT=8089
STRIPE_SECRET_KEY=your_stripe_secret_key
STRIPE_WEBHOOK_SECRET=your_stripe_webhook_secret
PAYPAL_CLIENT_ID=your_paypal_client_id
PAYPAL_SECRET=your_paypal_secret
MPESA_CONSUMER_KEY=your_mpesa_consumer_key
MPESA_CONSUMER_SECRET=your_mpesa_consumer_secret
COINBASE_API_KEY=your_coinbase_api_key
COINBASE_WEBHOOK_SECRET=your_coinbase_webhook_secret
```

3. Build and Run
```bash
# Build service
mvn clean package

# Run with Docker
docker-compose up -d payment-service
```

## API Documentation

### Payment Endpoints

#### Create Payment
```http
POST /api/v1/payments
Content-Type: application/json

{
  "userId": "uuid",
  "orderId": "uuid",
  "paymentMethod": "CREDIT_CARD",
  "paymentType": "SINGLE_ORDER",
  "amount": 50.00,
  "currency": "USD"
}
```

#### Create Split Payment
```http
POST /api/v1/payments/split
Content-Type: application/json

{
  "orderId": "uuid",
  "paymentMethod": "CREDIT_CARD",
  "participants": [
    {
      "userId": "uuid",
      "amount": 25.00
    },
    {
      "userId": "uuid",
      "amount": 25.00
    }
  ]
}
```

#### Get Payment Status
```http
GET /api/v1/payments/{paymentId}
```

#### Update Payment Status
```http
PATCH /api/v1/payments/{paymentId}
Content-Type: application/json

{
  "status": "SUCCESSFUL"
}
```

## Webhook Endpoints

### Payment Gateway Webhooks
```http
POST /api/v1/payments/webhooks/stripe
POST /api/v1/payments/webhooks/paypal
POST /api/v1/payments/webhooks/mpesa
POST /api/v1/payments/webhooks/coinbase
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is proprietary and confidential. 