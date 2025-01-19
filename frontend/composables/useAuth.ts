import { useAuthStore } from '@/stores/auth'
import { navigateTo } from '#app'

export function useAuth() {
  const authStore = useAuthStore()

  const redirectToDashboard = () => {
    const role = authStore.userRole?.toLowerCase()
    switch (role) {
      case 'super_admin':
        return navigateTo('/superadmin/dashboard')
      case 'restaurant_admin':
        return navigateTo('/admin/dashboard')
      case 'waiter':
        return navigateTo('/waiter/dashboard')
      case 'kitchen_staff':
        return navigateTo('/kitchen/dashboard')
      default:
        return navigateTo('/dashboard')
    }
  }

  const checkAuth = () => {
    if (!authStore.isAuthenticated) {
      return navigateTo('/login')
    }
  }

  const checkRole = (allowedRoles: string[]) => {
    if (!authStore.userRole || !allowedRoles.includes(authStore.userRole)) {
      return navigateTo('/403')
    }
  }

  return {
    checkAuth,
    checkRole,
    redirectToDashboard
  }
} 