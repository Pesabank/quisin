import type { UseFetchOptions } from 'nuxt/app'
import { defu } from 'defu'

export function useApiClient() {
  const config = useRuntimeConfig()
  const authStore = useAuthStore()

  const defaultOptions: UseFetchOptions = {
    baseURL: config.public.apiBaseUrl,
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    },
    onRequest({ options }) {
      // Attach auth token if available
      if (authStore.token) {
        options.headers = {
          ...options.headers,
          'Authorization': `Bearer ${authStore.token}`
        }
      }
    },
    onResponseError({ response }) {
      // Handle unauthorized errors
      if (response.status === 401) {
        authStore.logout()
      }
    }
  }

  // Generic API methods
  return {
    get: (url: string, options = {}) => {
      const mergedOptions = defu(options, defaultOptions)
      return useFetch(url, { ...mergedOptions, method: 'GET' })
    },
    post: (url: string, body: any, options = {}) => {
      const mergedOptions = defu(options, defaultOptions)
      return useFetch(url, { 
        ...mergedOptions, 
        method: 'POST', 
        body 
      })
    },
    put: (url: string, body: any, options = {}) => {
      const mergedOptions = defu(options, defaultOptions)
      return useFetch(url, { 
        ...mergedOptions, 
        method: 'PUT', 
        body 
      })
    },
    delete: (url: string, options = {}) => {
      const mergedOptions = defu(options, defaultOptions)
      return useFetch(url, { 
        ...mergedOptions, 
        method: 'DELETE' 
      })
    }
  }
}

// Type for API response
export interface ApiResponse<T> {
  data: T | null
  error: Error | null
}
