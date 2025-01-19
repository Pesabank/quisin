<template>
  <div class="min-h-screen bg-[#1a1a1a] py-12">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-extrabold text-white sm:text-5xl">
          RESERVATION
        </h1>
        <div class="w-20 h-1 bg-[#FF6B00] mx-auto mt-4"></div>
      </div>

      <!-- Main Content -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <!-- Left Side - Table Layout -->
        <div class="bg-[#2a2a2a] rounded-lg shadow-xl p-8">
          <h2 class="text-xl font-semibold text-white mb-6">Restaurant Floor Plan</h2>
          
          <!-- Legend -->
          <div class="flex items-center space-x-6 mb-6">
            <div class="flex items-center">
              <span class="w-3 h-3 rounded-full bg-green-500 mr-2"></span>
              <span class="text-sm text-gray-300">Available</span>
            </div>
            <div class="flex items-center">
              <span class="w-3 h-3 rounded-full bg-red-500 mr-2"></span>
              <span class="text-sm text-gray-300">Occupied</span>
            </div>
            <div class="flex items-center">
              <span class="w-3 h-3 rounded-full bg-yellow-500 mr-2"></span>
              <span class="text-sm text-gray-300">Reserved</span>
            </div>
          </div>

          <!-- Table Grid -->
          <div class="grid grid-cols-3 gap-4 bg-[#3a3a3a] p-6 rounded-lg">
            <div
              v-for="table in tables"
              :key="table.id"
              :class="[
                'relative p-4 rounded-lg cursor-pointer transform transition-all duration-200 hover:scale-105',
                table.status === 'available' ? 'bg-green-100 hover:bg-green-200' :
                table.status === 'occupied' ? 'bg-red-100 hover:bg-red-200' :
                'bg-yellow-100 hover:bg-yellow-200',
                table.id === selectedTable?.id ? 'ring-2 ring-[#FF6B00]' : ''
              ]"
              @click="selectTable(table)"
            >
              <div class="text-center">
                <p class="font-medium text-gray-900">Table {{ table.number }}</p>
                <p class="text-sm text-gray-600">{{ table.size }} Seats</p>
              </div>
              <!-- Seats Visualization -->
              <div class="absolute inset-0 flex items-center justify-center">
                <div
                  v-for="seat in table.size"
                  :key="seat"
                  :style="{
                    transform: `rotate(${(360 / table.size) * seat}deg) translateY(-${table.size <= 4 ? '1.5' : '2'}rem)`
                  }"
                  class="absolute w-2 h-2 rounded-full bg-gray-400"
                ></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Right Side - Form -->
        <div class="bg-[#2a2a2a] rounded-lg shadow-xl p-8">
          <h2 class="text-xl font-semibold text-white mb-6">Reserve a Table</h2>
          <form class="space-y-6" @submit.prevent="submitReservation">
            <!-- Location Selection -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-200 mb-2">Country</label>
                <select
                  v-model="country"
                  class="block w-full px-4 py-3 rounded-lg bg-[#3a3a3a] border-gray-600 text-white shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                  @change="onCountryChange"
                  required
                >
                  <option value="">Select Country</option>
                  <option v-for="c in countries" :key="c.code" :value="c.code">{{ c.name }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-200 mb-2">City</label>
                <select
                  v-model="city"
                  class="block w-full px-4 py-3 rounded-lg bg-[#3a3a3a] border-gray-600 text-white shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                  @change="onCityChange"
                  required
                >
                  <option value="">Select City</option>
                  <option v-for="c in cities" :key="c" :value="c">{{ c }}</option>
                </select>
              </div>
            </div>

            <!-- Restaurant Selection -->
            <div>
              <label class="block text-sm font-medium text-gray-200 mb-2">Restaurant</label>
              <select
                v-model="restaurant"
                class="block w-full px-4 py-3 rounded-lg bg-[#3a3a3a] border-gray-600 text-white shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                required
              >
                <option value="">Select Restaurant</option>
                <option v-for="r in restaurants" :key="r.id" :value="r.id">{{ r.name }}</option>
              </select>
            </div>

            <!-- Date and Time -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-200 mb-2">Date</label>
                <input
                  type="date"
                  v-model="date"
                  :min="today"
                  class="block w-full px-4 py-3 rounded-lg bg-[#3a3a3a] border-gray-600 text-white shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                  required
                >
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-200 mb-2">Time</label>
                <select
                  v-model="time"
                  class="block w-full px-4 py-3 rounded-lg bg-[#3a3a3a] border-gray-600 text-white shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                  required
                >
                  <option value="">Select Time</option>
                  <option v-for="t in availableTimes" :key="t" :value="t">{{ t }}</option>
                </select>
              </div>
            </div>

            <!-- Party Size and Seating -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-200 mb-2">Party Size</label>
                <select
                  v-model="partySize"
                  class="block w-full px-4 py-3 rounded-lg bg-[#3a3a3a] border-gray-600 text-white shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                  required
                >
                  <option value="">Select Size</option>
                  <option v-for="size in 12" :key="size" :value="size">{{ size }} {{ size === 1 ? 'person' : 'people' }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-200 mb-2">Seating Preference</label>
                <select
                  v-model="seatingPreference"
                  class="block w-full px-4 py-3 rounded-lg bg-[#3a3a3a] border-gray-600 text-white shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                >
                  <option value="any">No preference</option>
                  <option value="indoor">Indoor</option>
                  <option value="outdoor">Outdoor</option>
                  <option value="bar">Bar</option>
                </select>
              </div>
            </div>

            <!-- Contact Information -->
            <div>
              <label class="block text-sm font-medium text-gray-200 mb-2">Name</label>
              <input
                type="text"
                v-model="name"
                placeholder="Your full name"
                class="block w-full px-4 py-3 rounded-lg bg-[#3a3a3a] border-gray-600 text-white shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                required
              >
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-200 mb-2">Email</label>
              <input
                type="email"
                v-model="email"
                placeholder="you@example.com"
                class="block w-full px-4 py-3 rounded-lg bg-[#3a3a3a] border-gray-600 text-white shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                required
              >
            </div>

            <!-- Special Requests -->
            <div>
              <label class="block text-sm font-medium text-gray-200 mb-2">Special Requests</label>
              <textarea
                v-model="specialRequests"
                rows="3"
                placeholder="Any special requests or dietary requirements?"
                class="block w-full px-4 py-3 rounded-lg bg-[#3a3a3a] border-gray-600 text-white shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
              ></textarea>
            </div>

            <!-- Submit Button -->
            <button
              type="submit"
              class="w-full flex justify-center py-3 px-4 rounded-lg shadow-sm text-lg font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00] transition-colors duration-200"
            >
              Reserve a Table
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

interface Table {
  id: string
  number: number
  size: number
  status: 'available' | 'occupied' | 'reserved'
}

// Form data
const date = ref('')
const time = ref('')
const partySize = ref('')
const seatingPreference = ref('any')
const name = ref('')
const email = ref('')
const specialRequests = ref('')
const country = ref('')
const city = ref('')
const restaurant = ref('')
const selectedTable = ref<Table | null>(null)

// Mock table data
const tables = ref<Table[]>([
  { id: '1', number: 1, size: 4, status: 'available' },
  { id: '2', number: 2, size: 2, status: 'occupied' },
  { id: '3', number: 3, size: 6, status: 'reserved' },
  { id: '4', number: 4, size: 4, status: 'available' },
  { id: '5', number: 5, size: 8, status: 'available' },
  { id: '6', number: 6, size: 2, status: 'occupied' }
])

// Location data
const countries = ref([
  { code: 'KE', name: 'Kenya' },
  { code: 'UG', name: 'Uganda' },
  { code: 'TZ', name: 'Tanzania' }
])

const cities = ref<string[]>([])
const restaurants = ref<{ id: string; name: string }[]>([])
const availableTimes = ref<string[]>([])

// Get today's date in YYYY-MM-DD format for min date
const today = new Date().toISOString().split('T')[0]

// Mock data for cities based on country selection
const onCountryChange = () => {
  if (country.value === 'KE') {
    cities.value = ['Nairobi', 'Mombasa', 'Kisumu']
  } else if (country.value === 'UG') {
    cities.value = ['Kampala', 'Entebbe', 'Jinja']
  } else if (country.value === 'TZ') {
    cities.value = ['Dar es Salaam', 'Arusha', 'Zanzibar']
  } else {
    cities.value = []
  }
  city.value = ''
  restaurant.value = ''
}

// Mock data for restaurants based on city selection
const onCityChange = () => {
  if (city.value) {
    restaurants.value = [
      { id: '1', name: 'Restaurant A' },
      { id: '2', name: 'Restaurant B' },
      { id: '3', name: 'Restaurant C' }
    ]
  } else {
    restaurants.value = []
  }
  restaurant.value = ''
}

// Generate available time slots
const generateTimeSlots = () => {
  const times = []
  for (let hour = 11; hour <= 21; hour++) {
    times.push(`${hour}:00`)
    times.push(`${hour}:30`)
  }
  availableTimes.value = times
}

generateTimeSlots()

const selectTable = (table: Table) => {
  if (table.status === 'available') {
    selectedTable.value = table
  }
}

const submitReservation = () => {
  // TODO: Implement reservation submission
  console.log({
    date: date.value,
    time: time.value,
    partySize: partySize.value,
    seatingPreference: seatingPreference.value,
    name: name.value,
    email: email.value,
    specialRequests: specialRequests.value,
    country: country.value,
    city: city.value,
    restaurant: restaurant.value,
    selectedTable: selectedTable.value
  })
}
</script> 