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
              <div class="h-0.5 w-full bg-[#FF6B00]"></div>
            </div>
            <div class="relative w-8 h-8 flex items-center justify-center bg-[#FF6B00] rounded-full">
              <svg class="w-5 h-5 text-white" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
              </svg>
              <span class="sr-only">Step 2</span>
            </div>
            <span class="absolute -bottom-6 w-full text-center text-sm font-medium text-[#FF6B00]">
              Admin Details
            </span>
          </li>
        </ol>
      </nav>

      <!-- Form -->
      <div class="mt-16">
        <div class="bg-white shadow-xl rounded-lg p-6 sm:p-8">
          <h2 class="text-2xl font-bold text-gray-900 mb-8">Admin Information</h2>
          
          <form @submit.prevent="handleSubmit" class="space-y-6">
            <!-- Personal Information -->
            <div class="grid grid-cols-1 gap-6 sm:grid-cols-2">
              <div>
                <label for="firstName" class="block text-sm font-medium text-gray-700">First Name</label>
                <input
                  type="text"
                  id="firstName"
                  v-model="form.firstName"
                  class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                  required
                />
              </div>

              <div>
                <label for="lastName" class="block text-sm font-medium text-gray-700">Last Name</label>
                <input
                  type="text"
                  id="lastName"
                  v-model="form.lastName"
                  class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                  required
                />
              </div>
            </div>

            <!-- Contact Information -->
            <div class="grid grid-cols-1 gap-6 sm:grid-cols-2">
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
            </div>

            <!-- Password Section -->
            <div class="space-y-6">
              <div>
                <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
                <div class="mt-1 relative rounded-md shadow-sm">
                  <input
                    :type="showPassword ? 'text' : 'password'"
                    id="password"
                    v-model="form.password"
                    class="block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] pr-10"
                    required
                    minlength="8"
                  />
                  <button
                    type="button"
                    @click="showPassword = !showPassword"
                    class="absolute inset-y-0 right-0 pr-3 flex items-center"
                  >
                    <svg
                      class="h-5 w-5 text-gray-400"
                      xmlns="http://www.w3.org/2000/svg"
                      viewBox="0 0 20 20"
                      fill="currentColor"
                      v-if="showPassword"
                    >
                      <path d="M10 12a2 2 0 100-4 2 2 0 000 4z" />
                      <path fill-rule="evenodd" d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z" clip-rule="evenodd" />
                    </svg>
                    <svg
                      class="h-5 w-5 text-gray-400"
                      xmlns="http://www.w3.org/2000/svg"
                      viewBox="0 0 20 20"
                      fill="currentColor"
                      v-else
                    >
                      <path fill-rule="evenodd" d="M3.707 2.293a1 1 0 00-1.414 1.414l14 14a1 1 0 001.414-1.414l-1.473-1.473A10.014 10.014 0 0019.542 10C18.268 5.943 14.478 3 10 3a9.958 9.958 0 00-4.512 1.074l-1.78-1.781zm4.261 4.26l1.514 1.515a2.003 2.003 0 012.45 2.45l1.514 1.514a4 4 0 00-5.478-5.478z" clip-rule="evenodd" />
                      <path d="M12.454 16.697L9.75 13.992a4 4 0 01-3.742-3.741L2.335 6.578A9.98 9.98 0 00.458 10c1.274 4.057 5.065 7 9.542 7 .847 0 1.669-.105 2.454-.303z" />
                    </svg>
                  </button>
                </div>
                <p class="mt-1 text-sm text-gray-500">Password must be at least 8 characters long</p>
              </div>

              <div>
                <label for="confirmPassword" class="block text-sm font-medium text-gray-700">Confirm Password</label>
                <div class="mt-1">
                  <input
                    :type="showPassword ? 'text' : 'password'"
                    id="confirmPassword"
                    v-model="form.confirmPassword"
                    class="block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00]"
                    required
                  />
                </div>
              </div>
            </div>

            <!-- Navigation Buttons -->
            <div class="flex justify-between pt-6">
              <button
                type="button"
                @click="$router.push('/superadmin/add-restaurant/step1')"
                class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
              >
                Back
              </button>
              <div class="flex space-x-4">
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
                  Create Restaurant
                </button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const showPassword = ref(false)

const form = ref({
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

onMounted(() => {
  // Check if we have restaurant data from step 1
  const restaurantData = localStorage.getItem('restaurantData')
  if (!restaurantData) {
    router.push('/superadmin/add-restaurant/step1')
  }
})

const handleSubmit = async () => {
  if (form.value.password !== form.value.confirmPassword) {
    alert('Passwords do not match')
    return
  }

  try {
    const restaurantData = JSON.parse(localStorage.getItem('restaurantData') || '{}')
    const completeData = {
      restaurant: restaurantData,
      admin: form.value
    }

    // TODO: Send data to backend API
    console.log('Submitting data:', completeData)

    // Clear stored data
    localStorage.removeItem('restaurantData')

    // Show success message
    alert('Restaurant created successfully!')

    // Redirect to dashboard
    router.push('/superadmin/dashboard')
  } catch (error) {
    console.error('Error creating restaurant:', error)
    alert('Error creating restaurant. Please try again.')
  }
}
</script>

<style scoped>
/* Add any component-specific styles here */
</style> 