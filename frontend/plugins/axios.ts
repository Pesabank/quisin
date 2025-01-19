import axios from 'axios'

interface RuntimeConfig {
  public: {
    apiBaseUrl: string
  }
}

export default defineNuxtPlugin((nuxtApp) => {
  const config = useRuntimeConfig() as RuntimeConfig

  const axiosInstance = axios.create({
    baseURL: config.public.apiBaseUrl,
    timeout: 10000,
    headers: {
      'Content-Type': 'application/json'
    }
  })

  // Request interceptor
  axiosInstance.interceptors.request.use(
    (config) => {
      // Get token from storage
      const token = localStorage.getItem('token')
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      return config
    },
    (error) => {
      return Promise.reject(error)
    }
  )

  // Response interceptor
  axiosInstance.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config

      // Handle 401 Unauthorized
      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true

        try {
          // Attempt to refresh token
          const refreshToken = localStorage.getItem('refreshToken')
          if (refreshToken) {
            const response = await axios.post(`${config.public.apiBaseUrl}/api/v1/auth/refresh`, {
              refreshToken
            })

            const { token } = response.data
            localStorage.setItem('token', token)

            // Retry the original request
            originalRequest.headers.Authorization = `Bearer ${token}`
            return axiosInstance(originalRequest)
          }
        } catch (error) {
          // Clear tokens and redirect to login
          localStorage.removeItem('token')
          localStorage.removeItem('refreshToken')
          navigateTo('/auth/sign-in')
        }
      }

      // If token is expired or invalid, redirect to login
      if (error.response?.status === 401) {
        navigateTo('/auth/sign-in')
      }

      return Promise.reject(error)
    }
  )

  return {
    provide: {
      axios: axiosInstance
    }
  }
}) 