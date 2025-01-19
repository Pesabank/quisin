<template>
  <nav class="bg-[#1A1C25] shadow-md fixed w-full top-0 z-50 transition-all duration-200" :class="{ 'shadow-lg': scrolled }">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center h-20">
        <!-- Logo -->
        <div class="w-[200px]">
          <NuxtLink to="/" class="flex items-center space-x-3 group">
            <div class="w-11 h-11 bg-[#FF6B00] rounded-xl flex items-center justify-center transform transition-transform group-hover:rotate-12">
              <span class="text-2xl font-bold text-white">Q</span>
            </div>
            <span class="text-2xl font-bold text-white">
              <span class="text-[#FF6B00]">Quisin</span>
            </span>
          </NuxtLink>
        </div>

        <!-- Primary Navigation - Centered -->
        <div class="flex-1 flex justify-center">
          <div class="flex items-center space-x-6">
            <NuxtLink
              v-for="(item, index) in navigationItems"
              :key="index"
              :to="item.path"
              class="group relative px-6 py-2 rounded-full transition-all duration-300 text-[15px] font-medium"
              :class="[
                $route.path === item.path
                  ? 'text-white bg-[#FF6B00]'
                  : 'text-gray-400 hover:text-white'
              ]"
            >
              <span class="relative z-10">
                {{ item.name }}
              </span>
              <div 
                class="absolute inset-0 w-full h-full rounded-full bg-gray-800/50 transform scale-0 transition-transform duration-300 origin-center group-hover:scale-100"
                :class="{ 'hidden': $route.path === item.path }"
              ></div>
            </NuxtLink>
          </div>
        </div>

        <!-- Secondary Navigation - Right Aligned -->
        <div class="w-[200px] flex justify-end">
          <div class="flex items-center space-x-3">
            <NuxtLink
              to="/auth/sign-in"
              class="group relative inline-flex items-center px-4 py-2 text-[15px] font-medium bg-gray-800 text-white rounded-full transition-all duration-300 hover:bg-gray-700"
            >
              <span class="relative flex items-center">
                <UserIcon class="h-5 w-5 mr-1.5" />
                <span>Sign In</span>
              </span>
            </NuxtLink>
            
            <NuxtLink
              to="/get-started"
              class="group relative inline-flex items-center px-4 py-2 text-[15px] font-medium bg-[#FF6B00] text-white rounded-full transition-all duration-300 hover:bg-[#ff7b1a]"
            >
              <span class="relative flex items-center">
                <RocketLaunchIcon class="h-5 w-5 mr-1.5" />
                <span>Get Started</span>
              </span>
            </NuxtLink>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { 
  UserIcon,
  RocketLaunchIcon
} from '@heroicons/vue/24/outline'

const scrolled = ref(false)

const navigationItems = [
  { name: 'Home', path: '/' },
  { name: 'Reservation', path: '/reservation' },
  { name: 'About', path: '/about' },
  { name: 'Contact', path: '/contact' }
]

// Handle scroll events for navbar shadow
const handleScroll = () => {
  scrolled.value = window.scrollY > 0
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.router-link-active {
  @apply text-current;
}
</style> 