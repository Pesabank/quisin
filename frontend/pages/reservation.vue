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

    <!-- Success Message -->
    <div v-if="successMessage" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
      <div class="bg-green-50 border-l-4 border-green-400 p-4">
        <div class="flex">
          <div class="flex-shrink-0">
            <svg class="h-5 w-5 text-green-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
            </svg>
          </div>
          <div class="ml-3">
            <p class="text-sm text-green-700">{{ successMessage }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Reservation Section -->
    <div class="py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Reservation Form -->
        <div class="bg-white rounded-lg shadow-xl p-8 w-full">
          <h2 class="text-2xl font-bold text-gray-900 mb-6">Book Your Table</h2>
          <form class="space-y-8 mb-8" @submit.prevent="handleSubmit">
            <!-- Error Alert -->
            <div v-if="error" class="bg-red-50 border-l-4 border-red-400 p-4">
              <div class="flex">
                <div class="flex-shrink-0">
                  <svg class="h-5 w-5 text-red-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
                  </svg>
                </div>
                <div class="ml-3">
                  <p class="text-sm text-red-700">{{ error }}</p>
                </div>
              </div>
            </div>

            <!-- Location Selection -->
            <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
              <div>
                <label class="block text-base font-medium text-gray-700 mb-3">Country</label>
                <select
                  v-model="form.country"
                  class="mt-1 block w-full px-6 py-4 text-lg rounded-md border-2 border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] hover:border-[#FF6B00] transition-all duration-200 bg-white text-gray-900"
                  required
                >
                  <option value="" disabled class="text-gray-500">Select your country</option>
                  <option v-for="country in countries" :key="country" :value="country">
                    {{ country }}
                  </option>
                </select>
              </div>
              <div>
                <label class="block text-base font-medium text-gray-700 mb-3">County/State</label>
                <select
                  v-model="form.county"
                  class="mt-1 block w-full px-6 py-4 text-lg rounded-md border-2 border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] hover:border-[#FF6B00] transition-all duration-200 bg-white text-gray-900"
                  required
                  :disabled="!form.country"
                >
                  <option value="" disabled class="text-gray-500">Select your county/state</option>
                  <option v-for="county in availableCounties" :key="county" :value="county">
                    {{ county }}
                  </option>
                </select>
              </div>
              <div>
                <label class="block text-base font-medium text-gray-700 mb-3">City</label>
                <select
                  v-model="form.city"
                  class="mt-1 block w-full px-6 py-4 text-lg rounded-md border-2 border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] hover:border-[#FF6B00] transition-all duration-200 bg-white text-gray-900"
                  required
                  :disabled="!form.county"
                >
                  <option value="" disabled class="text-gray-500">Select your city</option>
                  <option v-for="city in availableCities" :key="city" :value="city">
                    {{ city }}
                  </option>
                </select>
              </div>
              <div>
                <label class="block text-base font-medium text-gray-700 mb-3">Restaurant</label>
                <select
                  v-model="form.restaurant"
                  class="mt-1 block w-full px-6 py-4 text-lg rounded-md border-2 border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] hover:border-[#FF6B00] transition-all duration-200 bg-white text-gray-900"
                  required
                  :disabled="!form.city"
                >
                  <option value="" disabled class="text-gray-500">Select restaurant</option>
                  <option v-for="restaurant in reservationStore.availableRestaurants" :key="restaurant.id" :value="restaurant.id">
                    {{ restaurant.name }}
                  </option>
                </select>
              </div>
            </div>

            <!-- Guest Information -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
              <div>
                <label for="name" class="block text-base font-medium text-gray-700 mb-3">Full Name</label>
                <input 
                  type="text" 
                  id="name" 
                  v-model="form.name" 
                  class="mt-1 block w-full px-6 py-4 text-lg rounded-md border-2 border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] hover:border-[#FF6B00] transition-all duration-200 bg-white text-gray-900"
                  placeholder="Enter your full name"
                >
              </div>
              <div>
                <label for="email" class="block text-base font-medium text-gray-700 mb-3">Email</label>
                <input 
                  type="email" 
                  id="email" 
                  v-model="form.email" 
                  class="mt-1 block w-full px-6 py-4 text-lg rounded-md border-2 border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] hover:border-[#FF6B00] transition-all duration-200 bg-white text-gray-900"
                  placeholder="Enter your email"
                >
              </div>
            </div>

            <!-- Reservation Details -->
            <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
              <div>
                <label for="date" class="block text-base font-medium text-gray-700 mb-3">Date</label>
                <input 
                  type="date" 
                  id="date" 
                  v-model="form.date" 
                  class="mt-1 block w-full px-6 py-4 text-lg rounded-md border-2 border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] hover:border-[#FF6B00] transition-all duration-200 bg-white text-gray-900"
                >
              </div>
              <div>
                <label for="time" class="block text-base font-medium text-gray-700 mb-3">Time</label>
                <select 
                  id="time" 
                  v-model="form.time" 
                  class="mt-1 block w-full px-6 py-4 text-lg rounded-md border-2 border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] hover:border-[#FF6B00] transition-all duration-200 bg-white text-gray-900"
                >
                  <option value="" disabled class="text-gray-500">Select time</option>
                  <option v-for="time in availableTimes" :key="time" :value="time">{{ time }}</option>
                </select>
              </div>
              <div>
                <label for="guests" class="block text-base font-medium text-gray-700 mb-3">Number of Guests</label>
                <select 
                  id="guests" 
                  v-model="form.guests" 
                  class="mt-1 block w-full px-6 py-4 text-lg rounded-md border-2 border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] hover:border-[#FF6B00] transition-all duration-200 bg-white text-gray-900"
                >
                  <option value="" disabled class="text-gray-500">Select guests</option>
                  <option v-for="n in 10" :key="n" :value="n">{{ n }} {{ n === 1 ? 'Guest' : 'Guests' }}</option>
                </select>
              </div>
              <div>
                <label for="occasion" class="block text-base font-medium text-gray-700 mb-3">Special Occasion</label>
                <select 
                  id="occasion" 
                  v-model="form.occasion" 
                  class="mt-1 block w-full px-6 py-4 text-lg rounded-md border-2 border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] hover:border-[#FF6B00] transition-all duration-200 bg-white text-gray-900"
                >
                  <option value="" class="text-gray-500">None</option>
                  <option value="birthday">Birthday</option>
                  <option value="anniversary">Anniversary</option>
                  <option value="business">Business Dinner</option>
                  <option value="other">Other</option>
                </select>
              </div>
            </div>

            <!-- Table Selection -->
            <div>
              <div class="flex justify-between items-center mb-3">
                <label class="block text-base font-medium text-gray-700">Table Selection</label>
                <button
                  type="button"
                  class="text-[#FF6B00] hover:text-[#e66000] text-sm font-medium"
                  @click="openTableSelection"
                  :disabled="!canSelectTable"
                >
                  {{ form.tableId ? 'Change Table' : 'Select Table' }}
                </button>
              </div>
              <div 
                v-if="form.tableId" 
                class="p-4 bg-orange-50 rounded-lg border border-orange-200"
              >
                <div class="flex items-center justify-between">
                  <div>
                    <p class="font-medium text-gray-900">Table {{ selectedTableNumber }}</p>
                    <p class="text-sm text-gray-600">{{ selectedTableLocation }}</p>
                  </div>
                  <button
                    type="button"
                    class="text-gray-400 hover:text-gray-500"
                    @click="clearTableSelection"
                  >
                    <XMarkIcon class="h-5 w-5" />
                  </button>
                </div>
              </div>
              <div 
                v-else 
                class="p-4 bg-gray-50 rounded-lg border border-gray-200 text-gray-500 text-center"
              >
                No table selected
              </div>
            </div>

            <div>
              <label for="notes" class="block text-base font-medium text-gray-700 mb-3">Special Requests</label>
              <textarea 
                id="notes" 
                v-model="form.notes" 
                rows="3" 
                class="mt-1 block w-full px-6 py-4 text-lg rounded-md border-2 border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] hover:border-[#FF6B00] transition-all duration-200 bg-white text-gray-900" 
                placeholder="Any dietary restrictions or special requests?"
              ></textarea>
            </div>

            <div>
              <button 
                type="submit" 
                class="w-full flex justify-center py-4 px-6 border border-transparent rounded-md shadow-sm text-lg font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] hover:scale-[1.02] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00] transform transition-all duration-200 hover:shadow-lg hover:shadow-[#FF6B00]/30 active:scale-[0.98]"
                :disabled="isSubmitting || !form.tableId"
              >
                <span v-if="isSubmitting">Booking...</span>
                <span v-else>Book Table</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Table Selection Modal -->
    <TableSelectionModal
      v-if="showTableSelection"
      :show="showTableSelection"
      :restaurant-id="form.restaurant"
      :party-size="form.guests"
      :reservation-date-time="getReservationDateTime()"
      @close="closeTableSelection"
      @select="handleTableSelection"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { XMarkIcon } from '@heroicons/vue/24/outline'
import TableSelectionModal from '@/components/reservation/TableSelectionModal.vue'
import { useReservationStore } from '@/stores/reservation'
import { useReservation } from '@/composables/useReservation'

definePageMeta({
  layout: 'default',
  middleware: 'auth',
  auth: {
    unauthenticated: 'public'
  }
})

const countries = ['Kenya', 'Uganda', 'Tanzania']

const locationData = {
  Kenya: {
    counties: {
      Nairobi: ['Westlands', 'Kilimani', 'Karen'],
      Mombasa: ['Nyali', 'Bamburi', 'City Center'],
      Kisumu: ['CBD', 'Milimani']
    }
  },
  Uganda: {
    counties: {
      Kampala: ['Nakasero', 'Kololo', 'Bugolobi'],
      Wakiso: ['Entebbe', 'Kajjansi']
    }
  },
  Tanzania: {
    counties: {
      'Dar es Salaam': ['Masaki', 'Oysterbay', 'City Centre'],
      Arusha: ['CBD', 'Njiro']
    }
  }
}

const reservationStore = useReservationStore()
const { form, isSubmitting, error } = reservationStore
const { createReservation } = useReservation()

const availableCounties = ref<string[]>([])
const availableCities = ref<string[]>([])
const availableRestaurants = ref<{ id: string; name: string }[]>([])
const availableTimes = ['18:00', '18:30', '19:00', '19:30', '20:00', '20:30', '21:00', '21:30']
const showTableSelection = ref(false)
const selectedTableNumber = ref<string>('')
const selectedTableLocation = ref<string>('')
const successMessage = ref<string>('')

const canSelectTable = computed(() => {
  return form.restaurant && 
         form.date && 
         form.time && 
         form.guests
})

function updateCounties() {
  form.county = ''
  form.city = ''
  form.restaurant = ''
  availableCounties.value = form.country ? Object.keys(locationData[form.country].counties) : []
}

function updateCities() {
  form.city = ''
  form.restaurant = ''
  availableCities.value = form.county ? locationData[form.country].counties[form.county] : []
}

async function updateRestaurants() {
  form.restaurant = ''
  if (form.country && form.county && form.city) {
    await reservationStore.fetchRestaurants(form.country, form.county, form.city)
  }
}

function getReservationDateTime() {
  if (!form.date || !form.time) return ''
  return `${form.date}T${form.time}:00`
}

function openTableSelection() {
  showTableSelection.value = true
}

function closeTableSelection() {
  showTableSelection.value = false
}

function handleTableSelection(tableId: string, tableDetails: { number: string; location: string }) {
  form.tableId = tableId
  selectedTableNumber.value = tableDetails.number
  selectedTableLocation.value = tableDetails.location
  closeTableSelection()
}

function clearTableSelection() {
  form.tableId = ''
  selectedTableNumber.value = ''
  selectedTableLocation.value = ''
}

async function handleSubmit() {
  try {
    const reservationData = {
      ...form,
      reservationDateTime: getReservationDateTime()
    }
    const response = await createReservation(reservationData)
    successMessage.value = 'Reservation created successfully!'
    reservationStore.resetForm()
    // You might want to redirect to a confirmation page or show a success modal
  } catch (error) {
    // Error is handled by the composable
    console.error('Error submitting reservation:', error)
  }
}

// Watch for country changes
watch(() => form.country, updateCounties)

// Watch for county changes
watch(() => form.county, updateCities)

// Watch for city changes
watch(() => form.city, updateRestaurants)
</script>

<style scoped>
input, select, textarea {
  @apply transition-all duration-200 text-lg;
  min-height: 4rem;
}

input:hover, select:hover, textarea:hover {
  @apply border-[#FF6B00] shadow-md shadow-[#FF6B00]/20;
}

input:focus, select:focus, textarea:focus {
  @apply ring-2 ring-[#FF6B00] border-[#FF6B00] shadow-md shadow-[#FF6B00]/20;
}

select:disabled {
  @apply opacity-50 cursor-not-allowed;
}

select {
  @apply appearance-none bg-no-repeat bg-[length:24px_24px] bg-[right_1rem_center];
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%23666666'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M19 9l-7 7-7-7'%3E%3C/path%3E%3C/svg%3E");
  padding-right: 3rem !important;
}

select option {
  @apply py-4 px-6 text-lg;
}

input[type="date"] {
  @apply text-gray-700;
  min-height: 4rem;
  padding-right: 3rem !important;
}

input[type="date"]::-webkit-calendar-picker-indicator {
  @apply w-8 h-8 opacity-70 hover:opacity-100 cursor-pointer;
  margin-right: 0.5rem;
}

label {
  @apply text-base font-medium mb-3 block;
}

textarea {
  min-height: 8rem;
}

.form-group {
  @apply mb-6;
}

/* Add background color to options */
select option {
  @apply bg-white text-gray-700;
}

/* Improve dropdown visibility */
select:focus option:checked {
  @apply bg-[#FF6B00] text-white;
}
</style>