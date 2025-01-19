declare global {
  interface Window {
    paypal: {
      Buttons: (config: PayPalButtonsConfig) => PayPalButtons
    }
  }
}

interface PayPalButtons {
  render: (container: string | HTMLElement) => Promise<void>
  createOrder: () => Promise<string>
  onApprove: (data: PayPalOnApproveData) => Promise<void>
  onCancel: () => void
}

interface PayPalButtonsConfig {
  createOrder?: () => Promise<string>
  onApprove?: (data: PayPalOnApproveData) => Promise<void>
  onCancel?: () => void
}

interface PayPalOnApproveData {
  orderID: string
}

export {} 