<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="sm:flex sm:items-center">
        <div class="sm:flex-auto">
          <h1 class="text-3xl font-bold text-gray-900">Reservation Management</h1>
          <p class="mt-2 text-sm text-gray-700">
            View and manage all restaurant reservations
          </p>
        </div>
        <div class="mt-4 sm:mt-0 sm:ml-16 sm:flex-none">
          <button
            @click="showAddReservationModal = true"
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
          >
            <PlusIcon class="h-4 w-4 mr-2" />
            Add Reservation
          </button>
        </div>
      </div>

      <!-- Filters -->
      <div class="mt-8 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div class="flex items-center gap-4">
          <input
            type="date"
            v-model="selectedDate"
            class="block w-48 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
          />
          <select
            v-model="selectedStatus"
            class="block w-40 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
          >
            <option value="">All Status</option>
            <option value="confirmed">Confirmed</option>
            <option value="pending">Pending</option>
            <option value="cancelled">Cancelled</option>
          </select>
        </div>
        <div class="relative">
          <input
            type="text"
            v-model="searchQuery"
            placeholder="Search reservations..."
            class="block w-full sm:w-64 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base pl-10"
          />
          <MagnifyingGlassIcon class="h-5 w-5 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
        </div>
      </div>

      <!-- Reservations Table -->
      <div class="mt-8 flex flex-col">
        <div class="-mx-4 -my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
          <div class="inline-block min-w-full py-2 align-middle md:px-6 lg:px-8">
            <div class="overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg">
              <table class="min-w-full divide-y divide-gray-300">
                <thead class="bg-gray-50">
                  <tr>
                    <th scope="col" class="py-3.5 pl-4 pr-3 text-left text-sm font-semibold text-gray-900 sm:pl-6">Guest Name</th>
                    <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">Date & Time</th>
                    <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">Party Size</th>
                    <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">Status</th>
                    <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">Contact</th>
                    <th scope="col" class="relative py-3.5 pl-3 pr-4 sm:pr-6">
                      <span class="sr-only">Actions</span>
                    </th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 bg-white">
                  <tr v-for="reservation in reservations" :key="reservation.id">
                    <td class="whitespace-nowrap py-4 pl-4 pr-3 text-sm font-medium text-gray-900 sm:pl-6">
                      {{ reservation.guestName }}
                    </td>
                    <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                      {{ reservation.dateTime }}
                    </td>
                    <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                      {{ reservation.partySize }} people
                    </td>
                    <td class="whitespace-nowrap px-3 py-4 text-sm">
                      <span
                        :class="[
                          'inline-flex rounded-full px-2 text-xs font-semibold leading-5',
                          {
                            'bg-green-100 text-green-800': reservation.status === 'confirmed',
                            'bg-yellow-100 text-yellow-800': reservation.status === 'pending',
                            'bg-red-100 text-red-800': reservation.status === 'cancelled'
                          }
                        ]"
                      >
                        {{ reservation.status }}
                      </span>
                    </td>
                    <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                      {{ reservation.contact }}
                    </td>
                    <td class="relative whitespace-nowrap py-4 pl-3 pr-4 text-right text-sm font-medium sm:pr-6">
                      <button
                        @click="editReservation(reservation)"
                        class="text-[#FF6B00] hover:text-[#e66000] mr-4"
                      >
                        Edit
                      </button>
                      <button
                        @click="cancelReservation(reservation.id)"
                        class="text-red-600 hover:text-red-900"
                      >
                        Cancel
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { PlusIcon, MagnifyingGlassIcon } from '@heroicons/vue/24/outline'

definePageMeta({
  layout: 'admin'
})

const showAddReservationModal = ref(false)
const selectedDate = ref('')
const selectedStatus = ref('')
const searchQuery = ref('')

// Mock data
const reservations = ref([
  {
    id: '1',
    guestName: 'John Smith',
    dateTime: '2024-02-21 19:00',
    partySize: 4,
    status: 'confirmed',
    contact: '+254 712 345 678'
  },
  {
    id: '2',
    guestName: 'Sarah Johnson',
    dateTime: '2024-02-21 20:30',
    partySize: 2,
    status: 'pending',
    contact: '+254 723 456 789'
  },
  {
    id: '3',
    guestName: 'Michael Brown',
    dateTime: '2024-02-22 18:00',
    partySize: 6,
    status: 'cancelled',
    contact: '+254 734 567 890'
  }
])

const editReservation = (reservation: any) => {
  // Implement edit functionality
}

const cancelReservation = (id: string) => {
  // Implement cancel functionality
}
</script> 