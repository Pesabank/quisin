<template>
  <div class="min-h-screen bg-gray-900 py-12">
    <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
      <h1 class="text-4xl font-bold text-center text-white mb-2">RESERVATION</h1>
      <div class="h-1 w-24 bg-[#FF6B00] mx-auto mb-12"></div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-12">
        <!-- Table Layout Section -->
        <div class="bg-gray-800 rounded-xl p-8">
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
          <div class="grid grid-cols-3 gap-4 bg-gray-700 p-6 rounded-lg">
            <div
              v-for="table in tables"
              :key="table.id"
              :class="[
                'relative p-4 rounded-lg cursor-pointer transform transition-all duration-200 hover:scale-105',
                table.status === 'available' ? 'bg-green-100 hover:bg-green-200' :
                table.status === 'occupied' ? 'bg-red-100 hover:bg-red-200' :
                'bg-yellow-100 hover:bg-yellow-200'
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

        <!-- Reservation Form -->
        <div class="bg-gray-800 rounded-xl p-8">
          <h2 class="text-xl font-semibold text-white mb-6">Reserve a Table</h2>
          <form @submit.prevent="submitReservation" class="space-y-6">
            <!-- Country & City Selection -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">Country</label>
                <select
                  v-model="form.country"
                  class="block w-full px-4 py-3 rounded-lg bg-gray-700 border border-gray-600 text-white focus:ring-[#FF6B00] focus:border-[#FF6B00]"
                  required
                >
                  <option value="">Select Country</option>
                  <option value="kenya">Kenya</option>
                  <option value="uganda">Uganda</option>
                  <option value="tanzania">Tanzania</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">City</label>
                <select
                  v-model="form.city"
                  class="block w-full px-4 py-3 rounded-lg bg-gray-700 border border-gray-600 text-white focus:ring-[#FF6B00] focus:border-[#FF6B00]"
                  required
                >
                  <option value="">Select City</option>
                  <option value="nairobi">Nairobi</option>
                  <option value="mombasa">Mombasa</option>
                  <option value="kisumu">Kisumu</option>
                </select>
              </div>
            </div>

            <!-- Restaurant Selection -->
            <div>
              <label class="block text-sm font-medium text-gray-300 mb-2">Restaurant</label>
              <select
                v-model="form.restaurant"
                class="block w-full px-4 py-3 rounded-lg bg-gray-700 border border-gray-600 text-white focus:ring-[#FF6B00] focus:border-[#FF6B00]"
                required
              >
                <option value="">Select Restaurant</option>
                <option value="downtown">Downtown Branch</option>
                <option value="uptown">Uptown Branch</option>
                <option value="waterfront">Waterfront Branch</option>
              </select>
            </div>

            <!-- Date & Time Selection -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">Date</label>
                <input
                  type="date"
                  v-model="form.date"
                  class="block w-full px-4 py-3 rounded-lg bg-gray-700 border border-gray-600 text-white focus:ring-[#FF6B00] focus:border-[#FF6B00]"
                  required
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">Time</label>
                <select
                  v-model="form.time"
                  class="block w-full px-4 py-3 rounded-lg bg-gray-700 border border-gray-600 text-white focus:ring-[#FF6B00] focus:border-[#FF6B00]"
                  required
                >
                  <option value="">Select Time</option>
                  <option v-for="time in availableTimes" :key="time" :value="time">{{ time }}</option>
                </select>
              </div>
            </div>

            <!-- Party Size & Seating Preference -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">Party Size</label>
                <select
                  v-model="form.partySize"
                  class="block w-full px-4 py-3 rounded-lg bg-gray-700 border border-gray-600 text-white focus:ring-[#FF6B00] focus:border-[#FF6B00]"
                  required
                >
                  <option value="">Select Size</option>
                  <option v-for="n in 8" :key="n" :value="n">{{ n }} {{ n === 1 ? 'person' : 'people' }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">Seating Preference</label>
                <select
                  v-model="form.seatingPreference"
                  class="block w-full px-4 py-3 rounded-lg bg-gray-700 border border-gray-600 text-white focus:ring-[#FF6B00] focus:border-[#FF6B00]"
                >
                  <option value="">No preference</option>
                  <option value="indoor">Indoor</option>
                  <option value="outdoor">Outdoor</option>
                  <option value="bar">Bar</option>
                </select>
              </div>
            </div>

            <!-- Contact Information -->
            <div>
              <label class="block text-sm font-medium text-gray-300 mb-2">Full Name</label>
              <input
                type="text"
                v-model="form.name"
                placeholder="Your full name"
                class="block w-full px-4 py-3 rounded-lg bg-gray-700 border border-gray-600 text-white focus:ring-[#FF6B00] focus:border-[#FF6B00]"
                required
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-300 mb-2">Email</label>
              <input
                type="email"
                v-model="form.email"
                placeholder="you@example.com"
                class="block w-full px-4 py-3 rounded-lg bg-gray-700 border border-gray-600 text-white focus:ring-[#FF6B00] focus:border-[#FF6B00]"
                required
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-300 mb-2">Special Requests</label>
              <textarea
                v-model="form.specialRequests"
                rows="3"
                placeholder="Any special requests or dietary requirements?"
                class="block w-full px-4 py-3 rounded-lg bg-gray-700 border border-gray-600 text-white focus:ring-[#FF6B00] focus:border-[#FF6B00]"
              ></textarea>
            </div>

            <div>
              <button
                type="submit"
                class="w-full bg-[#FF6B00] text-white py-3 px-6 rounded-lg font-medium hover:bg-[#e66000] transition-colors"
              >
                Reserve a Table
              </button>
            </div>
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

const tables = ref<Table[]>([
  { id: '1', number: 1, size: 4, status: 'available' },
  { id: '2', number: 2, size: 2, status: 'occupied' },
  { id: '3', number: 3, size: 6, status: 'reserved' },
  { id: '4', number: 4, size: 4, status: 'available' },
  { id: '5', number: 5, size: 8, status: 'available' },
  { id: '6', number: 6, size: 2, status: 'occupied' }
])

const form = ref({
  country: '',
  city: '',
  restaurant: '',
  date: '',
  time: '',
  partySize: '',
  seatingPreference: '',
  name: '',
  email: '',
  specialRequests: '',
  selectedTable: null as Table | null
})

const availableTimes = [
  '11:00 AM', '11:30 AM',
  '12:00 PM', '12:30 PM',
  '1:00 PM', '1:30 PM',
  '2:00 PM', '2:30 PM',
  '5:00 PM', '5:30 PM',
  '6:00 PM', '6:30 PM',
  '7:00 PM', '7:30 PM',
  '8:00 PM', '8:30 PM',
  '9:00 PM', '9:30 PM'
]

const selectTable = (table: Table) => {
  if (table.status === 'available') {
    form.value.selectedTable = table
  }
}

const submitReservation = () => {
  // TODO: Implement reservation submission
  console.log('Reservation submitted:', form.value)
}
</script> 