/**
 * Formats a number as currency
 * @param amount Amount in cents
 * @returns Formatted currency string
 */
export const formatCurrency = (amount: number): string => {
  const dollars = Math.abs(amount) / 100
  const sign = amount < 0 ? '-' : ''
  return `${sign}$${dollars.toFixed(2)}`
}

/**
 * Validates a credit card number using the Luhn algorithm
 * @param cardNumber Credit card number as string
 * @returns boolean indicating if the card number is valid
 */
export const validateCardNumber = (cardNumber: string): boolean => {
  const digits = cardNumber.replace(/\D/g, '')
  if (digits.length < 13 || digits.length > 19) return false

  let sum = 0
  let isEven = false

  for (let i = digits.length - 1; i >= 0; i--) {
    let digit = parseInt(digits[i])

    if (isEven) {
      digit *= 2
      if (digit > 9) {
        digit -= 9
      }
    }

    sum += digit
    isEven = !isEven
  }

  return sum % 10 === 0
} 