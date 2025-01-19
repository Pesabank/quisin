import { defineNuxtPlugin } from '#app'

export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig()

  return {
    provide: {
      apiBaseUrl: config.public.apiBaseUrl || 'http://localhost:8080/api'
    }
  }
}) 