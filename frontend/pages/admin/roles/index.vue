<script setup lang="ts">
import { ref, computed } from 'vue'
import { 
  UserGroupIcon,
  PlusCircleIcon,
  PencilSquareIcon,
  TrashIcon,
  CheckIcon,
  XMarkIcon
} from '@heroicons/vue/24/outline'

interface Permission {
  id: string
  name: string
  description: string
  key: string
}

interface Role {
  id: string
  name: string
  description: string
  permissions: string[]
  createdAt: string
}

const showAddModal = ref(false)
const showEditModal = ref(false)
const showDeleteConfirm = ref(false)
const selectedRole = ref<Role | null>(null)
const searchQuery = ref('')

const newRole = ref({
  name: '',
  description: '',
  permissions: [] as string[]
})

const availablePermissions: Permission[] = [
  {
    id: '1',
    name: 'Menu Management',
    description: 'Can create, edit, and delete menu items',
    key: 'menu.manage'
  },
  {
    id: '2',
    name: 'Order Management',
    description: 'Can view and process orders',
    key: 'order.manage'
  },
  {
    id: '3',
    name: 'Staff Management',
    description: 'Can manage staff members and roles',
    key: 'staff.manage'
  },
  {
    id: '4',
    name: 'Inventory Management',
    description: 'Can manage inventory and stock levels',
    key: 'inventory.manage'
  },
  {
    id: '5',
    name: 'Financial Management',
    description: 'Can access financial reports and transactions',
    key: 'finance.manage'
  }
]

const roles = ref<Role[]>([
  {
    id: '1',
    name: 'Manager',
    description: 'Full access to restaurant management features',
    permissions: ['menu.manage', 'order.manage', 'staff.manage', 'inventory.manage', 'finance.manage'],
    createdAt: '2024-02-01'
  },
  {
    id: '2',
    name: 'Waiter',
    description: 'Can manage orders and view menu',
    permissions: ['order.manage'],
    createdAt: '2024-02-01'
  },
  {
    id: '3',
    name: 'Kitchen Staff',
    description: 'Can view and process orders',
    permissions: ['order.manage'],
    createdAt: '2024-02-01'
  }
])

const filteredRoles = computed(() => {
  return roles.value.filter(role => 
    role.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    role.description.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

const openAddModal = () => {
  showAddModal.value = true
  newRole.value = {
    name: '',
    description: '',
    permissions: []
  }
}

const closeAddModal = () => {
  showAddModal.value = false
}

const addRole = () => {
  const id = (roles.value.length + 1).toString()
  roles.value.push({
    ...newRole.value,
    id,
    createdAt: new Date().toISOString().split('T')[0]
  })
  closeAddModal()
}

const openEditModal = (role: Role) => {
  selectedRole.value = role
  newRole.value = {
    name: role.name,
    description: role.description,
    permissions: [...role.permissions]
  }
  showEditModal.value = true
}

const closeEditModal = () => {
  showEditModal.value = false
  selectedRole.value = null
}

const updateRole = () => {
  if (!selectedRole.value) return
  const index = roles.value.findIndex(r => r.id === selectedRole.value?.id)
  if (index !== -1) {
    roles.value[index] = {
      ...roles.value[index],
      ...newRole.value
    }
  }
  closeEditModal()
}

const openDeleteConfirm = (role: Role) => {
  selectedRole.value = role
  showDeleteConfirm.value = true
}

const closeDeleteConfirm = () => {
  showDeleteConfirm.value = false
  selectedRole.value = null
}

const deleteRole = () => {
  if (!selectedRole.value) return
  roles.value = roles.value.filter(r => r.id !== selectedRole.value?.id)
  closeDeleteConfirm()
}

const hasPermission = (role: Role, permissionKey: string) => {
  return role.permissions.includes(permissionKey)
}
</script>

<template>
  <div class="p-6">
    <div class="flex justify-between items-center mb-6">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Role Management</h1>
        <p class="mt-1 text-sm text-gray-600">Create and manage staff roles and permissions</p>
      </div>
      <button
        @click="openAddModal"
        class="flex items-center gap-2 px-4 py-2 bg-[#FF6B00] text-white rounded-lg hover:bg-[#E65A00]"
      >
        <PlusCircleIcon class="h-5 w-5" />
        Create New Role
      </button>
    </div>

    <!-- Search -->
    <div class="mb-6">
      <input
        type="text"
        v-model="searchQuery"
        placeholder="Search roles..."
        class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
      />
    </div>

    <!-- Roles List -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Role Name
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Description
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Permissions
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Created
            </th>
            <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
              Actions
            </th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="role in filteredRoles" :key="role.id">
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm font-medium text-gray-900">{{ role.name }}</div>
            </td>
            <td class="px-6 py-4">
              <div class="text-sm text-gray-600">{{ role.description }}</div>
            </td>
            <td class="px-6 py-4">
              <div class="flex flex-wrap gap-2">
                <span
                  v-for="permission in availablePermissions"
                  :key="permission.id"
                  :class="{
                    'px-2 py-1 text-xs rounded-full flex items-center gap-1': true,
                    'bg-[#FF6B00] bg-opacity-10 text-[#FF6B00]': hasPermission(role, permission.key),
                    'bg-gray-100 text-gray-800': !hasPermission(role, permission.key)
                  }"
                >
                  <CheckIcon v-if="hasPermission(role, permission.key)" class="h-3 w-3" />
                  <XMarkIcon v-else class="h-3 w-3" />
                  {{ permission.name }}
                </span>
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-500">{{ role.createdAt }}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <button
                @click="openEditModal(role)"
                class="text-[#FF6B00] hover:text-[#E65A00] mr-3"
              >
                <PencilSquareIcon class="h-5 w-5" />
              </button>
              <button
                @click="openDeleteConfirm(role)"
                class="text-red-600 hover:text-red-900"
              >
                <TrashIcon class="h-5 w-5" />
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Add/Edit Role Modal -->
    <div v-if="showAddModal || showEditModal" class="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center">
      <div class="bg-white rounded-lg p-6 max-w-2xl w-full">
        <h2 class="text-xl font-bold mb-4">{{ showEditModal ? 'Edit Role' : 'Create New Role' }}</h2>
        <form @submit.prevent="showEditModal ? updateRole() : addRole()" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700">Role Name</label>
            <input
              type="text"
              v-model="newRole.name"
              placeholder="e.g., Waiter, Kitchen Staff, Manager"
              required
              class="mt-1 block w-full border rounded-md shadow-sm py-2 px-3 focus:ring focus:ring-[#FF6B00]"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">Description</label>
            <textarea
              v-model="newRole.description"
              placeholder="Describe the role's responsibilities"
              required
              class="mt-1 block w-full border rounded-md shadow-sm py-2 px-3 focus:ring focus:ring-blue-500"
              rows="3"
            ></textarea>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Permissions</label>
            <div class="space-y-2">
              <div
                v-for="permission in availablePermissions"
                :key="permission.id"
                class="flex items-start"
              >
                <div class="flex items-center h-5">
                  <input
                    type="checkbox"
                    :id="permission.key"
                    v-model="newRole.permissions"
                    :value="permission.key"
                    class="h-4 w-4 text-[#FF6B00] focus:ring-[#FF6B00] border-gray-300 rounded"
                  />
                </div>
                <div class="ml-3">
                  <label :for="permission.key" class="font-medium text-gray-700">{{ permission.name }}</label>
                  <p class="text-gray-500 text-sm">{{ permission.description }}</p>
                </div>
              </div>
            </div>
          </div>
          <div class="flex justify-end gap-3 mt-6">
            <button
              type="button"
              @click="showEditModal ? closeEditModal() : closeAddModal()"
              class="px-4 py-2 border rounded-md text-gray-700 hover:bg-gray-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              class="px-4 py-2 bg-[#FF6B00] text-white rounded-md hover:bg-[#E65A00]"
            >
              {{ showEditModal ? 'Update Role' : 'Create Role' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div v-if="showDeleteConfirm" class="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center">
      <div class="bg-white rounded-lg p-6 max-w-md w-full">
        <h2 class="text-xl font-bold mb-4">Confirm Delete</h2>
        <p class="text-gray-600 mb-6">
          Are you sure you want to delete the role "{{ selectedRole?.name }}"? This action cannot be undone.
        </p>
        <div class="flex justify-end gap-3">
          <button
            @click="closeDeleteConfirm"
            class="px-4 py-2 border rounded-md text-gray-700 hover:bg-gray-50"
          >
            Cancel
          </button>
          <button
            @click="deleteRole"
            class="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700"
          >
            Delete
          </button>
        </div>
      </div>
    </div>
  </div>
</template> 