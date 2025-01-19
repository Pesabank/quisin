<template>
  <div class="min-h-screen bg-white py-16 sm:py-24">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center">
        <h2 class="text-base text-[#FF6B00] font-semibold tracking-wide uppercase">About Us</h2>
        <p class="mt-2 text-3xl font-extrabold text-gray-900 sm:text-4xl lg:text-5xl">
          Revolutionizing Restaurant Management
        </p>
        <p class="mt-4 max-w-2xl text-xl text-gray-500 lg:mx-auto">
          Empowering restaurants with modern technology to enhance customer experience and streamline operations.
        </p>
      </div>

      <div class="mt-20">
        <div class="grid grid-cols-1 gap-12 lg:grid-cols-3 lg:gap-8">
          <div class="space-y-4">
            <div class="flex items-center justify-center h-12 w-12 rounded-md bg-[#FF6B00] text-white mx-auto">
              <RocketLaunchIcon class="h-6 w-6" aria-hidden="true" />
            </div>
            <h3 class="text-2xl font-bold text-gray-900 text-center">Our Mission</h3>
            <p class="text-lg text-gray-500">
              To provide cutting-edge technology solutions that help restaurants thrive in the digital age while maintaining their unique identity and charm.
            </p>
          </div>

          <div class="space-y-4">
            <div class="flex items-center justify-center h-12 w-12 rounded-md bg-[#FF6B00] text-white mx-auto">
              <LightBulbIcon class="h-6 w-6" aria-hidden="true" />
            </div>
            <h3 class="text-2xl font-bold text-gray-900 text-center">Our Vision</h3>
            <p class="text-lg text-gray-500">
              To become the global standard for restaurant management systems, known for innovation, reliability, and exceptional customer service.
            </p>
          </div>

          <div class="space-y-4">
            <div class="flex items-center justify-center h-12 w-12 rounded-md bg-[#FF6B00] text-white mx-auto">
              <HeartIcon class="h-6 w-6" aria-hidden="true" />
            </div>
            <h3 class="text-2xl font-bold text-gray-900 text-center">Our Values</h3>
            <p class="text-lg text-gray-500">
              Excellence, innovation, integrity, and customer satisfaction are at the heart of everything we do.
            </p>
          </div>
        </div>
      </div>

      <div class="mt-20">
        <div class="bg-gray-50 overflow-hidden rounded-2xl shadow-xl">
          <div class="grid grid-cols-1 lg:grid-cols-2">
            <div class="relative">
              <img
                class="h-full w-full object-cover"
                src="https://images.unsplash.com/photo-1600565193348-f74bd3c7ccdf?ixlib=rb-4.0.3"
                alt="Restaurant team"
              />
            </div>
            <div class="p-8 sm:p-12">
              <h3 class="text-2xl font-bold text-gray-900 sm:text-3xl">Why Choose Quisin?</h3>
              <div class="mt-8 space-y-6">
                <div class="flex">
                  <CheckCircleIcon class="h-6 w-6 text-[#FF6B00]" />
                  <span class="ml-3 text-gray-500">Comprehensive management tools</span>
                </div>
                <div class="flex">
                  <CheckCircleIcon class="h-6 w-6 text-[#FF6B00]" />
                  <span class="ml-3 text-gray-500">Real-time analytics and reporting</span>
                </div>
                <div class="flex">
                  <CheckCircleIcon class="h-6 w-6 text-[#FF6B00]" />
                  <span class="ml-3 text-gray-500">Seamless reservation system</span>
                </div>
                <div class="flex">
                  <CheckCircleIcon class="h-6 w-6 text-[#FF6B00]" />
                  <span class="ml-3 text-gray-500">24/7 customer support</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  RocketLaunchIcon,
  LightBulbIcon,
  HeartIcon,
  CheckCircleIcon
} from '@heroicons/vue/24/outline'
</script> 