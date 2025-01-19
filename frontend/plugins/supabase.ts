import { createClient } from '@supabase/supabase-js'

export default defineNuxtPlugin((nuxtApp) => {
  const config = useRuntimeConfig()

  const supabase = createClient(
    config.public.supabaseUrl,
    config.public.supabaseAnonKey,
    {
      auth: {
        persistSession: true,
        autoRefreshToken: true,
      }
    }
  )

  // For debugging
  console.log('Supabase Plugin: Initializing with URL:', config.public.supabaseUrl)

  return {
    provide: {
      supabase
    }
  }
}) 