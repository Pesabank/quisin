import { useAuthStore } from '~/stores/auth'
import { storeToRefs } from 'pinia'
import { computed } from 'vue'

export function useAuthState() {
  const authStore = useAuthStore()
  const { user, isAuthenticated, userRole } = storeToRefs(authStore)

  const isSuperAdmin = computed(() => userRole.value === 'SUPER_ADMIN')
  const isRestaurantAdmin = computed(() => userRole.value === 'RESTAURANT_ADMIN')
  const isWaiter = computed(() => userRole.value === 'WAITER')
  const isKitchenStaff = computed(() => userRole.value === 'KITCHEN_STAFF')

  const dashboardPath = computed(() => {
    switch (userRole.value?.toLowerCase()) {
      case 'super_admin':
        return '/superadmin/dashboard'
      case 'restaurant_admin':
        return '/admin/dashboard'
      case 'waiter':
        return '/waiter/dashboard'
      case 'kitchen_staff':
        return '/kitchen/dashboard'
      default:
        return '/dashboard'
    }
  })

  return {
    user,
    isAuthenticated,
    userRole,
    isSuperAdmin,
    isRestaurantAdmin,
    isWaiter,
    isKitchenStaff,
    dashboardPath
  }
} 