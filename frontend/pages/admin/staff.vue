<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center">
          <h1 class="text-3xl font-bold text-gray-900">Staff Management</h1>
          <button
            @click="openAddStaffModal"
            class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700"
          >
            Add Staff Member
          </button>
        </div>
      </div>
    </header>

    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Staff List -->
      <div class="bg-white shadow overflow-hidden sm:rounded-lg">
        <div class="px-4 py-5 sm:p-6">
          <div class="flex justify-between mb-4">
            <div class="flex space-x-4">
              <select
                v-model="filterRole"
                class="rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
              >
                <option value="all">All Roles</option>
                <option value="waiter">Waiter</option>
                <option value="chef">Chef</option>
                <option value="manager">Manager</option>
                <option value="cashier">Cashier</option>
              </select>
              <input
                type="text"
                v-model="searchQuery"
                placeholder="Search staff..."
                class="rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
              />
            </div>
          </div>

          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Name
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Role
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Contact
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Joined
                  </th>
                  <th scope="col" class="relative px-6 py-3">
                    <span class="sr-only">Actions</span>
                  </th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="staff in filteredStaff" :key="staff.id">
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="flex items-center">
                      <div class="h-10 w-10 flex-shrink-0">
                        <img
                          class="h-10 w-10 rounded-full"
                          :src="staff.avatar"
                          :alt="staff.name"
                        />
                      </div>
                      <div class="ml-4">
                        <div class="text-sm font-medium text-gray-900">{{ staff.name }}</div>
                        <div class="text-sm text-gray-500">{{ staff.email }}</div>
                      </div>
                    </div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span
                      class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                      :class="roleClasses[staff.role]"
                    >
                      {{ staff.role }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span
                      class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                      :class="staff.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
                    >
                      {{ staff.isActive ? 'Active' : 'Inactive' }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {{ staff.phone }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {{ formatDate(staff.joinedDate) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <button
                      @click="editStaff(staff)"
                      class="text-blue-600 hover:text-blue-900 mr-4"
                    >
                      Edit
                    </button>
                    <button
                      @click="toggleStaffStatus(staff)"
                      class="text-gray-600 hover:text-gray-900"
                    >
                      {{ staff.isActive ? 'Deactivate' : 'Activate' }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface Staff {
  id: string
  name: string
  email: string
  phone: string
  role: 'waiter' | 'chef' | 'manager' | 'cashier'
  isActive: boolean
  joinedDate: string
  avatar: string
}

const staff = ref<Staff[]>([
  {
    id: '1',
    name: 'John Smith',
    email: 'john.smith@example.com',
    phone: '(555) 123-4567',
    role: 'manager',
    isActive: true,
    joinedDate: '2023-01-15',
    avatar: 'https://ui-avatars.com/api/?name=John+Smith'
  },
  {
    id: '2',
    name: 'Sarah Johnson',
    email: 'sarah.j@example.com',
    phone: '(555) 234-5678',
    role: 'chef',
    isActive: true,
    joinedDate: '2023-03-20',
    avatar: 'https://ui-avatars.com/api/?name=Sarah+Johnson'
  },
  {
    id: '3',
    name: 'Mike Wilson',
    email: 'mike.w@example.com',
    phone: '(555) 345-6789',
    role: 'waiter',
    isActive: true,
    joinedDate: '2023-06-10',
    avatar: 'https://ui-avatars.com/api/?name=Mike+Wilson'
  }
])

const filterRole = ref('all')
const searchQuery = ref('')

const roleClasses = {
  waiter: 'bg-blue-100 text-blue-800',
  chef: 'bg-yellow-100 text-yellow-800',
  manager: 'bg-purple-100 text-purple-800',
  cashier: 'bg-green-100 text-green-800'
}

const filteredStaff = computed(() => {
  let result = staff.value

  if (filterRole.value !== 'all') {
    result = result.filter(s => s.role === filterRole.value)
  }

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(s => 
      s.name.toLowerCase().includes(query) ||
      s.email.toLowerCase().includes(query) ||
      s.phone.includes(query)
    )
  }

  return result
})

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const openAddStaffModal = () => {
  // TODO: Implement add staff modal
  console.log('Opening add staff modal')
}

const editStaff = (staffMember: Staff) => {
  // TODO: Implement edit staff modal
  console.log('Editing staff member:', staffMember.id)
}

const toggleStaffStatus = async (staffMember: Staff) => {
  const action = staffMember.isActive ? 'deactivate' : 'activate'
  if (confirm(`Are you sure you want to ${action} ${staffMember.name}?`)) {
    try {
      // TODO: Implement API call to toggle staff status
      staffMember.isActive = !staffMember.isActive
    } catch (error) {
      console.error(`Error ${action}ing staff member:`, error)
    }
  }
}
</script> 