import { createClient } from '@supabase/supabase-js'
import { useAuthStore } from '~/stores/auth'

export default defineNuxtPlugin(async (nuxtApp) => {
  console.log('App Plugin: Starting initialization')

  // Only proceed with auth setup on client-side
  if (!process.client) {
    console.log('App Plugin: Skipping auth setup on server-side')
    return
  }

  const config = useRuntimeConfig()
  console.log('App Plugin: Runtime config loaded', {
    hasUrl: !!config.public.supabaseUrl,
    hasKey: !!config.public.supabaseAnonKey
  })

  if (!config.public.supabaseUrl || !config.public.supabaseAnonKey) {
    console.error('App Plugin: Missing Supabase configuration')
    return
  }

  const supabase = createClient(
    config.public.supabaseUrl,
    config.public.supabaseAnonKey,
    {
      auth: {
        persistSession: true,
        autoRefreshToken: true,
        detectSessionInUrl: true,
        storage: process.client ? localStorage : undefined
      }
    }
  )

  // Provide Supabase client immediately
  nuxtApp.provide('supabase', supabase)
  console.log('App Plugin: Supabase client provided')

  if (process.client) {
    try {
      // Initialize Auth Store
      const authStore = useAuthStore()
      console.log('App Plugin: Auth store accessed')

      // Set up auth state change listener
      const { data: { subscription } } = supabase.auth.onAuthStateChange(async (event, session) => {
        console.log('App Plugin: Auth state change', { event, userId: session?.user?.id })
        if (event === 'SIGNED_IN' && session?.user) {
          authStore.setUser(session.user)
        } else if (event === 'SIGNED_OUT') {
          authStore.clearUser()
        }
      })

      // Check for existing session
      const { data: { session } } = await supabase.auth.getSession()
      console.log('App Plugin: Session check -', session ? 'Found existing session' : 'No session found')
      
      if (session?.user) {
        authStore.setUser(session.user)
      }
      
      authStore.isInitialized = true
      console.log('App Plugin: Auth store initialized')

      // Cleanup on app unmount
      nuxtApp.hook('app:beforeMount', () => {
        subscription?.unsubscribe()
      })

    } catch (error) {
      console.error('App Plugin: Auth initialization error:', error)
      const authStore = useAuthStore()
      authStore.isInitialized = true // Mark as initialized even on error
    }
  }

  console.log('App Plugin: Setup complete')
}) 