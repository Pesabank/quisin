import { defineStore } from 'pinia'

interface User {
  id: string
  email: string
  role: 'superadmin' | 'admin' | 'waiter' | 'kitchen'
  name: string
}

interface LoginPayload {
  email: string
  password: string
  rememberMe: boolean
}

interface AuthState {
  user: User | null
  token: string | null
  isAuthenticated: boolean
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    user: null,
    token: null,
    isAuthenticated: false
  }),

  getters: {
    isSuperAdmin: (state) => state.user?.role === 'superadmin',
    isAdmin: (state) => state.user?.role === 'admin',
    isWaiter: (state) => state.user?.role === 'waiter',
    isKitchen: (state) => state.user?.role === 'kitchen'
  },

  actions: {
    async login(payload: LoginPayload) {
      try {
        // TODO: Replace with actual API call
        // Simulated API response for development
        const response = await new Promise<{ user: User; token: string }>((resolve) => {
          setTimeout(() => {
            resolve({
              user: {
                id: '1',
                email: payload.email,
                role: 'admin',
                name: 'John Doe'
              },
              token: 'dummy_jwt_token'
            })
          }, 1000)
        })

        this.user = response.user
        this.token = response.token
        this.isAuthenticated = true

        // Store token in localStorage if rememberMe is true
        if (payload.rememberMe) {
          localStorage.setItem('token', response.token)
        } else {
          sessionStorage.setItem('token', response.token)
        }

        return response
      } catch (error) {
        console.error('Login failed:', error)
        throw error
      }
    },

    async logout() {
      try {
        // TODO: Add API call to invalidate token if needed

        // Clear state
        this.user = null
        this.token = null
        this.isAuthenticated = false

        // Remove stored tokens
        localStorage.removeItem('token')
        sessionStorage.removeItem('token')
      } catch (error) {
        console.error('Logout failed:', error)
        throw error
      }
    },

    async checkAuth() {
      try {
        const token = localStorage.getItem('token') || sessionStorage.getItem('token')
        
        if (!token) {
          return false
        }

        // TODO: Add API call to validate token and get user info
        // For now, we'll simulate a successful response
        const response = await new Promise<{ user: User }>((resolve) => {
          setTimeout(() => {
            resolve({
              user: {
                id: '1',
                email: 'john@example.com',
                role: 'admin',
                name: 'John Doe'
              }
            })
          }, 500)
        })

        this.user = response.user
        this.token = token
        this.isAuthenticated = true

        return true
      } catch (error) {
        console.error('Auth check failed:', error)
        this.logout()
        return false
      }
    }
  }
}) 