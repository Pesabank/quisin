<template>
  <div class="min-h-screen bg-gradient-to-b from-gray-50 to-white">
    <!-- Hero Section -->
    <div class="relative overflow-hidden">
      <div class="absolute inset-0">
        <img
          src="https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1470&q=80"
          alt="Restaurant Background"
          class="w-full h-full object-cover"
        />
        <div class="absolute inset-0 bg-gradient-to-r from-gray-900/90 to-gray-900/50"></div>
      </div>
      <div class="relative max-w-7xl mx-auto py-24 px-4 sm:py-32 sm:px-6 lg:px-8">
        <h1 class="text-4xl font-extrabold tracking-tight text-white sm:text-5xl lg:text-6xl">
          Getting Started with <span class="text-[#FF6B00]">Quisin</span>
        </h1>
        <p class="mt-6 max-w-3xl text-xl text-gray-300">
          Follow these simple steps to get your restaurant up and running with our comprehensive management system.
        </p>
      </div>
    </div>

    <!-- Steps Section -->
    <div class="max-w-7xl mx-auto py-16 px-4 sm:px-6 lg:px-8">
      <div class="relative">
        <!-- Vertical Line -->
        <div class="hidden lg:block absolute left-1/2 top-0 bottom-0 w-0.5 bg-gray-200"></div>

        <!-- Steps -->
        <div class="space-y-16">
          <!-- Step 1 -->
          <div class="relative">
            <div class="lg:flex lg:items-center lg:space-x-16">
              <div class="lg:w-1/2 lg:text-right">
                <div class="bg-white rounded-2xl shadow-xl p-8 lg:mr-8 transform transition-all duration-300 hover:scale-105 hover:shadow-2xl">
                  <div class="absolute lg:right-0 lg:translate-x-1/2 -top-4 w-12 h-12 bg-[#FF6B00] rounded-full flex items-center justify-center text-white font-bold text-xl shadow-lg z-10">
                    1
                  </div>
                  <h3 class="text-2xl font-bold text-gray-900 mt-2 mb-4">Sign Up</h3>
                  <div class="text-gray-500 space-y-4">
                    <p>Contact our sales team to create your restaurant's account. We'll set you up with:</p>
                    <ul class="space-y-3">
                      <li class="flex items-center">
                        <CheckCircleIcon class="h-5 w-5 text-[#FF6B00] mr-2 flex-shrink-0" />
                        <span>Admin account credentials</span>
                      </li>
                      <li class="flex items-center">
                        <CheckCircleIcon class="h-5 w-5 text-[#FF6B00] mr-2 flex-shrink-0" />
                        <span>Initial system configuration</span>
                      </li>
                      <li class="flex items-center">
                        <CheckCircleIcon class="h-5 w-5 text-[#FF6B00] mr-2 flex-shrink-0" />
                        <span>Restaurant profile setup</span>
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
              <div class="hidden lg:block lg:w-1/2"></div>
            </div>
          </div>

          <!-- Step 2 -->
          <div class="relative">
            <div class="lg:flex lg:items-center lg:space-x-16">
              <div class="hidden lg:block lg:w-1/2"></div>
              <div class="lg:w-1/2">
                <div class="bg-white rounded-2xl shadow-xl p-8 lg:ml-8 transform transition-all duration-300 hover:scale-105 hover:shadow-2xl">
                  <div class="absolute lg:left-0 lg:-translate-x-1/2 -top-4 w-12 h-12 bg-[#FF6B00] rounded-full flex items-center justify-center text-white font-bold text-xl shadow-lg z-10">
                    2
                  </div>
                  <h3 class="text-2xl font-bold text-gray-900 mt-2 mb-4">Configure Your Restaurant</h3>
                  <div class="text-gray-500 space-y-4">
                    <p>Set up your restaurant's basic information and operational settings:</p>
                    <ul class="space-y-3">
                      <li class="flex items-center">
                        <Cog6ToothIcon class="h-5 w-5 text-[#FF6B00] mr-2 flex-shrink-0" />
                        <span>Menu items and categories</span>
                      </li>
                      <li class="flex items-center">
                        <TableCellsIcon class="h-5 w-5 text-[#FF6B00] mr-2 flex-shrink-0" />
                        <span>Table layout and capacity</span>
                      </li>
                      <li class="flex items-center">
                        <UserGroupIcon class="h-5 w-5 text-[#FF6B00] mr-2 flex-shrink-0" />
                        <span>Staff roles and permissions</span>
                      </li>
                      <li class="flex items-center">
                        <ClockIcon class="h-5 w-5 text-[#FF6B00] mr-2 flex-shrink-0" />
                        <span>Operating hours</span>
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Step 3 -->
          <div class="relative">
            <div class="lg:flex lg:items-center lg:space-x-16">
              <div class="lg:w-1/2 lg:text-right">
                <div class="bg-white rounded-2xl shadow-xl p-8 lg:mr-8 transform transition-all duration-300 hover:scale-105 hover:shadow-2xl">
                  <div class="absolute lg:right-0 lg:translate-x-1/2 -top-4 w-12 h-12 bg-[#FF6B00] rounded-full flex items-center justify-center text-white font-bold text-xl shadow-lg z-10">
                    3
                  </div>
                  <h3 class="text-2xl font-bold text-gray-900 mt-2 mb-4">Train Your Staff</h3>
                  <div class="text-gray-500 space-y-4">
                    <p>Get your team familiar with the system:</p>
                    <ul class="space-y-3">
                      <li class="flex items-center">
                        <KeyIcon class="h-5 w-5 text-[#FF6B00] mr-2 flex-shrink-0" />
                        <span>Provide login credentials to staff members</span>
                      </li>
                      <li class="flex items-center">
                        <ClipboardDocumentListIcon class="h-5 w-5 text-[#FF6B00] mr-2 flex-shrink-0" />
                        <span>Train on order management</span>
                      </li>
                      <li class="flex items-center">
                        <ComputerDesktopIcon class="h-5 w-5 text-[#FF6B00] mr-2 flex-shrink-0" />
                        <span>Review kitchen display system</span>
                      </li>
                      <li class="flex items-center">
                        <TableCellsIcon class="h-5 w-5 text-[#FF6B00] mr-2 flex-shrink-0" />
                        <span>Practice table management</span>
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
              <div class="hidden lg:block lg:w-1/2"></div>
            </div>
          </div>

          <!-- Step 4 -->
          <div class="relative">
            <div class="lg:flex lg:items-center lg:space-x-16">
              <div class="hidden lg:block lg:w-1/2"></div>
              <div class="lg:w-1/2">
                <div class="bg-white rounded-2xl shadow-xl p-8 lg:ml-8 transform transition-all duration-300 hover:scale-105 hover:shadow-2xl">
                  <div class="absolute lg:left-0 lg:-translate-x-1/2 -top-4 w-12 h-12 bg-[#FF6B00] rounded-full flex items-center justify-center text-white font-bold text-xl shadow-lg z-10">
                    4
                  </div>
                  <h3 class="text-2xl font-bold text-gray-900 mt-2 mb-4">Go Live</h3>
                  <div class="text-gray-500 space-y-4">
                    <p>Launch your restaurant with Quisin:</p>
                    <ul class="space-y-3">
                      <li class="flex items-center">
                        <ShoppingCartIcon class="h-5 w-5 text-[#FF6B00] mr-2 flex-shrink-0" />
                        <span>Start accepting orders</span>
                      </li>
                      <li class="flex items-center">
                        <ChartBarIcon class="h-5 w-5 text-[#FF6B00] mr-2 flex-shrink-0" />
                        <span>Monitor real-time analytics</span>
                      </li>
                      <li class="flex items-center">
                        <ChatBubbleBottomCenterTextIcon class="h-5 w-5 text-[#FF6B00] mr-2 flex-shrink-0" />
                        <span>Get customer feedback</span>
                      </li>
                      <li class="flex items-center">
                        <PhoneIcon class="h-5 w-5 text-[#FF6B00] mr-2 flex-shrink-0" />
                        <span>Access 24/7 support</span>
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- CTA Section -->
      <div class="mt-24 text-center">
        <div class="inline-block bg-gradient-to-r from-orange-50 to-orange-100 rounded-2xl p-8 shadow-lg transform transition-all duration-300 hover:shadow-xl">
          <h2 class="text-3xl font-bold text-gray-900 mb-4">Ready to get started?</h2>
          <p class="text-lg text-gray-600 mb-8">Join thousands of successful restaurants today.</p>
          <div class="flex flex-col sm:flex-row justify-center gap-4">
            <NuxtLink
              to="/contact"
              class="inline-flex items-center justify-center px-8 py-4 border border-transparent text-lg font-medium rounded-xl text-white bg-[#FF6B00] hover:bg-[#e66000] transition-colors duration-200 shadow-lg hover:shadow-xl"
            >
              Contact Sales
              <ArrowRightIcon class="ml-2 h-5 w-5" />
            </NuxtLink>
            <NuxtLink
              to="/about"
              class="inline-flex items-center justify-center px-8 py-4 border-2 border-[#FF6B00] text-lg font-medium rounded-xl text-[#FF6B00] hover:bg-orange-50 transition-colors duration-200"
            >
              Learn More
            </NuxtLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  CheckCircleIcon,
  Cog6ToothIcon,
  TableCellsIcon,
  UserGroupIcon,
  ClockIcon,
  KeyIcon,
  ClipboardDocumentListIcon,
  ComputerDesktopIcon,
  ShoppingCartIcon,
  ChartBarIcon,
  ChatBubbleBottomCenterTextIcon,
  PhoneIcon,
  ArrowRightIcon
} from '@heroicons/vue/24/outline'
</script> 