import { cacheGet, cacheSet } from './cache'
import { logger } from '../utils/logger'

interface Currency {
  code: string
  symbol: string
  name: string
}

export const currencies: { [key: string]: Currency } = {
  USD: { code: 'USD', symbol: '$', name: 'US Dollar' },
  EUR: { code: 'EUR', symbol: '€', name: 'Euro' },
  GBP: { code: 'GBP', symbol: '£', name: 'British Pound' },
  JPY: { code: 'JPY', symbol: '¥', name: 'Japanese Yen' },
  AUD: { code: 'AUD', symbol: 'A$', name: 'Australian Dollar' },
  CAD: { code: 'CAD', symbol: 'C$', name: 'Canadian Dollar' },
  CHF: { code: 'CHF', symbol: 'Fr', name: 'Swiss Franc' },
  CNY: { code: 'CNY', symbol: '¥', name: 'Chinese Yuan' },
  INR: { code: 'INR', symbol: '₹', name: 'Indian Rupee' },
  KES: { code: 'KES', symbol: 'KSh', name: 'Kenyan Shilling' },
  NGN: { code: 'NGN', symbol: '₦', name: 'Nigerian Naira' },
  ZAR: { code: 'ZAR', symbol: 'R', name: 'South African Rand' }
}

export class CurrencyService {
  private static instance: CurrencyService
  private exchangeRatesCache: { [key: string]: number } = {}
  private readonly CACHE_TTL = 3600 // 1 hour

  private constructor() {}

  static getInstance(): CurrencyService {
    if (!CurrencyService.instance) {
      CurrencyService.instance = new CurrencyService()
    }
    return CurrencyService.instance
  }

  async getExchangeRate(from: string, to: string): Promise<number> {
    const cacheKey = `exchange:${from}:${to}`
    const cachedRate = await cacheGet(cacheKey)

    if (cachedRate) {
      return parseFloat(cachedRate)
    }

    try {
      // Use an exchange rate API (e.g., Open Exchange Rates, Fixer.io)
      const response = await fetch(
        `${process.env.EXCHANGE_RATE_API_URL}?base=${from}&symbols=${to}`,
        {
          headers: {
            'Authorization': `Bearer ${process.env.EXCHANGE_RATE_API_KEY}`
          }
        }
      )
      
      const data = await response.json()
      const rate = data.rates[to]

      await cacheSet(cacheKey, rate.toString(), this.CACHE_TTL)
      return rate
    } catch (error) {
      logger.error('Error fetching exchange rate:', error)
      return 1 // Default to 1:1 exchange rate in case of error
    }
  }

  async convertAmount(amount: number, from: string, to: string): Promise<number> {
    const rate = await this.getExchangeRate(from, to)
    return amount * rate
  }

  formatAmount(amount: number, currency: Currency): string {
    return `${currency.symbol}${amount.toFixed(2)}`
  }

  getAllCurrencies(): Currency[] {
    return Object.values(currencies)
  }

  getCurrency(code: string): Currency | undefined {
    return currencies[code]
  }

  validateCurrency(code: string): boolean {
    return code in currencies
  }
}
