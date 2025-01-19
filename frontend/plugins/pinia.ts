import { defineNuxtPlugin } from '#app'
import { createPinia } from 'pinia'
import { markRaw } from 'vue'
import { useRouter } from 'vue-router'

export default defineNuxtPlugin((nuxtApp) => {
  const pinia = createPinia()
  
  // Add router to Pinia store instances
  pinia.use(({ store }) => {
    store.router = markRaw(useRouter())
  })

  nuxtApp.vueApp.use(pinia)
}) 