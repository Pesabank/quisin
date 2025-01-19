import { vi } from 'vitest'
import { config } from '@vue/test-utils'

// Mock global Vue components and plugins
config.global.stubs = {
  // Stub out complex components
  RouterLink: true,
  RouterView: true
}

// Mock window and global objects
Object.defineProperty(window, 'localStorage', {
  value: {
    getItem: vi.fn(),
    setItem: vi.fn(),
    removeItem: vi.fn(),
    clear: vi.fn()
  },
  writable: true
})

// Mock Pinia stores
vi.mock('pinia', () => ({
  defineStore: vi.fn(),
  storeToRefs: vi.fn()
}))

// Mock API client
vi.mock('@/utils/api-client', () => ({
  useApiClient: vi.fn(() => ({
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn()
  }))
}))

// Global error handling mock
vi.mock('@/plugins/error-handler', () => ({
  useErrorTrackingService: vi.fn(() => ({
    captureException: vi.fn(),
    captureMessage: vi.fn()
  }))
}))
