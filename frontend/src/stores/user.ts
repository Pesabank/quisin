import { defineStore } from 'pinia'
import { useAuthStore } from './auth'

interface UserProfile {
  id: string
  username: string
  email: string
  firstName: string
  lastName: string
  phoneNumber?: string
  avatar?: string
  preferences: UserPreferences
}

interface UserPreferences {
  theme: 'light' | 'dark'
  language: string
  notifications: {
    email: boolean
    push: boolean
    sms: boolean
  }
}

export const useUserStore = defineStore('user', {
  state: () => ({
    profile: null as UserProfile | null,
    isLoading: false,
    error: null as string | null
  }),

  getters: {
    fullName(): string {
      if (!this.profile) return ''
      return `${this.profile.firstName} ${this.profile.lastName}`.trim()
    },

    userInitials(): string {
      if (!this.profile) return ''
      return `${this.profile.firstName.charAt(0)}${this.profile.lastName.charAt(0)}`.toUpperCase()
    }
  },

  actions: {
    async fetchUserProfile() {
      const authStore = useAuthStore()
      if (!authStore.isAuthenticated) return

      this.isLoading = true
      this.error = null

      try {
        const { data, error } = await useApiClient().get('/users/profile')
        
        if (error.value) {
          throw new Error(error.value.message || 'Failed to fetch user profile')
        }

        this.profile = data.value
      } catch (err) {
        this.error = err instanceof Error ? err.message : 'An error occurred'
        console.error('Error fetching user profile:', err)
      } finally {
        this.isLoading = false
      }
    },

    async updateProfile(updates: Partial<UserProfile>) {
      this.isLoading = true
      this.error = null

      try {
        const { data, error } = await useApiClient().patch('/users/profile', updates)

        if (error.value) {
          throw new Error(error.value.message || 'Failed to update profile')
        }

        this.profile = { ...this.profile, ...data.value }
      } catch (err) {
        this.error = err instanceof Error ? err.message : 'An error occurred'
        throw err
      } finally {
        this.isLoading = false
      }
    },

    async updatePreferences(preferences: Partial<UserPreferences>) {
      if (!this.profile) return

      try {
        const { data, error } = await useApiClient().patch('/users/preferences', preferences)

        if (error.value) {
          throw new Error(error.value.message || 'Failed to update preferences')
        }

        this.profile.preferences = { ...this.profile.preferences, ...data.value }
      } catch (err) {
        this.error = err instanceof Error ? err.message : 'An error occurred'
        throw err
      }
    },

    clearProfile() {
      this.profile = null
      this.error = null
    }
  }
})
