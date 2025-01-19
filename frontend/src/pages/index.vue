<template>
  <div>
    <!-- Hero Section -->
    <section class="relative min-h-[80vh] bg-gradient-to-br from-gray-900 to-gray-800 pt-16 pb-20 px-4 sm:px-6 lg:pt-24 lg:pb-28 lg:px-8 overflow-hidden">
      <!-- Background Pattern -->
      <div class="absolute inset-0 opacity-10">
        <div class="absolute inset-0" style="background-image: url('data:image/svg+xml,%3Csvg width=\'60\' height=\'60\' viewBox=\'0 0 60 60\' xmlns=\'http://www.w3.org/2000/svg\'%3E%3Cg fill=\'none\' fill-rule=\'evenodd\'%3E%3Cg fill=\'%23ffffff\' fill-opacity=\'0.4\'%3E%3Cpath d=\'M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z\'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E')"></div>
      </div>
      
      <div class="relative max-w-7xl mx-auto">
        <div class="text-center">
          <h1 class="text-4xl tracking-tight font-extrabold text-white sm:text-5xl md:text-6xl">
            <span class="block mb-2">Welcome to</span>
            <span class="block text-[#FF6B00] font-serif italic">Quisin</span>
            <span class="block text-2xl sm:text-3xl md:text-4xl mt-4 font-light">Modern Restaurant Management System</span>
              </h1>
          <p class="mt-6 max-w-md mx-auto text-xl text-gray-300 sm:text-2xl md:mt-8 md:max-w-3xl">
            Elevate your dining experience with our comprehensive management solution.
              </p>
          <div class="mt-8 max-w-md mx-auto sm:flex sm:justify-center md:mt-12">
                <div class="rounded-md shadow">
                  <NuxtLink
                    to="/get-started"
                class="w-full flex items-center justify-center px-8 py-4 border border-transparent text-lg font-medium rounded-md text-white bg-[#FF6B00] hover:bg-[#e66000] transform hover:scale-105 transition-all duration-200 ease-in-out"
                  >
                    Get Started
                  </NuxtLink>
                </div>
            <div class="mt-4 rounded-md shadow sm:mt-0 sm:ml-4">
                  <NuxtLink
                    to="/about"
                class="w-full flex items-center justify-center px-8 py-4 border-2 border-[#FF6B00] text-lg font-medium rounded-md text-[#FF6B00] bg-transparent hover:bg-[#FF6B00] hover:text-white transform hover:scale-105 transition-all duration-200 ease-in-out"
                  >
                    Learn More
                  </NuxtLink>
                </div>
              </div>
            </div>
        </div>
    </section>

    <!-- Features Section -->
    <section class="py-24 bg-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-16">
          <h2 class="text-3xl font-extrabold text-gray-900 sm:text-4xl">
            Powerful Features for Modern Restaurants
          </h2>
          <p class="mt-4 text-xl text-gray-600">
            Everything you need to manage your restaurant efficiently
          </p>
        </div>
        <div class="grid grid-cols-1 gap-8 md:grid-cols-3">
          <!-- Feature 1 -->
          <div class="p-8 bg-gray-50 rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300">
            <div class="w-12 h-12 bg-[#FF6B00] rounded-lg flex items-center justify-center mb-6">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
              </svg>
                </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-4">Order Management</h3>
            <p class="text-gray-600 leading-relaxed">
              Efficiently manage orders with real-time updates and seamless communication between kitchen and service staff.
          </p>
        </div>
          <!-- Feature 2 -->
          <div class="p-8 bg-gray-50 rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300">
            <div class="w-12 h-12 bg-[#FF6B00] rounded-lg flex items-center justify-center mb-6">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M3 14h18m-9-4v8m-7 0h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z"></path>
              </svg>
                </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-4">Table Management</h3>
            <p class="text-gray-600 leading-relaxed">
              Optimize table assignments, track reservations, and manage seating arrangements with our intuitive interface.
            </p>
              </div>
          <!-- Feature 3 -->
          <div class="p-8 bg-gray-50 rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300">
            <div class="w-12 h-12 bg-[#FF6B00] rounded-lg flex items-center justify-center mb-6">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z"></path>
              </svg>
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-4">Payment Processing</h3>
            <p class="text-gray-600 leading-relaxed">
              Handle payments securely with multiple payment options and automated billing system integration.
            </p>
              </div>
            </div>
          </div>
    </section>

    <!-- Call to Action Section -->
    <section class="bg-[#FF6B00] py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center">
          <h2 class="text-3xl font-extrabold text-white sm:text-4xl">
            Ready to Transform Your Restaurant?
          </h2>
          <p class="mt-4 text-xl text-white opacity-90">
            Join thousands of restaurants already using Quisin
          </p>
          <div class="mt-8">
            <NuxtLink
              to="/get-started"
              class="inline-flex items-center justify-center px-8 py-4 border border-transparent text-lg font-medium rounded-md text-[#FF6B00] bg-white hover:bg-gray-100 transform hover:scale-105 transition-all duration-200 ease-in-out"
            >
              Start Free Trial
            </NuxtLink>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'default',
  auth: false
})
</script> 

<style scoped>
.bg-gradient-to-br {
  background-size: 400% 400%;
  animation: gradient 15s ease infinite;
}

@keyframes gradient {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}
</style> 