import { defineStore } from 'pinia'
import axios from 'axios'

interface LoyaltySettings {
  enabled: boolean
  pointsPerDollar: number
  tierThresholds: {
    silver: number
    gold: number
    platinum: number
  }
}

interface SettingsState {
  loyaltySettings: LoyaltySettings
  loading: boolean
  error: string | null
}

export const useSettingsStore = defineStore('settings', {
  state: (): SettingsState => ({
    loyaltySettings: {
      enabled: false,
      pointsPerDollar: 1,
      tierThresholds: {
        silver: 1000,
        gold: 5000,
        platinum: 10000
      }
    },
    loading: false,
    error: null
  }),

  getters: {
    isLoyaltyEnabled: (state) => state.loyaltySettings.enabled,
    getLoyaltySettings: (state) => state.loyaltySettings
  },

  actions: {
    async fetchSettings() {
      this.loading = true
      this.error = null
      try {
        const response = await axios.get('/api/settings/loyalty')
        this.loyaltySettings = response.data
      } catch (error) {
        this.error = 'Failed to fetch loyalty settings'
        console.error('Error fetching loyalty settings:', error)
      } finally {
        this.loading = false
      }
    },

    async updateLoyaltySettings(settings: Partial<LoyaltySettings>) {
      this.loading = true
      this.error = null
      try {
        const response = await axios.put('/api/settings/loyalty', settings)
        this.loyaltySettings = response.data
      } catch (error) {
        this.error = 'Failed to update loyalty settings'
        console.error('Error updating loyalty settings:', error)
      } finally {
        this.loading = false
      }
    },

    async toggleLoyaltyProgram(enabled: boolean) {
      await this.updateLoyaltySettings({ enabled })
    }
  }
})
