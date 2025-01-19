<template>
  <nav class="bg-white shadow-sm">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between h-16">
        <!-- Logo and primary nav -->
        <div class="flex">
          <div class="flex-shrink-0 flex items-center">
            <NuxtLink to="/" class="text-2xl font-bold text-[#FF6B00]">Quisin</NuxtLink>
          </div>
          <div class="hidden sm:ml-8 sm:flex sm:space-x-8">
            <NuxtLink
              to="/"
              class="inline-flex items-center px-1 pt-1 text-sm font-medium text-gray-900 border-b-2"
              :class="[$route.path === '/' ? 'border-[#FF6B00]' : 'border-transparent hover:border-gray-300']"
            >
              Home
            </NuxtLink>
            <NuxtLink
              to="/about"
              class="inline-flex items-center px-1 pt-1 text-sm font-medium text-gray-900 border-b-2"
              :class="[$route.path === '/about' ? 'border-[#FF6B00]' : 'border-transparent hover:border-gray-300']"
            >
              About
            </NuxtLink>
            <NuxtLink
              to="/reservation"
              class="inline-flex items-center px-1 pt-1 text-sm font-medium text-gray-900 border-b-2"
              :class="[$route.path === '/reservation' ? 'border-[#FF6B00]' : 'border-transparent hover:border-gray-300']"
            >
              Reservation
            </NuxtLink>
            <NuxtLink
              to="/contact"
              class="inline-flex items-center px-1 pt-1 text-sm font-medium text-gray-900 border-b-2"
              :class="[$route.path === '/contact' ? 'border-[#FF6B00]' : 'border-transparent hover:border-gray-300']"
            >
              Contact
            </NuxtLink>
          </div>
        </div>

        <!-- Secondary nav -->
        <div class="hidden sm:flex sm:items-center sm:space-x-4">
          <NuxtLink
            to="/auth/sign-in"
            class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-[#FF6B00] hover:bg-[#e66000] transition-colors duration-200"
          >
            Sign In
          </NuxtLink>
          <NuxtLink
            to="/get-started"
            class="inline-flex items-center px-4 py-2 border border-[#FF6B00] text-sm font-medium rounded-md text-[#FF6B00] hover:bg-orange-50 transition-colors duration-200"
          >
            Get Started
          </NuxtLink>
        </div>

        <!-- Mobile menu button -->
        <div class="flex items-center sm:hidden">
          <button
            @click="mobileMenuOpen = !mobileMenuOpen"
            class="inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100"
          >
            <span class="sr-only">Open main menu</span>
            <Bars3Icon v-if="!mobileMenuOpen" class="block h-6 w-6" />
            <XMarkIcon v-else class="block h-6 w-6" />
          </button>
        </div>
      </div>
    </div>

    <!-- Mobile menu -->
    <div v-show="mobileMenuOpen" class="sm:hidden">
      <div class="pt-2 pb-3 space-y-1">
        <NuxtLink
          to="/"
          class="block pl-3 pr-4 py-2 text-base font-medium border-l-4"
          :class="[$route.path === '/' ? 'border-[#FF6B00] text-[#FF6B00] bg-orange-50' : 'border-transparent text-gray-500 hover:bg-gray-50 hover:border-gray-300']"
        >
          Home
        </NuxtLink>
        <NuxtLink
          to="/about"
          class="block pl-3 pr-4 py-2 text-base font-medium border-l-4"
          :class="[$route.path === '/about' ? 'border-[#FF6B00] text-[#FF6B00] bg-orange-50' : 'border-transparent text-gray-500 hover:bg-gray-50 hover:border-gray-300']"
        >
          About
        </NuxtLink>
        <NuxtLink
          to="/reservation"
          class="block pl-3 pr-4 py-2 text-base font-medium border-l-4"
          :class="[$route.path === '/reservation' ? 'border-[#FF6B00] text-[#FF6B00] bg-orange-50' : 'border-transparent text-gray-500 hover:bg-gray-50 hover:border-gray-300']"
        >
          Reservation
        </NuxtLink>
        <NuxtLink
          to="/contact"
          class="block pl-3 pr-4 py-2 text-base font-medium border-l-4"
          :class="[$route.path === '/contact' ? 'border-[#FF6B00] text-[#FF6B00] bg-orange-50' : 'border-transparent text-gray-500 hover:bg-gray-50 hover:border-gray-300']"
        >
          Contact
        </NuxtLink>
      </div>
      <div class="pt-4 pb-3 border-t border-gray-200">
        <div class="space-y-1">
          <NuxtLink
            to="/auth/sign-in"
            class="block pl-3 pr-4 py-2 text-base font-medium text-gray-500 hover:text-gray-800 hover:bg-gray-50"
          >
            Sign In
          </NuxtLink>
          <NuxtLink
            to="/get-started"
            class="block pl-3 pr-4 py-2 text-base font-medium text-gray-500 hover:text-gray-800 hover:bg-gray-50"
          >
            Get Started
          </NuxtLink>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Bars3Icon, XMarkIcon } from '@heroicons/vue/24/outline'

const mobileMenuOpen = ref(false)
</script> 