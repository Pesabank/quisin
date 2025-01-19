import { useAuthStore } from '~/stores/auth'

// List of public routes that don't require authentication
const publicRoutes = [
  '/',
  '/about',
  '/menu',
  '/reservation',
  '/contact',
  '/auth/sign-in',
  '/auth/sign-up',
  '/auth/forgot-password'
]

// List of protected routes and their required roles
const protectedRoutes = {
  '/superadmin': 'superadmin',
  '/admin': 'admin',
  '/kitchen': 'kitchen_staff',
  '/waiter': 'waiter',
  '/customer': 'customer'
}

export default defineNuxtRouteMiddleware((to) => {
  // Skip middleware on server-side
  if (process.server) return

  // Skip middleware for routes that explicitly disable it
  if (to.meta?.middleware === false) {
    console.log('Auth Middleware: Skipped for route', to.path)
    return
  }

  const authStore = useAuthStore()
  
  // Check if the route is public
  const isPublicRoute = publicRoutes.includes(to.path)

  console.log('Auth Middleware:', {
    path: to.path,
    isPublicRoute,
    isLoggedIn: authStore.isLoggedIn,
    meta: to.meta,
    publicRoutes
  })

  if (isPublicRoute) {
    console.log('Public route access granted:', to.path)
    return
  }

  // Check if user is authenticated for protected routes
  if (!authStore.isLoggedIn) {
    console.log('Not logged in, redirecting to sign-in from:', to.path)
    return navigateTo('/auth/sign-in')
  }

  // Check role-based access for protected routes
  for (const [routePrefix, requiredRole] of Object.entries(protectedRoutes)) {
    if (to.path.startsWith(routePrefix)) {
      const hasRole = authStore.user?.user_metadata?.role === requiredRole
      if (!hasRole) {
        console.warn(`Access denied: User does not have required role ${requiredRole}`)
        return navigateTo('/')
      }
    }
  }
}) 