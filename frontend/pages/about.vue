<template>
  <div class="min-h-screen bg-white">
    <!-- Hero Section -->
    <div class="relative bg-gray-900 py-16">
      <div class="absolute inset-0">
        <div class="absolute inset-0 bg-gradient-to-br from-[#FF6B00] to-gray-900 opacity-80"></div>
      </div>
      <div class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h1 class="text-4xl font-extrabold tracking-tight text-white sm:text-5xl lg:text-6xl">About Quisin</h1>
        <p class="mt-6 max-w-2xl mx-auto text-xl text-gray-200">
          Revolutionizing restaurant management with modern technology and exceptional service
        </p>
      </div>
    </div>

    <!-- Story Section -->
    <div class="py-16 bg-white overflow-hidden">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
          <div>
            <h2 class="text-3xl font-extrabold text-gray-900 sm:text-4xl">Our Story</h2>
            <p class="mt-4 text-lg text-gray-600">
              Founded in 2023, Quisin was born from a vision to transform how restaurants operate. We understand the challenges that restaurant owners, staff, and customers face in today's fast-paced dining environment.
            </p>
            <p class="mt-4 text-lg text-gray-600">
              Our platform combines cutting-edge technology with intuitive design to create a seamless restaurant management experience that benefits everyone involved in the dining journey.
            </p>
          </div>
          <div class="relative">
            <div class="aspect-w-16 aspect-h-9 rounded-lg shadow-xl overflow-hidden">
              <img src="https://placehold.co/800x450/FF6B00/FFFFFF.png?text=Restaurant+Management" alt="Restaurant Management" class="object-cover">
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Values Section -->
    <div class="bg-gray-50 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center">
          <h2 class="text-3xl font-extrabold text-gray-900 sm:text-4xl">Our Values</h2>
          <p class="mt-4 max-w-2xl mx-auto text-xl text-gray-600">
            The principles that guide everything we do
          </p>
        </div>

        <div class="mt-12 grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-3">
          <!-- Innovation -->
          <div class="bg-white rounded-lg shadow-lg overflow-hidden">
            <div class="p-6">
              <div class="w-12 h-12 bg-[#FF6B00] rounded-lg flex items-center justify-center mb-4">
                <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
              </div>
              <h3 class="text-xl font-semibold text-gray-900">Innovation</h3>
              <p class="mt-4 text-gray-600">
                We continuously push the boundaries of what's possible in restaurant management technology.
              </p>
            </div>
          </div>

          <!-- Excellence -->
          <div class="bg-white rounded-lg shadow-lg overflow-hidden">
            <div class="p-6">
              <div class="w-12 h-12 bg-[#FF6B00] rounded-lg flex items-center justify-center mb-4">
                <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z" />
                </svg>
              </div>
              <h3 class="text-xl font-semibold text-gray-900">Excellence</h3>
              <p class="mt-4 text-gray-600">
                We strive for excellence in every aspect of our service and support.
              </p>
            </div>
          </div>

          <!-- Customer Focus -->
          <div class="bg-white rounded-lg shadow-lg overflow-hidden">
            <div class="p-6">
              <div class="w-12 h-12 bg-[#FF6B00] rounded-lg flex items-center justify-center mb-4">
                <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
              </div>
              <h3 class="text-xl font-semibold text-gray-900">Customer Focus</h3>
              <p class="mt-4 text-gray-600">
                Our customers' success is our success. We're dedicated to providing exceptional support.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Team Section -->
    <div class="bg-white py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center">
          <h2 class="text-3xl font-extrabold text-gray-900 sm:text-4xl">Our Team</h2>
          <p class="mt-4 max-w-2xl mx-auto text-xl text-gray-600">
            Meet the people behind Quisin
          </p>
        </div>

        <div class="mt-12 grid grid-cols-1 gap-12 sm:grid-cols-2 lg:grid-cols-3">
          <!-- Team Member 1 -->
          <div class="text-center">
            <div class="relative">
              <div class="w-48 h-48 mx-auto rounded-full overflow-hidden bg-gray-200">
                <svg class="w-full h-full text-gray-400" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M24 20.993V24H0v-2.996A14.977 14.977 0 0112.004 15c4.904 0 9.26 2.354 11.996 5.993zM16.002 8.999a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
              </div>
            </div>
            <div class="mt-4">
              <h3 class="text-xl font-semibold text-gray-900">John Smith</h3>
              <p class="text-[#FF6B00]">CEO & Founder</p>
            </div>
          </div>

          <!-- Team Member 2 -->
          <div class="text-center">
            <div class="relative">
              <div class="w-48 h-48 mx-auto rounded-full overflow-hidden bg-gray-200">
                <svg class="w-full h-full text-gray-400" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M24 20.993V24H0v-2.996A14.977 14.977 0 0112.004 15c4.904 0 9.26 2.354 11.996 5.993zM16.002 8.999a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
              </div>
            </div>
            <div class="mt-4">
              <h3 class="text-xl font-semibold text-gray-900">Sarah Johnson</h3>
              <p class="text-[#FF6B00]">CTO</p>
            </div>
          </div>

          <!-- Team Member 3 -->
          <div class="text-center">
            <div class="relative">
              <div class="w-48 h-48 mx-auto rounded-full overflow-hidden bg-gray-200">
                <svg class="w-full h-full text-gray-400" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M24 20.993V24H0v-2.996A14.977 14.977 0 0112.004 15c4.904 0 9.26 2.354 11.996 5.993zM16.002 8.999a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
              </div>
            </div>
            <div class="mt-4">
              <h3 class="text-xl font-semibold text-gray-900">Michael Chen</h3>
              <p class="text-[#FF6B00]">Head of Product</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- CTA Section -->
    <div class="bg-[#FF6B00]">
      <div class="max-w-7xl mx-auto py-12 px-4 sm:px-6 lg:py-16 lg:px-8 lg:flex lg:items-center lg:justify-between">
        <h2 class="text-3xl font-extrabold tracking-tight text-white sm:text-4xl">
          <span class="block">Ready to get started?</span>
          <span class="block text-gray-900">Join us today and transform your restaurant.</span>
        </h2>
        <div class="mt-8 flex lg:mt-0 lg:flex-shrink-0">
          <div class="inline-flex rounded-md shadow">
            <NuxtLink to="/get-started" class="inline-flex items-center justify-center px-5 py-3 border border-transparent text-base font-medium rounded-md text-[#FF6B00] bg-white hover:bg-gray-50">
              Get Started
            </NuxtLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'default',
  auth: false
})
</script>

<style scoped>
.aspect-w-16 {
  position: relative;
  padding-bottom: 56.25%;
}

.aspect-w-16 > * {
  position: absolute;
  height: 100%;
  width: 100%;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
}
</style> 