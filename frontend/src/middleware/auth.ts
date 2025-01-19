import { UserRole } from '~/src/stores/auth'

export default defineNuxtRouteMiddleware((to) => {
  const authStore = useAuthStore()

  // Public routes that don't require authentication
  const publicRoutes = ['/auth/sign-in', '/auth/register']

  // Check if the route is public
  if (publicRoutes.includes(to.path)) {
    return
  }

  // Redirect to login if not authenticated
  if (!authStore.isAuthenticated) {
    return navigateTo('/auth/sign-in')
  }

  // Role-based route protection
  const roleRequiredForRoute: Record<string, UserRole[]> = {
    '/superadmin': ['SUPERADMIN'],
    '/admin': ['RESTAURANT_ADMIN'],
    '/kitchen': ['KITCHEN_STAFF'],
    '/waiter': ['WAITER'],
    '/customer': ['CUSTOMER']
  }

  // Check route-specific role requirements
  for (const [routePrefix, allowedRoles] of Object.entries(roleRequiredForRoute)) {
    if (to.path.startsWith(routePrefix)) {
      if (!allowedRoles.includes(authStore.userRole!)) {
        // Redirect to unauthorized page or dashboard based on user's role
        authStore.redirectAfterLogin()
        return
      }
    }
  }
})
