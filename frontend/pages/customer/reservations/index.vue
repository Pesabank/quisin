<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="sm:flex sm:items-center">
        <div class="sm:flex-auto">
          <h1 class="text-3xl font-bold text-gray-900">Table Reservations</h1>
          <p class="mt-2 text-sm text-gray-700">
            Book a table for your next dining experience or manage your existing reservations.
          </p>
        </div>
        <div class="mt-4 sm:mt-0 sm:ml-16 sm:flex-none">
          <button
            @click="openReservationModal"
            class="inline-flex items-center justify-center rounded-md border border-transparent bg-[#FF6B00] px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-[#FF6B00] focus:ring-offset-2 sm:w-auto"
          >
            <PlusIcon class="h-5 w-5 mr-2" />
            New Reservation
          </button>
        </div>
      </div>

      <!-- Upcoming Reservations -->
      <div class="mt-8">
        <h2 class="text-lg font-medium text-gray-900">Upcoming Reservations</h2>
        <div class="mt-4 space-y-6">
          <TransitionGroup
            enter-active-class="transition duration-100 ease-out"
            enter-from-class="transform scale-95 opacity-0"
            enter-to-class="transform scale-100 opacity-100"
            leave-active-class="transition duration-75 ease-in"
            leave-from-class="transform scale-100 opacity-100"
            leave-to-class="transform scale-95 opacity-0"
          >
            <div
              v-for="reservation in upcomingReservations"
              :key="reservation.id"
              class="bg-white shadow rounded-lg overflow-hidden"
            >
              <div class="px-4 py-5 sm:p-6">
                <div class="sm:flex sm:items-center sm:justify-between">
                  <div>
                    <h3 class="text-lg font-medium leading-6 text-gray-900">
                      Reservation #{{ reservation.id }}
                    </h3>
                    <div class="mt-2 sm:flex sm:items-center text-sm text-gray-500">
                      <CalendarIcon class="h-5 w-5 text-gray-400 mr-2" />
                      {{ formatDate(reservation.date) }}
                      <ClockIcon class="h-5 w-5 text-gray-400 mx-2" />
                      {{ formatTime(reservation.time) }}
                      <UsersIcon class="h-5 w-5 text-gray-400 mx-2" />
                      {{ reservation.guests }} guests
                      <TableCellsIcon class="h-5 w-5 text-gray-400 mx-2" />
                      Table {{ reservation.tableNumber }}
                    </div>
                  </div>
                  <div class="mt-4 sm:mt-0 sm:ml-6 sm:flex-shrink-0">
                    <button
                      @click="cancelReservation(reservation)"
                      class="inline-flex items-center px-3 py-2 border border-gray-300 shadow-sm text-sm leading-4 font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
                    >
                      Cancel Reservation
                    </button>
                  </div>
                </div>
                <div class="mt-4 text-sm">
                  <p v-if="reservation.specialRequests" class="text-gray-600">
                    <span class="font-medium">Special Requests:</span> {{ reservation.specialRequests }}
                  </p>
                </div>
              </div>
            </div>
          </TransitionGroup>
        </div>
      </div>

      <!-- Past Reservations -->
      <div class="mt-12">
        <h2 class="text-lg font-medium text-gray-900">Past Reservations</h2>
        <div class="mt-4 overflow-hidden shadow ring-1 ring-black ring-opacity-5 sm:rounded-lg">
          <table class="min-w-full divide-y divide-gray-300">
            <thead class="bg-gray-50">
              <tr>
                <th scope="col" class="py-3.5 pl-4 pr-3 text-left text-sm font-semibold text-gray-900 sm:pl-6">Date</th>
                <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">Time</th>
                <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">Guests</th>
                <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">Table</th>
                <th scope="col" class="relative py-3.5 pl-3 pr-4 sm:pr-6">
                  <span class="sr-only">Actions</span>
                </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 bg-white">
              <tr v-for="reservation in pastReservations" :key="reservation.id">
                <td class="whitespace-nowrap py-4 pl-4 pr-3 text-sm font-medium text-gray-900 sm:pl-6">
                  {{ formatDate(reservation.date) }}
                </td>
                <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                  {{ formatTime(reservation.time) }}
                </td>
                <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                  {{ reservation.guests }} guests
                </td>
                <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                  Table {{ reservation.tableNumber }}
                </td>
                <td class="relative whitespace-nowrap py-4 pl-3 pr-4 text-right text-sm font-medium sm:pr-6">
                  <button
                    @click="bookSimilar(reservation)"
                    class="text-[#FF6B00] hover:text-[#e66000]"
                  >
                    Book Similar
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- New Reservation Modal -->
    <TransitionRoot as="template" :show="isReservationModalOpen">
      <Dialog as="div" class="relative z-10" @close="closeReservationModal">
        <TransitionChild
          as="template"
          enter="ease-out duration-300"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="ease-in duration-200"
          leave-from="opacity-100"
          leave-to="opacity-0"
        >
          <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" />
        </TransitionChild>

        <div class="fixed inset-0 z-10 overflow-y-auto">
          <div class="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0">
            <TransitionChild
              as="template"
              enter="ease-out duration-300"
              enter-from="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
              enter-to="opacity-100 translate-y-0 sm:scale-100"
              leave="ease-in duration-200"
              leave-from="opacity-100 translate-y-0 sm:scale-100"
              leave-to="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
            >
              <DialogPanel class="relative transform overflow-hidden rounded-lg bg-white px-4 pb-4 pt-5 text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-lg sm:p-6">
                <div>
                  <div class="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-[#FF6B00]">
                    <CalendarDaysIcon class="h-6 w-6 text-white" aria-hidden="true" />
                  </div>
                  <div class="mt-3 text-center sm:mt-5">
                    <DialogTitle as="h3" class="text-base font-semibold leading-6 text-gray-900">
                      New Reservation
                    </DialogTitle>
                    <div class="mt-6">
                      <!-- Date Selection -->
                      <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-700 mb-1">Date</label>
                        <input
                          type="date"
                          v-model="newReservation.date"
                          class="block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] sm:text-sm"
                          :min="today"
                        />
                      </div>

                      <!-- Time Selection -->
                      <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-700 mb-1">Time</label>
                        <select
                          v-model="newReservation.time"
                          class="block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] sm:text-sm"
                        >
                          <option v-for="time in availableTimes" :key="time" :value="time">
                            {{ time }}
                          </option>
                        </select>
                      </div>

                      <!-- Number of Guests -->
                      <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-700 mb-1">Number of Guests</label>
                        <div class="flex rounded-md shadow-sm">
                          <button
                            type="button"
                            @click="decrementGuests"
                            class="relative inline-flex items-center rounded-l-md border border-gray-300 bg-white px-3 py-2 text-sm font-medium text-gray-500 hover:bg-gray-50 focus:z-10 focus:border-[#FF6B00] focus:outline-none focus:ring-1 focus:ring-[#FF6B00]"
                          >
                            <MinusIcon class="h-4 w-4" />
                          </button>
                          <input
                            type="number"
                            v-model="newReservation.guests"
                            min="1"
                            max="12"
                            class="block w-24 border-gray-300 text-center focus:border-[#FF6B00] focus:ring-[#FF6B00] sm:text-sm"
                          />
                          <button
                            type="button"
                            @click="incrementGuests"
                            class="relative inline-flex items-center rounded-r-md border border-gray-300 bg-white px-3 py-2 text-sm font-medium text-gray-500 hover:bg-gray-50 focus:z-10 focus:border-[#FF6B00] focus:outline-none focus:ring-1 focus:ring-[#FF6B00]"
                          >
                            <PlusIcon class="h-4 w-4" />
                          </button>
                        </div>
                      </div>

                      <!-- Table Selection -->
                      <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-700 mb-1">Available Tables</label>
                        <div class="grid grid-cols-4 gap-2">
                          <button
                            v-for="table in availableTables"
                            :key="table.number"
                            @click="selectTable(table)"
                            :class="[
                              'px-3 py-2 text-sm font-medium rounded-md',
                              newReservation.tableNumber === table.number
                                ? 'bg-[#FF6B00] text-white'
                                : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-50'
                            ]"
                          >
                            {{ table.number }}
                          </button>
                        </div>
                      </div>

                      <!-- Special Requests -->
                      <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-700 mb-1">Special Requests</label>
                        <textarea
                          v-model="newReservation.specialRequests"
                          rows="3"
                          class="block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] sm:text-sm"
                          placeholder="Any special requests or preferences..."
                        ></textarea>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="mt-5 sm:mt-6 sm:grid sm:grid-flow-row-dense sm:grid-cols-2 sm:gap-3">
                  <button
                    type="button"
                    class="inline-flex w-full justify-center rounded-md bg-[#FF6B00] px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-[#e66000] focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-[#FF6B00] sm:col-start-2"
                    @click="submitReservation"
                  >
                    Confirm Reservation
                  </button>
                  <button
                    type="button"
                    class="mt-3 inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 sm:col-start-1 sm:mt-0"
                    @click="closeReservationModal"
                  >
                    Cancel
                  </button>
                </div>
              </DialogPanel>
            </TransitionChild>
          </div>
        </div>
      </Dialog>
    </TransitionRoot>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import {
  CalendarIcon,
  ClockIcon,
  UsersIcon,
  TableCellsIcon,
  PlusIcon,
  MinusIcon,
  CalendarDaysIcon
} from '@heroicons/vue/24/outline'

interface Reservation {
  id: string
  date: string
  time: string
  guests: number
  tableNumber: number
  specialRequests: string
}

// State
const isReservationModalOpen = ref(false)
const newReservation = ref({
  date: '',
  time: '',
  guests: 2,
  tableNumber: 0,
  specialRequests: ''
})

// Mock data
const upcomingReservations = ref<Reservation[]>([
  {
    id: '1001',
    date: '2024-01-20',
    time: '19:00',
    guests: 4,
    tableNumber: 12,
    specialRequests: 'Window seat preferred'
  }
])

const pastReservations = ref<Reservation[]>([
  {
    id: '1000',
    date: '2024-01-10',
    time: '20:00',
    guests: 2,
    tableNumber: 8,
    specialRequests: ''
  }
])

// Computed
const today = computed(() => {
  const date = new Date()
  return date.toISOString().split('T')[0]
})

const availableTimes = [
  '11:00', '11:30', '12:00', '12:30', '13:00', '13:30',
  '18:00', '18:30', '19:00', '19:30', '20:00', '20:30', '21:00'
]

const availableTables = [
  { number: 1, capacity: 2 },
  { number: 2, capacity: 2 },
  { number: 3, capacity: 4 },
  { number: 4, capacity: 4 },
  { number: 5, capacity: 6 },
  { number: 6, capacity: 6 },
  { number: 7, capacity: 8 },
  { number: 8, capacity: 8 }
]

// Methods
const openReservationModal = () => {
  newReservation.value = {
    date: today.value,
    time: '',
    guests: 2,
    tableNumber: 0,
    specialRequests: ''
  }
  isReservationModalOpen.value = true
}

const closeReservationModal = () => {
  isReservationModalOpen.value = false
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const formatTime = (time: string) => {
  return new Date(`2000-01-01T${time}`).toLocaleTimeString('en-US', {
    hour: 'numeric',
    minute: 'numeric',
    hour12: true
  })
}

const incrementGuests = () => {
  if (newReservation.value.guests < 12) {
    newReservation.value.guests++
  }
}

const decrementGuests = () => {
  if (newReservation.value.guests > 1) {
    newReservation.value.guests--
  }
}

const selectTable = (table: { number: number; capacity: number }) => {
  newReservation.value.tableNumber = table.number
}

const submitReservation = () => {
  // TODO: Submit reservation to API
  const reservation = {
    id: Math.random().toString(36).substr(2, 9),
    ...newReservation.value
  }
  upcomingReservations.value.push(reservation)
  closeReservationModal()
}

const cancelReservation = (reservation: Reservation) => {
  // TODO: Cancel reservation via API
  const index = upcomingReservations.value.findIndex(r => r.id === reservation.id)
  if (index !== -1) {
    upcomingReservations.value.splice(index, 1)
  }
}

const bookSimilar = (reservation: Reservation) => {
  newReservation.value = {
    date: today.value,
    time: reservation.time,
    guests: reservation.guests,
    tableNumber: reservation.tableNumber,
    specialRequests: reservation.specialRequests || ''
  }
  isReservationModalOpen.value = true
}
</script> 