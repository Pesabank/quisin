// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  ssr: false,
  devtools: { enabled: true },
  modules: [
    '@pinia/nuxt',
    '@nuxtjs/tailwindcss'
  ],
  pinia: {
    autoImports: [
      'defineStore',
      'storeToRefs'
    ]
  },
  nitro: {
    devProxy: {
      '/api': {
        target: 'http://localhost:18081',
        changeOrigin: true,
        prependPath: false,
        secure: false,
        ws: true
      }
    }
  },
  imports: {
    dirs: ['stores']
  },
  runtimeConfig: {
    public: {
      apiBaseUrl: process.env.NUXT_PUBLIC_API_BASE_URL,
      supabaseUrl: process.env.NUXT_PUBLIC_SUPABASE_URL,
      supabaseAnonKey: process.env.NUXT_PUBLIC_SUPABASE_ANON_KEY
    }
  },
  app: {
    head: {
      title: 'Quisin',
      meta: [
        { charset: 'utf-8' },
        { name: 'viewport', content: 'width=device-width, initial-scale=1' },
        { 
          hid: 'description', 
          name: 'description', 
          content: 'Modern restaurant management system'
        }
      ],
      link: [
        { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }
      ]
    }
  },
  css: [
    '@/assets/css/main.css'
  ],
  components: {
    dirs: ['~/components']
  },
  postcss: {
    plugins: {
      tailwindcss: {},
      autoprefixer: {}
    }
  },
  experimental: {
    asyncContext: true
  },
  compatibilityDate: '2024-03-15'
})