<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="sm:flex sm:items-center">
        <div class="sm:flex-auto">
          <h1 class="text-3xl font-bold text-gray-900">Staff Management</h1>
          <p class="mt-2 text-sm text-gray-700">
            Manage your restaurant staff and their roles
          </p>
        </div>
        <div class="mt-4 sm:mt-0 sm:ml-16 sm:flex-none">
          <button
            @click="showAddStaffModal = true"
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
          >
            <PlusIcon class="h-4 w-4 mr-2" />
            Add Staff Member
          </button>
        </div>
      </div>

      <!-- Filters -->
      <div class="mt-8 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div class="flex items-center gap-4">
          <select
            v-model="selectedRole"
            class="block w-40 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
          >
            <option value="">All Roles</option>
            <option value="manager">Manager</option>
            <option value="chef">Chef</option>
            <option value="waiter">Waiter</option>
            <option value="cashier">Cashier</option>
          </select>
          <select
            v-model="selectedStatus"
            class="block w-40 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
          >
            <option value="">All Status</option>
            <option value="active">Active</option>
            <option value="inactive">Inactive</option>
          </select>
        </div>
        <div class="relative">
          <input
            type="text"
            v-model="searchQuery"
            placeholder="Search staff..."
            class="block w-full sm:w-64 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base pl-10"
          />
          <MagnifyingGlassIcon class="h-5 w-5 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
        </div>
      </div>

      <!-- Staff Table -->
      <div class="mt-8 flex flex-col">
        <div class="-mx-4 -my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
          <div class="inline-block min-w-full py-2 align-middle md:px-6 lg:px-8">
            <div class="overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg">
              <table class="min-w-full divide-y divide-gray-300">
                <thead class="bg-gray-50">
                  <tr>
                    <th scope="col" class="py-3.5 pl-4 pr-3 text-left text-sm font-semibold text-gray-900 sm:pl-6">Name</th>
                    <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">Role</th>
                    <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">Email</th>
                    <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">Phone</th>
                    <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">Status</th>
                    <th scope="col" class="relative py-3.5 pl-3 pr-4 sm:pr-6">
                      <span class="sr-only">Actions</span>
                    </th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 bg-white">
                  <tr v-for="staff in staffMembers" :key="staff.id">
                    <td class="whitespace-nowrap py-4 pl-4 pr-3 text-sm sm:pl-6">
                      <div class="flex items-center">
                        <div class="h-10 w-10 flex-shrink-0">
                          <img
                            v-if="staff.avatar"
                            :src="staff.avatar"
                            :alt="staff.name"
                            class="h-10 w-10 rounded-full"
                          />
                          <div
                            v-else
                            class="h-10 w-10 rounded-full bg-gray-200 flex items-center justify-center"
                          >
                            <UserIcon class="h-5 w-5 text-gray-500" />
                          </div>
                        </div>
                        <div class="ml-4">
                          <div class="font-medium text-gray-900">{{ staff.name }}</div>
                        </div>
                      </div>
                    </td>
                    <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">{{ staff.role }}</td>
                    <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">{{ staff.email }}</td>
                    <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">{{ staff.phone }}</td>
                    <td class="whitespace-nowrap px-3 py-4 text-sm">
                      <span
                        :class="[
                          'inline-flex rounded-full px-2 text-xs font-semibold leading-5',
                          staff.status === 'active'
                            ? 'bg-green-100 text-green-800'
                            : 'bg-red-100 text-red-800'
                        ]"
                      >
                        {{ staff.status }}
                      </span>
                    </td>
                    <td class="relative whitespace-nowrap py-4 pl-3 pr-4 text-right text-sm font-medium sm:pr-6">
                      <button
                        @click="editStaff(staff)"
                        class="text-[#FF6B00] hover:text-[#e66000] mr-4"
                      >
                        Edit
                      </button>
                      <button
                        @click="deactivateStaff(staff.id)"
                        class="text-red-600 hover:text-red-900"
                      >
                        {{ staff.status === 'active' ? 'Deactivate' : 'Activate' }}
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
import { PlusIcon, MagnifyingGlassIcon, UserIcon } from '@heroicons/vue/24/outline'

definePageMeta({
  layout: 'admin'
})

const showAddStaffModal = ref(false)
const selectedRole = ref('')
const selectedStatus = ref('')
const searchQuery = ref('')

// Mock data
const staffMembers = ref([
  {
    id: '1',
    name: 'John Smith',
    role: 'Manager',
    email: 'john.smith@quisin.com',
    phone: '+254 712 345 678',
    status: 'active',
    avatar: null
  },
  {
    id: '2',
    name: 'Sarah Johnson',
    role: 'Chef',
    email: 'sarah.johnson@quisin.com',
    phone: '+254 723 456 789',
    status: 'active',
    avatar: null
  },
  {
    id: '3',
    name: 'Michael Brown',
    role: 'Waiter',
    email: 'michael.brown@quisin.com',
    phone: '+254 734 567 890',
    status: 'inactive',
    avatar: null
  }
])

const editStaff = (staff: any) => {
  // Implement edit functionality
}

const deactivateStaff = (id: string) => {
  // Implement deactivate functionality
}
</script> 