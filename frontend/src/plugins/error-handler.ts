import { defineNuxtPlugin } from '#app'

export default defineNuxtPlugin((nuxtApp) => {
  // Global error handler
  nuxtApp.vueApp.config.errorHandler = (error, instance, info) => {
    // Log error to console
    console.error('Global Vue Error:', error)
    console.error('Error Info:', info)

    // Send error to monitoring service (e.g., Sentry)
    // This is a placeholder - replace with actual error tracking
    if (process.client) {
      // Client-side error tracking
      const errorTrackingService = useErrorTrackingService()
      errorTrackingService.captureException(error)
    }
  }

  // Unhandled promise rejection handler
  if (process.client) {
    window.addEventListener('unhandledrejection', (event) => {
      console.error('Unhandled Promise Rejection:', event.reason)
      
      // Prevent default error logging
      event.preventDefault()

      // Optional: Show user-friendly error notification
      useToast().error('An unexpected error occurred. Please try again.')
    })
  }
})

// Error Tracking Service (Mock implementation)
function useErrorTrackingService() {
  return {
    captureException: (error: any) => {
      // Placeholder for error tracking service
      // In production, replace with actual error tracking (e.g., Sentry)
      console.warn('Error captured:', error)
    },
    captureMessage: (message: string) => {
      console.warn('Error message:', message)
    }
  }
}

// Toast Notification Service
function useToast() {
  return {
    success: (message: string) => {
      // Implement toast notification
      console.log('Success:', message)
    },
    error: (message: string) => {
      // Implement toast notification
      console.error('Error:', message)
    },
    warning: (message: string) => {
      // Implement toast notification
      console.warn('Warning:', message)
    }
  }
}
