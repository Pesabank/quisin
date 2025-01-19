export enum PaymentStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  SUCCESSFUL = 'SUCCESSFUL',
  FAILED = 'FAILED',
  CANCELLED = 'CANCELLED',
  REFUNDED = 'REFUNDED'
}

export enum PaymentMethod {
  CREDIT_CARD = 'CREDIT_CARD',
  MOBILE_MONEY = 'MOBILE_MONEY',
  DIGITAL_WALLET = 'DIGITAL_WALLET',
  CASH = 'CASH'
}

export interface PaymentMethodData {
  type: string;
  card?: {
    number: string;
    exp_month: number;
    exp_year: number;
    cvc: string;
  };
  billing_details?: {
    name: string;
    address: {
      line1: string;
      city: string;
      postal_code: string;
    };
  };
}

export interface PaymentRequest {
  orderId: string;
  amount: number;
  paymentMethodId?: string;
  phoneNumber?: string;
}

export interface PaymentResponse {
  id: string;
  orderId: string;
  amount: number;
  currency: string;
  status: PaymentStatus;
  paymentMethod: PaymentMethod;
  createdAt: string;
  updatedAt: string;
  externalTransactionId?: string;
}

export interface ErrorResponse {
  message: string;
  code?: string;
  details?: Record<string, any>;
  timestamp?: string;
}

export interface PaymentState {
  isLoading: boolean;
  error: string | null;
}

export interface CashPaymentConfirmation {
  amountReceived: string;
  staffId: string;
}

export interface CashPaymentCancellation {
  reason: string;
  staffId: string;
} 