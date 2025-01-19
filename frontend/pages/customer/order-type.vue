<template>
  <div class="min-h-screen bg-white flex flex-col items-center justify-center px-4">
    <!-- Logo and Welcome Message -->
    <div class="text-center mb-8">
      <h1 class="text-[#FF6B00] text-4xl font-bold mb-2">Quisin</h1>
      <h2 class="text-2xl font-semibold text-gray-900">How would you like to order?</h2>
    </div>

    <!-- Order Type Selection -->
    <div class="space-y-6 w-full max-w-sm">
      <!-- Single Order -->
      <button
        @click="startSingleOrder"
        class="w-full bg-white border-2 border-[#FF6B00] rounded-lg p-6 text-left hover:bg-orange-50 transition-colors duration-200 group"
      >
        <div class="flex items-center justify-between">
          <div>
            <h3 class="text-lg font-medium text-gray-900">Single Order</h3>
            <p class="mt-1 text-sm text-gray-500">Order for yourself</p>
          </div>
          <UserIcon class="h-6 w-6 text-[#FF6B00]" />
        </div>
      </button>

      <!-- Group Order -->
      <button
        @click="startGroupOrder"
        class="w-full bg-white border-2 border-[#FF6B00] rounded-lg p-6 text-left hover:bg-orange-50 transition-colors duration-200 group"
      >
        <div class="flex items-center justify-between">
          <div>
            <h3 class="text-lg font-medium text-gray-900">Group Order</h3>
            <p class="mt-1 text-sm text-gray-500">Order together with friends</p>
          </div>
          <UserGroupIcon class="h-6 w-6 text-[#FF6B00]" />
        </div>
      </button>
    </div>

    <!-- Guest Name Modal -->
    <TransitionRoot as="template" :show="showGuestNameModal">
      <Dialog as="div" class="relative z-10" @close="closeGuestNameModal">
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
                  <div class="mt-3 text-center sm:mt-5">
                    <DialogTitle as="h3" class="text-base font-semibold leading-6 text-gray-900">
                      Enter Your Name
                    </DialogTitle>
                    <div class="mt-4">
                      <input
                        type="text"
                        v-model="guestName"
                        class="block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] sm:text-sm"
                        placeholder="Your name"
                        @keyup.enter="confirmGuestName"
                      />
                    </div>
                  </div>
                </div>
                <div class="mt-5 sm:mt-6">
                  <button
                    type="button"
                    class="inline-flex w-full justify-center rounded-md bg-[#FF6B00] px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-[#e66000] focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-[#FF6B00]"
                    @click="confirmGuestName"
                  >
                    Continue
                  </button>
                </div>
              </DialogPanel>
            </TransitionChild>
          </div>
        </div>
      </Dialog>
    </TransitionRoot>

    <!-- Floating Bell Icon -->
    <button
      @click="hailWaiter"
      :disabled="isHailButtonDisabled"
      class="fixed bottom-6 right-6 bg-[#FF6B00] text-white p-3 rounded-full shadow-lg hover:bg-[#e66000] transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
    >
      <BellIcon class="h-6 w-6" />
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'nuxt/app'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import { UserIcon, UserGroupIcon, BellIcon } from '@heroicons/vue/24/outline'

const router = useRouter()
const showGuestNameModal = ref(false)
const guestName = ref('')
const orderType = ref<'single' | 'group'>('single')
const isHailButtonDisabled = ref(false)

const startSingleOrder = () => {
  orderType.value = 'single'
  showGuestNameModal.value = true
}

const startGroupOrder = () => {
  orderType.value = 'group'
  showGuestNameModal.value = true
}

const closeGuestNameModal = () => {
  showGuestNameModal.value = false
  guestName.value = ''
}

const confirmGuestName = () => {
  if (!guestName.value.trim()) return

  if (orderType.value === 'single') {
    // Store guest name in localStorage or state management
    localStorage.setItem('guestName', guestName.value)
    router.push('/customer/menu')
  } else {
    // Store guest name and navigate to group order sharing page
    localStorage.setItem('guestName', guestName.value)
    router.push('/customer/group-order-share')
  }
}

const hailWaiter = async () => {
  try {
    const response = await fetch('/api/hail-waiter', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        tableNumber: localStorage.getItem('tableNumber'),
      }),
    })

    if (response.ok) {
      isHailButtonDisabled.value = true
      
      // Enable button after 5 minutes
      setTimeout(() => {
        isHailButtonDisabled.value = false
      }, 5 * 60 * 1000)
    }
  } catch (error) {
    console.error('Error hailing waiter:', error)
  }
}
</script> 