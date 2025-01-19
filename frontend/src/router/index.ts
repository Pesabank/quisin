import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      redirect: '/admin'
    },
    // Admin routes
    {
      path: '/admin',
      name: 'admin',
      component: () => import('@/pages/admin/index.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/admin/orders',
      name: 'admin-orders',
      component: () => import('@/pages/admin/orders.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/admin/staff',
      name: 'admin-staff',
      component: () => import('@/pages/admin/staff.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/admin/reports',
      name: 'admin-reports',
      component: () => import('@/pages/admin/reports.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    // Waiter routes
    {
      path: '/waiter',
      name: 'waiter',
      component: () => import('@/pages/waiter/index.vue'),
      meta: { requiresAuth: true }
    },
    // Kitchen routes
    {
      path: '/kitchen',
      name: 'kitchen',
      component: () => import('@/pages/kitchen/index.vue'),
      meta: { requiresAuth: true }
    },
    // Menu routes
    {
      path: '/menu',
      name: 'menu',
      component: () => import('@/pages/menu/index.vue'),
      meta: { requiresAuth: true }
    },
    // Super Admin routes
    {
      path: '/superadmin',
      name: 'superadmin',
      component: () => import('@/pages/superadmin/index.vue'),
      meta: { requiresAuth: true, requiresSuperAdmin: true }
    },
    // Authentication routes
    {
      path: '/auth/sign-in',
      name: 'sign-in',
      component: () => import('@/pages/auth/sign-in.vue')
    },
    // Existing routes
    {
      path: '/analytics',
      name: 'analytics',
      component: () => import('@/views/Analytics.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/inventory',
      name: 'inventory',
      component: () => import('@/views/Inventory.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/settings',
      name: 'settings',
      component: () => import('@/views/Settings.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    // 404 route
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/pages/error/404.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/pages/login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/pages/register.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/admin/dashboard',
      name: 'admin-dashboard',
      component: () => import('@/pages/admin/dashboard.vue'),
      meta: { 
        requiresAuth: true,
        roles: ['SUPER_ADMIN']
      }
    },
    // Add other role-based routes...
  ]
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'sign-in' })
  } else if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next({ name: 'dashboard' })
  } else if (to.meta.requiresSuperAdmin && !authStore.isSuperAdmin) {
    next({ name: 'admin' })
  } else {
    next()
  }
})

export default router
