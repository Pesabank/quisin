import { defineStore } from 'pinia'
import { User } from '@supabase/supabase-js'

interface AuthState {
  user: User | null
  isAuthenticated: boolean
  isInitialized: boolean
  isLoading: boolean
}

interface LoginCredentials {
  email: string
  password: string
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    user: null,
    isAuthenticated: false,
    isInitialized: false,
    isLoading: false
  }),

  getters: {
    currentUser: (state) => state.user,
    isLoggedIn: (state) => state.isAuthenticated,
    isSuperAdmin: (state) => state.user?.user_metadata?.role === 'superadmin',
    isAdmin: (state) => state.user?.user_metadata?.role === 'admin',
    isKitchenStaff: (state) => state.user?.user_metadata?.role === 'kitchen_staff',
    isWaiter: (state) => state.user?.user_metadata?.role === 'waiter',
    isCustomer: (state) => state.user?.user_metadata?.role === 'customer',
    isReady: (state) => state.isInitialized && !state.isLoading
  },

  actions: {
    setUser(user: User) {
      this.user = user
      this.isAuthenticated = true
      console.log('Auth Store: User set', { id: user.id, email: user.email, role: user.user_metadata?.role })
    },

    clearUser() {
      this.user = null
      this.isAuthenticated = false
      console.log('Auth Store: User cleared')
    },

    async login({ email, password }: LoginCredentials) {
      if (!this.isReady) {
        console.error('Auth Store: Not ready')
        throw new Error('Authentication service not ready')
      }

      this.isLoading = true
      
      try {
        const { $supabase } = useNuxtApp()
        
        if (!$supabase?.auth) {
          throw new Error('Authentication service not available')
        }

        console.log('Auth Store: Attempting login for', email)
        
        const { data, error } = await $supabase.auth.signInWithPassword({
          email,
          password
        })

        if (error) {
          console.error('Auth Store: Login error', error)
          throw error
        }

        if (data.user) {
          console.log('Auth Store: Login successful', { id: data.user.id, email: data.user.email })
          this.setUser(data.user)
          return data.user
        } else {
          console.error('Auth Store: No user data received')
          throw new Error('No user data received')
        }
      } catch (error: any) {
        console.error('Auth Store: Login error:', error.message)
        throw error
      } finally {
        this.isLoading = false
      }
    },

    async logout() {
      if (!this.isReady) {
        console.error('Auth Store: Not ready')
        throw new Error('Authentication service not ready')
      }

      this.isLoading = true
      
      try {
        const { $supabase } = useNuxtApp()
        
        if (!$supabase?.auth) {
          throw new Error('Authentication service not available')
        }

        console.log('Auth Store: Attempting logout')
        
        const { error } = await $supabase.auth.signOut()
        if (error) {
          console.error('Auth Store: Logout error', error)
          throw error
        }
        this.clearUser()
        console.log('Auth Store: Logout successful')
      } catch (error: any) {
        console.error('Auth Store: Logout error:', error.message)
        throw error
      } finally {
        this.isLoading = false
      }
    },

    async initializeAuth() {
      if (this.isInitialized) {
        return
      }

      this.isLoading = true
      
      try {
        const { $supabase } = useNuxtApp()
        
        if (!$supabase?.auth) {
          throw new Error('Authentication service not available')
        }

        console.log('Auth Store: Initializing auth')
        
        // Check for existing session
        const { data: { user }, error } = await $supabase.auth.getUser()
        
        if (error) {
          console.error('Auth Store: Get user error', error)
          throw error
        }

        if (user) {
          console.log('Auth Store: Found existing session', { id: user.id, email: user.email })
          this.setUser(user)
        } else {
          console.log('Auth Store: No existing session found')
          this.clearUser()
        }

        this.isInitialized = true
        console.log('Auth Store: Initialized successfully')
      } catch (error) {
        console.error('Auth Store: Initialization error:', error)
        this.clearUser()
        throw error
      } finally {
        this.isLoading = false
      }
    }
  }
}) 