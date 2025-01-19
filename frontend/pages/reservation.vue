<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Hero Section -->
    <div class="relative bg-gray-900 py-16">
      <div class="absolute inset-0">
        <div class="absolute inset-0 bg-gradient-to-br from-[#FF6B00] to-gray-900 opacity-80"></div>
      </div>
      <div class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h1 class="text-4xl font-extrabold tracking-tight text-white sm:text-5xl lg:text-6xl">Make a Reservation</h1>
        <p class="mt-6 max-w-2xl mx-auto text-xl text-gray-200">
          Book your table for an unforgettable dining experience
        </p>
      </div>
    </div>

    <!-- Reservation Section -->
    <div class="py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-12">
          <!-- Reservation Form -->
          <div class="bg-white rounded-lg shadow-xl p-8">
            <h2 class="text-2xl font-bold text-gray-900 mb-6">Book Your Table</h2>
            <form class="space-y-6">
              <!-- Guest Information -->
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label for="name" class="block text-sm font-medium text-gray-700">Full Name</label>
                  <input type="text" id="name" v-model="form.name" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]">
                </div>
                <div>
                  <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
                  <input type="email" id="email" v-model="form.email" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]">
                </div>
              </div>

              <!-- Reservation Details -->
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label for="date" class="block text-sm font-medium text-gray-700">Date</label>
                  <input type="date" id="date" v-model="form.date" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]">
                </div>
                <div>
                  <label for="time" class="block text-sm font-medium text-gray-700">Time</label>
                  <select id="time" v-model="form.time" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]">
                    <option v-for="time in availableTimes" :key="time" :value="time">{{ time }}</option>
                  </select>
                </div>
              </div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label for="guests" class="block text-sm font-medium text-gray-700">Number of Guests</label>
                  <select id="guests" v-model="form.guests" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]">
                    <option v-for="n in 10" :key="n" :value="n">{{ n }} {{ n === 1 ? 'Guest' : 'Guests' }}</option>
                  </select>
                </div>
                <div>
                  <label for="occasion" class="block text-sm font-medium text-gray-700">Special Occasion</label>
                  <select id="occasion" v-model="form.occasion" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]">
                    <option value="">None</option>
                    <option value="birthday">Birthday</option>
                    <option value="anniversary">Anniversary</option>
                    <option value="business">Business Dinner</option>
                    <option value="other">Other</option>
                  </select>
                </div>
              </div>

              <div>
                <label for="notes" class="block text-sm font-medium text-gray-700">Special Requests</label>
                <textarea id="notes" v-model="form.notes" rows="3" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]" placeholder="Any dietary restrictions or special requests?"></textarea>
              </div>

              <div>
                <button type="submit" class="w-full flex justify-center py-3 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00] transition-colors duration-200">
                  Book Table
                </button>
              </div>
            </form>
          </div>

          <!-- Additional Information -->
          <div class="space-y-8">
            <!-- Opening Hours -->
            <div class="bg-white rounded-lg shadow-xl p-8">
              <h3 class="text-xl font-semibold text-gray-900 mb-4">Opening Hours</h3>
              <div class="space-y-3">
                <div class="flex justify-between">
                  <span class="text-gray-600">Monday - Thursday</span>
                  <span class="text-gray-900">11:00 AM - 10:00 PM</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-gray-600">Friday - Saturday</span>
                  <span class="text-gray-900">11:00 AM - 11:00 PM</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-gray-600">Sunday</span>
                  <span class="text-gray-900">11:00 AM - 9:00 PM</span>
                </div>
              </div>
            </div>

            <!-- Reservation Policy -->
            <div class="bg-white rounded-lg shadow-xl p-8">
              <h3 class="text-xl font-semibold text-gray-900 mb-4">Reservation Policy</h3>
              <ul class="space-y-3 text-gray-600">
                <li class="flex items-start">
                  <svg class="h-6 w-6 text-[#FF6B00] mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                  </svg>
                  <span>Reservations are held for 15 minutes past the booking time</span>
                </li>
                <li class="flex items-start">
                  <svg class="h-6 w-6 text-[#FF6B00] mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                  </svg>
                  <span>For parties of 6 or more, please call us directly</span>
                </li>
                <li class="flex items-start">
                  <svg class="h-6 w-6 text-[#FF6B00] mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                  </svg>
                  <span>Special requests are subject to availability</span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

definePageMeta({
  layout: 'default',
  middleware: 'auth',
  auth: {
    unauthenticated: 'public'
  }
})

const form = ref({
  name: '',
  email: '',
  date: '',
  time: '',
  guests: 2,
  occasion: '',
  notes: ''
})

const availableTimes = [
  '11:00 AM', '11:30 AM',
  '12:00 PM', '12:30 PM', '1:00 PM', '1:30 PM',
  '2:00 PM', '2:30 PM', '3:00 PM', '3:30 PM',
  '4:00 PM', '4:30 PM', '5:00 PM', '5:30 PM',
  '6:00 PM', '6:30 PM', '7:00 PM', '7:30 PM',
  '8:00 PM', '8:30 PM', '9:00 PM'
]
</script>

<style scoped>
input, select, textarea {
  @apply border-gray-300;
}

input:focus, select:focus, textarea:focus {
  @apply ring-2 ring-[#FF6B00] border-[#FF6B00];
}
</style> 