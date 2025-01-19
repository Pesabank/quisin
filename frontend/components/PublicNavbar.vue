<template>
  <nav class="bg-white shadow-lg">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between h-16">
        <div class="flex">
          <!-- Logo -->
          <div class="flex-shrink-0 flex items-center">
            <NuxtLink to="/" class="text-2xl font-bold text-[#FF6B00]">
              Quisin
            </NuxtLink>
          </div>

          <!-- Navigation Links -->
          <div class="hidden sm:ml-6 sm:flex sm:space-x-8">
            <NuxtLink
              v-for="item in navigationItems"
              :key="item.path"
              :to="item.path"
              class="nav-link"
              active-class="active-nav-link"
            >
              {{ item.name }}
            </NuxtLink>
          </div>
        </div>

        <!-- Right side -->
        <div class="flex items-center space-x-4">
          <NuxtLink
            to="/auth/sign-in"
            class="text-gray-500 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium"
          >
            Sign In
          </NuxtLink>
          <NuxtLink
            to="/get-started"
            class="bg-[#FF6B00] text-white hover:bg-[#e66000] px-4 py-2 rounded-md text-sm font-medium"
          >
            Get Started
          </NuxtLink>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
const navigationItems = [
  { name: 'Home', path: '/' },
  { name: 'Menu', path: '/menu' },
  { name: 'Reservation', path: '/reservation' },
  { name: 'About', path: '/about' },
  { name: 'Contact', path: '/contact' }
]
</script>

<style scoped>
.nav-link {
  @apply inline-flex items-center px-1 pt-1 text-sm font-medium text-gray-500 hover:text-gray-900;
  @apply border-b-2 border-transparent hover:border-gray-300;
}

.active-nav-link {
  @apply border-[#FF6B00] text-gray-900;
}
</style> 