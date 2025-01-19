import { config } from '@vue/test-utils'
import { vi } from 'vitest'
import type { NavigationFailure, RouteLocationRaw } from 'vue-router'

// Mock ResizeObserver
class ResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}

global.ResizeObserver = ResizeObserver

// Mock Nuxt's useRouter
vi.mock('#app', () => ({
  useRouter: () => ({
    push: vi.fn((to: RouteLocationRaw) => Promise.resolve({} as NavigationFailure | void | undefined)),
    replace: vi.fn((to: RouteLocationRaw) => Promise.resolve({} as NavigationFailure | void | undefined)),
    go: vi.fn((delta: number) => Promise.resolve()),
    back: vi.fn(() => Promise.resolve()),
    forward: vi.fn(() => Promise.resolve())
  }),
  useRoute: () => ({
    path: '/',
    query: {},
    params: {}
  }),
  defineNuxtPlugin: (fn: any) => fn,
  useRuntimeConfig: () => ({
    public: {
      apiBaseUrl: 'http://localhost:8089'
    }
  })
}))

// Global mocks
config.global.mocks = {
  $t: (key: string) => key,
  $toast: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn(),
    info: vi.fn()
  }
}

// Global stubs
config.global.stubs = {
  NuxtLink: true,
  ClientOnly: true
} 