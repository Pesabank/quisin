<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="sm:flex sm:items-center">
        <div class="sm:flex-auto">
          <h1 class="text-3xl font-bold text-gray-900">Menu Categories</h1>
          <p class="mt-2 text-sm text-gray-700">
            Manage your menu categories and organize your dishes
          </p>
        </div>
        <div class="mt-4 sm:mt-0 sm:ml-16 sm:flex-none">
          <button
            @click="showAddCategoryModal = true"
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
          >
            <PlusIcon class="h-4 w-4 mr-2" />
            Add Category
          </button>
        </div>
      </div>

      <!-- Tabs -->
      <div class="mt-6 border-b border-gray-200">
        <nav class="-mb-px flex space-x-8" aria-label="Tabs">
          <NuxtLink
            to="/admin/menu"
            class="border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm"
          >
            Menu Items
          </NuxtLink>
          <NuxtLink
            to="/admin/menu/categories"
            class="border-[#FF6B00] text-[#FF6B00] whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm"
          >
            Categories
          </NuxtLink>
          <NuxtLink
            to="/admin/menu/stock"
            class="border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm"
          >
            Stock Management
          </NuxtLink>
        </nav>
      </div>

      <!-- Categories List -->
      <div class="mt-8">
        <div class="flex flex-col">
          <div class="-my-2 -mx-4 overflow-x-auto sm:-mx-6 lg:-mx-8">
            <div class="inline-block min-w-full py-2 align-middle md:px-6 lg:px-8">
              <div class="overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg">
                <table class="min-w-full divide-y divide-gray-300">
                  <thead class="bg-gray-50">
                    <tr>
                      <th scope="col" class="py-3.5 pl-4 pr-3 text-left text-sm font-semibold text-gray-900 sm:pl-6">
                        Category Name
                      </th>
                      <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                        Items Count
                      </th>
                      <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                        Status
                      </th>
                      <th scope="col" class="relative py-3.5 pl-3 pr-4 sm:pr-6">
                        <span class="sr-only">Actions</span>
                      </th>
                    </tr>
                  </thead>
                  <tbody class="divide-y divide-gray-200 bg-white">
                    <tr v-for="category in categories" :key="category">
                      <td class="whitespace-nowrap py-4 pl-4 pr-3 text-sm font-medium text-gray-900 sm:pl-6">
                        {{ category }}
                      </td>
                      <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                        {{ getItemsCount(category) }}
                      </td>
                      <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                          Active
                        </span>
                      </td>
                      <td class="relative whitespace-nowrap py-4 pl-3 pr-4 text-right text-sm font-medium sm:pr-6">
                        <button
                          @click="editCategory(category)"
                          class="text-[#FF6B00] hover:text-[#e66000] mr-4"
                        >
                          Edit
                        </button>
                        <button
                          @click="deleteCategory(category)"
                          class="text-red-600 hover:text-red-900"
                        >
                          Delete
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
  </div>

  <!-- Add Category Modal -->
  <TransitionRoot appear :show="showAddCategoryModal" as="template">
    <Dialog as="div" @close="showAddCategoryModal = false" class="relative z-10">
      <TransitionChild
        as="template"
        enter="duration-300 ease-out"
        enter-from="opacity-0"
        enter-to="opacity-100"
        leave="duration-200 ease-in"
        leave-from="opacity-100"
        leave-to="opacity-0"
      >
        <div class="fixed inset-0 bg-black bg-opacity-25" />
      </TransitionChild>

      <div class="fixed inset-0 overflow-y-auto">
        <div class="flex min-h-full items-center justify-center p-4 text-center">
          <TransitionChild
            as="template"
            enter="duration-300 ease-out"
            enter-from="opacity-0 scale-95"
            enter-to="opacity-100 scale-100"
            leave="duration-200 ease-in"
            leave-from="opacity-100 scale-100"
            leave-to="opacity-0 scale-95"
          >
            <DialogPanel class="w-full max-w-md transform overflow-hidden rounded-2xl bg-white p-6 text-left align-middle shadow-xl transition-all">
              <DialogTitle as="h3" class="text-lg font-medium leading-6 text-gray-900">
                Add New Category
              </DialogTitle>

              <form @submit.prevent="addCategory" class="mt-4">
                <div class="space-y-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Category Name</label>
                    <input
                      type="text"
                      v-model="newCategory.name"
                      class="block w-full h-12 px-3 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
                      required
                    />
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Description</label>
                    <textarea
                      v-model="newCategory.description"
                      rows="3"
                      class="block w-full px-3 py-2 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
                    ></textarea>
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Status</label>
                    <select
                      v-model="newCategory.status"
                      class="block w-full h-12 px-3 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
                      required
                    >
                      <option value="active">Active</option>
                      <option value="inactive">Inactive</option>
                    </select>
                  </div>
                </div>

                <div class="mt-6 flex justify-end space-x-3">
                  <button
                    type="button"
                    class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
                    @click="showAddCategoryModal = false"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    class="px-4 py-2 border border-transparent rounded-md text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000]"
                  >
                    Add Category
                  </button>
                </div>
              </form>
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { PlusIcon } from '@heroicons/vue/24/outline'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'

definePageMeta({
  layout: 'admin'
})

// State
const showAddCategoryModal = ref(false)
const categories = ref(['Main Course', 'Appetizers', 'Desserts', 'Beverages'])
const newCategory = ref({
  name: '',
  description: '',
  status: 'active'
})

// Methods
const getItemsCount = (category: string) => {
  // TODO: Implement actual count
  return Math.floor(Math.random() * 10) + 1
}

const editCategory = (category: string) => {
  // TODO: Implement category editing
}

const deleteCategory = (category: string) => {
  // TODO: Implement category deletion
}

const addCategory = () => {
  if (newCategory.value.name) {
    categories.value.push(newCategory.value.name)
    // Reset form
    newCategory.value = {
      name: '',
      description: '',
      status: 'active'
    }
    showAddCategoryModal.value = false
    // TODO: API call to add category
  }
}
</script> 