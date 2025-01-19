import { useAuthStore } from '~/stores/auth'
import { storeToRefs } from 'pinia'
import { defineNuxtRouteMiddleware, navigateTo } from '#app'

export default defineNuxtRouteMiddleware((to) => {
  const authStore = useAuthStore()

  // Public routes that don't require authentication
  const publicRoutes = ['/', '/about', '/contact', '/menu', '/auth/sign-in']
  
  if (!publicRoutes.includes(to.path) && !authStore.isAuthenticated) {
    return navigateTo('/auth/sign-in')
  }
}) 