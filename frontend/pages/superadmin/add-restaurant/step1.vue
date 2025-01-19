<template>
  <div class="min-h-screen bg-gray-50 py-12">
    <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Progress Steps -->
      <nav aria-label="Progress">
        <ol role="list" class="flex items-center">
          <li class="relative flex-1">
            <div class="absolute inset-0 flex items-center" aria-hidden="true">
              <div class="h-0.5 w-full bg-[#FF6B00]"></div>
            </div>
            <div class="relative w-8 h-8 flex items-center justify-center bg-[#FF6B00] rounded-full">
              <svg class="w-5 h-5 text-white" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
              </svg>
              <span class="sr-only">Step 1</span>
            </div>
            <span class="absolute -bottom-6 w-full text-center text-sm font-medium text-[#FF6B00]">
              Restaurant Details
            </span>
          </li>

          <li class="relative flex-1">
            <div class="absolute inset-0 flex items-center" aria-hidden="true">
              <div class="h-0.5 w-full bg-gray-200"></div>
            </div>
            <div class="relative w-8 h-8 flex items-center justify-center bg-white border-2 border-gray-300 rounded-full">
              <span class="text-gray-500">2</span>
              <span class="sr-only">Step 2</span>
            </div>
            <span class="absolute -bottom-6 w-full text-center text-sm font-medium text-gray-500">
              Admin Details
            </span>
          </li>
        </ol>
      </nav>

      <!-- Form -->
      <div class="mt-16">
        <div class="bg-white shadow-xl rounded-lg p-6 sm:p-8">
          <h2 class="text-2xl font-bold text-gray-900 mb-8">Restaurant Information</h2>
          
          <form @submit.prevent="handleSubmit" class="space-y-6">
            <!-- Restaurant Name -->
            <div>
              <label for="name" class="block text-sm font-medium text-gray-700">Restaurant Name</label>
              <div class="mt-1">
                <input
                  type="text"
                  id="name"
                  v-model="form.name"
                  class="block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                  required
                />
              </div>
            </div>

            <!-- Logo Upload -->
            <div>
              <label class="block text-sm font-medium text-gray-700">Restaurant Logo</label>
              <div class="mt-1 flex justify-center px-6 pt-5 pb-6 border-2 border-gray-300 border-dashed rounded-md hover:border-[#FF6B00] transition-colors duration-200">
                <div class="space-y-1 text-center">
                  <svg
                    class="mx-auto h-12 w-12 text-gray-400"
                    stroke="currentColor"
                    fill="none"
                    viewBox="0 0 48 48"
                  >
                    <path
                      d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                  </svg>
                  <div class="flex text-sm text-gray-600">
                    <label
                      for="file-upload"
                      class="relative cursor-pointer rounded-md font-medium text-[#FF6B00] hover:text-[#e66000] focus-within:outline-none"
                    >
                      <span>Upload a file</span>
                      <input id="file-upload" name="file-upload" type="file" class="sr-only" @change="handleFileUpload" accept="image/*" />
                    </label>
                    <p class="pl-1">or drag and drop</p>
                  </div>
                  <p class="text-xs text-gray-500">PNG, JPG, GIF up to 10MB</p>
                </div>
              </div>
            </div>

            <!-- Location Details -->
            <div class="grid grid-cols-1 gap-6 sm:grid-cols-2">
              <div>
                <label for="country" class="block text-sm font-medium text-gray-700">Country</label>
                <select
                  id="country"
                  v-model="form.country"
                  class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                  required
                >
                  <option value="">Select Country</option>
                  <option v-for="country in countries" :key="country.code" :value="country.code">
                    {{ country.name }}
                  </option>
                </select>
              </div>

              <div>
                <label for="city" class="block text-sm font-medium text-gray-700">City</label>
                <input
                  type="text"
                  id="city"
                  v-model="form.city"
                  class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                  required
                />
              </div>

              <div class="sm:col-span-2">
                <label for="address" class="block text-sm font-medium text-gray-700">Street Address</label>
                <input
                  type="text"
                  id="address"
                  v-model="form.address"
                  class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                  required
                />
              </div>
            </div>

            <!-- Currency -->
            <div>
              <label for="currency" class="block text-sm font-medium text-gray-700">Currency</label>
              <select
                id="currency"
                v-model="form.currency"
                class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                required
              >
                <option value="">Select Currency</option>
                <option value="USD">USD - US Dollar</option>
                <option value="EUR">EUR - Euro</option>
                <option value="GBP">GBP - British Pound</option>
                <option value="JPY">JPY - Japanese Yen</option>
                <!-- Add more currencies as needed -->
              </select>
            </div>

            <!-- Contact Information -->
            <div class="grid grid-cols-1 gap-6 sm:grid-cols-2">
              <div>
                <label for="phone" class="block text-sm font-medium text-gray-700">Phone Number</label>
                <input
                  type="tel"
                  id="phone"
                  v-model="form.phone"
                  class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                  required
                />
              </div>

              <div>
                <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
                <input
                  type="email"
                  id="email"
                  v-model="form.email"
                  class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                  required
                />
              </div>
            </div>

            <!-- Navigation Buttons -->
            <div class="flex justify-end space-x-4 pt-6">
              <button
                type="button"
                @click="$router.push('/superadmin/dashboard')"
                class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
              >
                Cancel
              </button>
              <button
                type="submit"
                class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
              >
                Next Step
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
import { useRouter } from 'vue-router'

const router = useRouter()

const form = ref({
  name: '',
  logo: null as File | null,
  country: '',
  city: '',
  address: '',
  currency: '',
  phone: '',
  email: ''
})

const countries = [
  { code: 'US', name: 'United States' },
  { code: 'GB', name: 'United Kingdom' },
  { code: 'CA', name: 'Canada' },
  { code: 'AU', name: 'Australia' },
  { code: 'DE', name: 'Germany' },
  { code: 'FR', name: 'France' },
  { code: 'IT', name: 'Italy' },
  { code: 'ES', name: 'Spain' },
  { code: 'JP', name: 'Japan' },
  { code: 'CN', name: 'China' },
]

const handleFileUpload = (event: Event) => {
  const input = event.target as HTMLInputElement
  if (input.files && input.files[0]) {
    form.value.logo = input.files[0]
  }
}

const handleSubmit = () => {
  // Store form data in localStorage for step 2
  localStorage.setItem('restaurantData', JSON.stringify(form.value))
  // Navigate to step 2
  router.push('/superadmin/add-restaurant/step2')
}
</script>

<style scoped>
/* Add any component-specific styles here */
</style> 