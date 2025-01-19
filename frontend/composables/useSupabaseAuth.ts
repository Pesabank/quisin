import { User } from '@supabase/supabase-js'
import { useAuthStore } from '~/stores/auth'

export const useSupabaseAuth = () => {
  const { $supabase } = useNuxtApp()
  const authStore = useAuthStore()
  const user = ref<User | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const signUp = async (email: string, password: string) => {
    try {
      loading.value = true
      error.value = null
      const { data, error: signUpError } = await $supabase.auth.signUp({
        email,
        password,
      })
      if (signUpError) throw signUpError
      if (data.user) {
        user.value = data.user
        authStore.setUser(data.user)
      }
    } catch (e: any) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  const signIn = async (email: string, password: string) => {
    try {
      loading.value = true
      error.value = null
      const { data, error: signInError } = await $supabase.auth.signInWithPassword({
        email,
        password,
      })
      if (signInError) throw signInError
      if (data.user) {
        user.value = data.user
        authStore.setUser(data.user)
      }
    } catch (e: any) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  const signOut = async () => {
    try {
      loading.value = true
      error.value = null
      const { error: signOutError } = await $supabase.auth.signOut()
      if (signOutError) throw signOutError
      user.value = null
      authStore.clearUser()
    } catch (e: any) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  const resetPassword = async (email: string) => {
    try {
      loading.value = true
      error.value = null
      const { error: resetError } = await $supabase.auth.resetPasswordForEmail(email)
      if (resetError) throw resetError
    } catch (e: any) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  return {
    user,
    loading,
    error,
    signUp,
    signIn,
    signOut,
    resetPassword
  }
} 