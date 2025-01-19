import { describe, it, expect } from 'vitest'
import { formatCurrency, validateCardNumber } from '../payment'

describe('Payment Utils', () => {
  describe('formatCurrency', () => {
    it('should format currency correctly', () => {
      expect(formatCurrency(1000)).toBe('$10.00')
      expect(formatCurrency(1999)).toBe('$19.99')
      expect(formatCurrency(0)).toBe('$0.00')
    })

    it('should handle negative values', () => {
      expect(formatCurrency(-1000)).toBe('-$10.00')
    })
  })

  describe('validateCardNumber', () => {
    it('should validate valid card numbers', () => {
      // Test Visa
      expect(validateCardNumber('4532015112830366')).toBe(true)
      // Test Mastercard
      expect(validateCardNumber('5425233430109903')).toBe(true)
      // Test Amex
      expect(validateCardNumber('374245455400126')).toBe(true)
    })

    it('should reject invalid card numbers', () => {
      expect(validateCardNumber('1234567890123456')).toBe(false)
      expect(validateCardNumber('invalid')).toBe(false)
      expect(validateCardNumber('')).toBe(false)
    })

    it('should handle card numbers with spaces and dashes', () => {
      expect(validateCardNumber('4532-0151-1283-0366')).toBe(true)
      expect(validateCardNumber('4532 0151 1283 0366')).toBe(true)
    })
  })
}) 