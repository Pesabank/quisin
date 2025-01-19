&lt;template&gt;
  &lt;div class="min-h-screen bg-gray-100"&gt;
    &lt;nav class="bg-white border-b border-gray-200"&gt;
      &lt;div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8"&gt;
        &lt;div class="flex justify-between h-16"&gt;
          &lt;div class="flex"&gt;
            &lt;div class="flex-shrink-0 flex items-center"&gt;
              &lt;img class="h-8 w-auto" src="~/assets/logo.svg" alt="Quisin" /&gt;
            &lt;/div&gt;
            &lt;div class="hidden sm:ml-6 sm:flex sm:space-x-8"&gt;
              &lt;!-- Navigation Links --&gt;
              &lt;template v-for="item in navigationItems" :key="item.name"&gt;
                &lt;NuxtLink
                  :to="item.href"
                  :class="[
                    $route.path === item.href
                      ? 'border-blue-500 text-gray-900'
                      : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700',
                    'inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium'
                  ]"
                &gt;
                  {{ item.name }}
                &lt;/NuxtLink&gt;
              &lt;/template&gt;
            &lt;/div&gt;
          &lt;/div&gt;
          
          &lt;div class="flex items-center"&gt;
            &lt;!-- User Dropdown --&gt;
            &lt;div class="ml-3 relative"&gt;
              &lt;BaseDropdown&gt;
                &lt;template #trigger&gt;
                  &lt;button class="flex items-center space-x-2 text-sm focus:outline-none"&gt;
                    &lt;div class="h-8 w-8 rounded-full bg-gray-200 flex items-center justify-center"&gt;
                      {{ userInitials }}
                    &lt;/div&gt;
                    &lt;span class="hidden md:block"&gt;{{ userName }}&lt;/span&gt;
                  &lt;/button&gt;
                &lt;/template&gt;
                
                &lt;template #content&gt;
                  &lt;div class="py-1"&gt;
                    &lt;DropdownItem @click="navigateToProfile"&gt;Profile&lt;/DropdownItem&gt;
                    &lt;DropdownItem @click="navigateToSettings"&gt;Settings&lt;/DropdownItem&gt;
                    &lt;div class="border-t border-gray-100"&gt;
                      &lt;DropdownItem @click="handleLogout" class="text-red-600"&gt;
                        Sign out
                      &lt;/DropdownItem&gt;
                    &lt;/div&gt;
                  &lt;/div&gt;
                &lt;/template&gt;
              &lt;/BaseDropdown&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/nav&gt;

    &lt;!-- Page Content --&gt;
    &lt;main class="py-10"&gt;
      &lt;div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8"&gt;
        &lt;slot /&gt;
      &lt;/div&gt;
    &lt;/main&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { computed } from 'vue'
import { useAuthStore } from '~/stores/auth'
import { useUserStore } from '~/stores/user'

const authStore = useAuthStore()
const userStore = useUserStore()

const userName = computed(() => userStore.fullName || 'User')
const userInitials = computed(() => userStore.userInitials || 'U')

// Navigation items based on user role
const navigationItems = computed(() => {
  const items = []
  const role = authStore.userRole

  switch (role) {
    case 'SUPERADMIN':
      items.push(
        { name: 'Dashboard', href: '/superadmin' },
        { name: 'Restaurants', href: '/superadmin/restaurants' },
        { name: 'Users', href: '/superadmin/users' }
      )
      break
    case 'RESTAURANT_ADMIN':
      items.push(
        { name: 'Dashboard', href: '/admin' },
        { name: 'Menu', href: '/admin/menu' },
        { name: 'Staff', href: '/admin/staff' },
        { name: 'Reports', href: '/admin/reports' }
      )
      break
    case 'KITCHEN_STAFF':
      items.push(
        { name: 'Orders', href: '/kitchen' },
        { name: 'Inventory', href: '/kitchen/inventory' }
      )
      break
    case 'WAITER':
      items.push(
        { name: 'Tables', href: '/waiter' },
        { name: 'Orders', href: '/waiter/orders' }
      )
      break
    case 'CUSTOMER':
      items.push(
        { name: 'Menu', href: '/customer' },
        { name: 'Orders', href: '/customer/orders' },
        { name: 'Reservations', href: '/customer/reservations' }
      )
      break
  }

  return items
})

// Navigation functions
const navigateToProfile = () => navigateTo('/profile')
const navigateToSettings = () => navigateTo('/settings')

const handleLogout = async () => {
  await authStore.logout()
  navigateTo('/auth/sign-in')
}
&lt;/script&gt;
