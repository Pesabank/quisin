<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="lg:grid lg:grid-cols-2 lg:gap-x-12 xl:gap-x-16">
        <!-- Order Summary -->
        <div class="lg:col-span-1">
          <div class="bg-white shadow-sm rounded-lg">
            <div class="px-4 py-6 sm:px-6">
              <h2 class="text-lg font-medium text-gray-900">Group Order Summary</h2>
              
              <!-- Member Orders -->
              <div class="mt-6">
                <div v-for="member in groupMembers" :key="member.id" class="mb-8">
                  <div class="flex items-center justify-between mb-4">
                    <div class="flex items-center">
                      <UserCircleIcon class="h-8 w-8 text-gray-400" />
                      <h3 class="ml-3 text-base font-medium text-gray-900">{{ member.name }}</h3>
                    </div>
                    <span class="text-sm text-gray-500">{{ member.status }}</span>
                  </div>

                  <ul role="list" class="divide-y divide-gray-200">
                    <li v-for="item in member.items" :key="item.id" class="py-4">
                      <div class="flex items-start">
                        <img
                          :src="item.image"
                          :alt="item.name"
                          class="h-16 w-16 rounded-md object-cover"
                        />
                        <div class="ml-4 flex-1">
                          <div class="flex justify-between">
                            <div>
                              <h4 class="text-sm font-medium text-gray-900">{{ item.name }}</h4>
                              <p class="mt-1 text-sm text-gray-500">Qty: {{ item.quantity }}</p>
                            </div>
                            <p class="text-sm font-medium text-gray-900">
                              {{ formatPrice(item.price * item.quantity) }}
                            </p>
                          </div>
                          <div class="mt-2">
                            <p class="text-sm text-gray-500 italic" v-if="item.comments">
                              "{{ item.comments }}"
                            </p>
                          </div>
                        </div>
                      </div>
                    </li>
                  </ul>

                  <div class="mt-4 border-t border-gray-200 pt-4">
                    <div class="flex justify-between text-sm">
                      <span class="text-gray-500">Subtotal</span>
                      <span class="font-medium text-gray-900">{{ formatPrice(getMemberSubtotal(member)) }}</span>
                    </div>
                    <div class="mt-1 flex justify-between text-sm">
                      <span class="text-gray-500">Tax (10%)</span>
                      <span class="font-medium text-gray-900">{{ formatPrice(getMemberTax(member)) }}</span>
                    </div>
                    <div class="mt-2 flex justify-between text-base font-medium text-gray-900">
                      <span>Total</span>
                      <span>{{ formatPrice(getMemberTotal(member)) }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Group Total -->
              <div class="mt-8 border-t border-gray-200 pt-6">
                <div class="flex justify-between text-base font-medium text-gray-900">
                  <p>Group Subtotal</p>
                  <p>{{ formatPrice(groupSubtotal) }}</p>
                </div>
                <div class="mt-2 flex justify-between text-sm text-gray-500">
                  <p>Tax (10%)</p>
                  <p>{{ formatPrice(groupTax) }}</p>
                </div>
                <div class="mt-2 flex justify-between text-lg font-medium text-gray-900">
                  <p>Group Total</p>
                  <p>{{ formatPrice(groupTotal) }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Payment Section -->
        <div class="mt-10 lg:mt-0">
          <div class="bg-white shadow-sm rounded-lg">
            <div class="px-4 py-6 sm:px-6">
              <h2 class="text-lg font-medium text-gray-900">Payment</h2>

              <!-- Bill Splitting Options -->
              <div class="mt-6">
                <h3 class="text-base font-medium text-gray-900">Bill Splitting</h3>
                <div class="mt-4 space-y-4">
                  <div class="flex items-center">
                    <input
                      type="radio"
                      id="split-equal"
                      v-model="splitMethod"
                      value="equal"
                      class="h-4 w-4 border-gray-300 text-[#FF6B00] focus:ring-[#FF6B00]"
                    />
                    <label for="split-equal" class="ml-3 block text-sm font-medium text-gray-700">
                      Split Equally ({{ formatPrice(equalSplitAmount) }} per person)
                    </label>
                  </div>
                  <div class="flex items-center">
                    <input
                      type="radio"
                      id="split-individual"
                      v-model="splitMethod"
                      value="individual"
                      class="h-4 w-4 border-gray-300 text-[#FF6B00] focus:ring-[#FF6B00]"
                    />
                    <label for="split-individual" class="ml-3 block text-sm font-medium text-gray-700">
                      Pay Individual Orders
                    </label>
                  </div>
                </div>
              </div>

              <!-- Payment Method -->
              <div class="mt-8">
                <h3 class="text-base font-medium text-gray-900">Payment Method</h3>
                <div class="mt-4 space-y-4">
                  <div class="flex items-center">
                    <input
                      type="radio"
                      id="card"
                      v-model="paymentMethod"
                      value="card"
                      class="h-4 w-4 border-gray-300 text-[#FF6B00] focus:ring-[#FF6B00]"
                    />
                    <label for="card" class="ml-3">
                      <span class="block text-sm font-medium text-gray-700">Credit/Debit Card</span>
                      <div class="mt-1 flex space-x-2">
                        <img src="/images/visa.svg" alt="Visa" class="h-8" />
                        <img src="/images/mastercard.svg" alt="Mastercard" class="h-8" />
                        <img src="/images/amex.svg" alt="American Express" class="h-8" />
                      </div>
                    </label>
                  </div>
                  <div class="flex items-center">
                    <input
                      type="radio"
                      id="mpesa"
                      v-model="paymentMethod"
                      value="mpesa"
                      class="h-4 w-4 border-gray-300 text-[#FF6B00] focus:ring-[#FF6B00]"
                    />
                    <label for="mpesa" class="ml-3 block text-sm font-medium text-gray-700">
                      M-PESA
                    </label>
                  </div>
                </div>
              </div>

              <!-- Pay Button -->
              <div class="mt-8">
                <button
                  @click="processPayment"
                  class="w-full flex justify-center items-center px-6 py-3 border border-transparent rounded-md shadow-sm text-base font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
                >
                  {{ splitMethod === 'equal' ? 'Pay ' + formatPrice(equalSplitAmount) : 'Pay ' + formatPrice(groupTotal) }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Floating Bell Icon -->
    <button
      @click="hailWaiter"
      :disabled="isHailButtonDisabled"
      class="fixed bottom-6 right-6 bg-[#FF6B00] text-white p-3 rounded-full shadow-lg hover:bg-[#e66000] transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
    >
      <BellIcon class="h-6 w-6" />
    </button>

    <!-- Success Modal -->
    <TransitionRoot as="template" :show="showSuccessModal">
      <Dialog as="div" class="relative z-10" @close="closeSuccessModal">
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
              <DialogPanel class="relative transform overflow-hidden rounded-lg bg-white px-4 pb-4 pt-5 text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-sm sm:p-6">
                <div>
                  <div class="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-green-100">
                    <CheckIcon class="h-6 w-6 text-green-600" aria-hidden="true" />
                  </div>
                  <div class="mt-3 text-center sm:mt-5">
                    <DialogTitle as="h3" class="text-base font-semibold leading-6 text-gray-900">
                      Payment Successful
                    </DialogTitle>
                    <div class="mt-2">
                      <p class="text-sm text-gray-500">
                        Your group order has been placed successfully. You will receive a confirmation email shortly.
                      </p>
                    </div>
                  </div>
                </div>
                <div class="mt-5 sm:mt-6">
                  <button
                    type="button"
                    class="inline-flex w-full justify-center rounded-md bg-[#FF6B00] px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-[#e66000]"
                    @click="goToOrders"
                  >
                    View Orders
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
import { useRouter } from 'nuxt/app'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import { UserCircleIcon, BellIcon, CheckIcon } from '@heroicons/vue/24/outline'

interface OrderItem {
  id: string
  name: string
  price: number
  quantity: number
  image: string
  comments?: string
}

interface GroupMember {
  id: string
  name: string
  status: string
  items: OrderItem[]
}

const router = useRouter()
const splitMethod = ref<'equal' | 'individual'>('equal')
const paymentMethod = ref<'card' | 'mpesa'>('card')
const showSuccessModal = ref(false)
const isHailButtonDisabled = ref(false)

// Mock data - replace with actual data from your state management
const groupMembers = ref<GroupMember[]>([
  {
    id: '1',
    name: 'John Doe',
    status: 'Group Leader',
    items: [
      {
        id: '1',
        name: 'Margherita Pizza',
        price: 1200,
        quantity: 1,
        image: 'https://images.unsplash.com/photo-1574071318508-1cdbab80d002',
        comments: 'Extra cheese please'
      }
    ]
  },
  {
    id: '2',
    name: 'Jane Smith',
    status: 'Member',
    items: [
      {
        id: '2',
        name: 'Chicken Wings',
        price: 800,
        quantity: 2,
        image: 'https://images.unsplash.com/photo-1567620832903-9fc6debc209f',
        comments: 'Spicy'
      }
    ]
  }
])

// Computed values
const getMemberSubtotal = (member: GroupMember) => {
  return member.items.reduce((total, item) => total + (item.price * item.quantity), 0)
}

const getMemberTax = (member: GroupMember) => {
  return getMemberSubtotal(member) * 0.1
}

const getMemberTotal = (member: GroupMember) => {
  return getMemberSubtotal(member) + getMemberTax(member)
}

const groupSubtotal = computed(() => {
  return groupMembers.value.reduce((total, member) => total + getMemberSubtotal(member), 0)
})

const groupTax = computed(() => groupSubtotal.value * 0.1)
const groupTotal = computed(() => groupSubtotal.value + groupTax.value)

const equalSplitAmount = computed(() => {
  return groupTotal.value / groupMembers.value.length
})

// Methods
const formatPrice = (price: number) => {
  return new Intl.NumberFormat('en-KE', {
    style: 'currency',
    currency: 'KES'
  }).format(price)
}

const processPayment = async () => {
  try {
    // Call payment API
    const response = await fetch('/api/payments/group-order', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        splitMethod: splitMethod.value,
        paymentMethod: paymentMethod.value,
        groupMembers: groupMembers.value,
        total: splitMethod.value === 'equal' ? equalSplitAmount.value : groupTotal.value
      })
    })

    if (response.ok) {
      showSuccessModal.value = true
    }
  } catch (error) {
    console.error('Payment error:', error)
  }
}

const closeSuccessModal = () => {
  showSuccessModal.value = false
}

const goToOrders = () => {
  router.push('/customer/orders')
}

const hailWaiter = async () => {
  try {
    const response = await fetch('/api/hail-waiter', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        tableNumber: localStorage.getItem('tableNumber')
      })
    })

    if (response.ok) {
      isHailButtonDisabled.value = true
      setTimeout(() => {
        isHailButtonDisabled.value = false
      }, 5 * 60 * 1000)
    }
  } catch (error) {
    console.error('Error hailing waiter:', error)
  }
}
</script> 