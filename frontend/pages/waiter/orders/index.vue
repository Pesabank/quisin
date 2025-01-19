<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="sm:flex sm:items-center">
        <div class="sm:flex-auto">
          <h1 class="text-3xl font-bold text-gray-900">Order Management</h1>
          <p class="mt-2 text-sm text-gray-700">
            Take orders, manage active tables, and process payments.
          </p>
        </div>
        <div class="mt-4 sm:mt-0 sm:ml-16 sm:flex-none">
          <button
            @click="showNewOrderModal = true"
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
          >
            <PlusIcon class="h-4 w-4 mr-2" />
            New Order
          </button>
        </div>
      </div>

      <!-- Stats -->
      <div class="mt-8 grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <ClockIcon class="h-6 w-6 text-[#FF6B00]" />
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Active Orders</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">{{ activeOrders.length }}</div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <TableCellsIcon class="h-6 w-6 text-gray-400" />
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Occupied Tables</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">{{ occupiedTables }}</div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <CurrencyDollarIcon class="h-6 w-6 text-green-500" />
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Today's Revenue</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">${{ todayRevenue.toFixed(2) }}</div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <CheckCircleIcon class="h-6 w-6 text-green-500" />
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Completed Orders</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">{{ completedOrders }}</div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Active Orders -->
      <div class="mt-8">
        <div class="bg-white shadow rounded-lg">
          <div class="p-6">
            <h2 class="text-lg font-medium text-gray-900">Active Orders</h2>
            
            <!-- Order Cards -->
            <div class="mt-6 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
              <div
                v-for="order in activeOrders"
                :key="order.id"
                class="bg-white border rounded-lg shadow-sm hover:shadow-md transition-shadow duration-200"
              >
                <div class="p-6">
                  <div class="flex justify-between items-start">
                    <div>
                      <div class="flex items-center">
                        <span class="text-lg font-medium text-gray-900">Table {{ order.tableNumber }}</span>
                        <span
                          :class="[
                            'ml-2 px-2 py-1 text-xs font-medium rounded-full',
                            order.status === 'preparing' ? 'bg-yellow-100 text-yellow-800' : '',
                            order.status === 'ready' ? 'bg-green-100 text-green-800' : '',
                            order.status === 'served' ? 'bg-blue-100 text-blue-800' : ''
                          ]"
                        >
                          {{ order.status }}
                        </span>
                      </div>
                      <p class="mt-1 text-sm text-gray-500">Started: {{ order.startTime }}</p>
                    </div>
                    <p class="text-lg font-semibold text-gray-900">${{ order.total.toFixed(2) }}</p>
                  </div>

                  <!-- Order Items -->
                  <div class="mt-4">
                    <div class="space-y-2">
                      <div
                        v-for="item in order.items"
                        :key="item.id"
                        class="flex justify-between items-center"
                      >
                        <div class="flex items-center">
                          <span class="text-sm font-medium text-gray-900">{{ item.quantity }}x</span>
                          <span class="ml-2 text-sm text-gray-700">{{ item.name }}</span>
                        </div>
                        <span class="text-sm text-gray-500">${{ (item.price * item.quantity).toFixed(2) }}</span>
                      </div>
                    </div>
                  </div>

                  <!-- Actions -->
                  <div class="mt-6 flex justify-between items-center">
                    <div class="flex space-x-2">
                      <button
                        @click="editOrder(order)"
                        class="inline-flex items-center px-3 py-1.5 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
                      >
                        <PencilIcon class="h-4 w-4 mr-1" />
                        Edit
                      </button>
                      <button
                        @click="openPaymentModal(order)"
                        class="inline-flex items-center px-3 py-1.5 border border-transparent rounded-md text-sm font-medium text-white bg-green-600 hover:bg-green-700"
                      >
                        <CurrencyDollarIcon class="h-4 w-4 mr-1" />
                        Pay
                      </button>
                    </div>
                    <button
                      @click="markAsServed(order)"
                      :disabled="order.status !== 'ready'"
                      :class="[
                        'inline-flex items-center px-3 py-1.5 border rounded-md text-sm font-medium',
                        order.status === 'ready'
                          ? 'border-transparent text-white bg-[#FF6B00] hover:bg-[#e66000]'
                          : 'border-gray-300 text-gray-400 bg-gray-50 cursor-not-allowed'
                      ]"
                    >
                      <CheckIcon class="h-4 w-4 mr-1" />
                      Served
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- New Order Modal -->
    <TransitionRoot appear :show="showNewOrderModal" as="template">
      <Dialog as="div" class="relative z-10" @close="showNewOrderModal = false">
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
          <div class="flex min-h-full items-center justify-center p-4">
            <TransitionChild
              as="template"
              enter="duration-300 ease-out"
              enter-from="opacity-0 scale-95"
              enter-to="opacity-100 scale-100"
              leave="duration-200 ease-in"
              leave-from="opacity-100 scale-100"
              leave-to="opacity-0 scale-95"
            >
              <DialogPanel class="w-full max-w-4xl transform overflow-hidden rounded-2xl bg-white p-6 text-left align-middle shadow-xl transition-all">
                <DialogTitle as="h3" class="text-lg font-medium leading-6 text-gray-900">
                  New Order
                </DialogTitle>

                <div class="mt-4">
                  <!-- Table Selection -->
                  <div class="mb-6">
                    <label class="block text-sm font-medium text-gray-700">Select Table</label>
                    <div class="mt-2 grid grid-cols-4 gap-4">
                      <button
                        v-for="table in availableTables"
                        :key="table.id"
                        @click="selectTable(table)"
                        :class="[
                          'flex flex-col items-center justify-center p-4 rounded-lg border-2',
                          selectedTable?.id === table.id
                            ? 'border-[#FF6B00] bg-orange-50'
                            : 'border-gray-200 hover:border-gray-300'
                        ]"
                      >
                        <span class="text-lg font-medium">Table {{ table.number }}</span>
                        <span class="text-sm text-gray-500">{{ table.capacity }} seats</span>
                      </button>
                    </div>
                  </div>

                  <!-- Menu Categories -->
                  <div class="mb-6">
                    <div class="border-b border-gray-200">
                      <nav class="-mb-px flex space-x-8">
                        <button
                          v-for="category in menuCategories"
                          :key="category.id"
                          @click="selectedCategory = category.id"
                          :class="[
                            selectedCategory === category.id
                              ? 'border-[#FF6B00] text-[#FF6B00]'
                              : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700',
                            'whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm'
                          ]"
                        >
                          {{ category.name }}
                        </button>
                      </nav>
                    </div>
                  </div>

                  <!-- Menu Items -->
                  <div class="grid grid-cols-2 gap-6">
                    <div class="space-y-4">
                      <div
                        v-for="item in filteredMenuItems"
                        :key="item.id"
                        class="flex justify-between items-center p-4 rounded-lg border border-gray-200 hover:border-[#FF6B00] cursor-pointer"
                        @click="addItemToOrder(item)"
                      >
                        <div>
                          <h4 class="font-medium text-gray-900">{{ item.name }}</h4>
                          <p class="text-sm text-gray-500">{{ item.description }}</p>
                        </div>
                        <div class="text-lg font-semibold text-gray-900">${{ item.price.toFixed(2) }}</div>
                      </div>
                    </div>

                    <!-- Order Summary -->
                    <div class="bg-gray-50 p-6 rounded-lg">
                      <h4 class="text-lg font-medium text-gray-900">Order Summary</h4>
                      
                      <div class="mt-4 space-y-4">
                        <div
                          v-for="item in orderItems"
                          :key="item.id"
                          class="flex items-center justify-between"
                        >
                          <div class="flex items-center">
                            <div class="flex items-center space-x-2">
                              <button
                                @click="decrementItem(item)"
                                class="p-1 rounded-full text-gray-400 hover:text-gray-500"
                              >
                                <MinusCircleIcon class="h-5 w-5" />
                              </button>
                              <span class="text-gray-900">{{ item.quantity }}</span>
                              <button
                                @click="incrementItem(item)"
                                class="p-1 rounded-full text-gray-400 hover:text-gray-500"
                              >
                                <PlusCircleIcon class="h-5 w-5" />
                              </button>
                            </div>
                            <span class="ml-4 text-gray-900">{{ item.name }}</span>
                          </div>
                          <div class="flex items-center">
                            <span class="text-gray-900">${{ (item.price * item.quantity).toFixed(2) }}</span>
                            <button
                              @click="removeItem(item)"
                              class="ml-4 text-gray-400 hover:text-gray-500"
                            >
                              <TrashIcon class="h-5 w-5" />
                            </button>
                          </div>
                        </div>
                      </div>

                      <div class="mt-6 border-t border-gray-200 pt-4">
                        <div class="flex justify-between text-base font-medium text-gray-900">
                          <p>Subtotal</p>
                          <p>${{ orderSubtotal.toFixed(2) }}</p>
                        </div>
                        <div class="mt-2 flex justify-between text-sm text-gray-500">
                          <p>Tax (10%)</p>
                          <p>${{ orderTax.toFixed(2) }}</p>
                        </div>
                        <div class="mt-2 flex justify-between text-lg font-medium text-gray-900">
                          <p>Total</p>
                          <p>${{ orderTotal.toFixed(2) }}</p>
                        </div>
                      </div>

                      <!-- Special Instructions -->
                      <div class="mt-6">
                        <label for="instructions" class="block text-sm font-medium text-gray-700">
                          Special Instructions
                        </label>
                        <textarea
                          id="instructions"
                          v-model="orderInstructions"
                          rows="3"
                          class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] sm:text-sm"
                        ></textarea>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="mt-6 flex justify-end space-x-4">
                  <button
                    type="button"
                    class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
                    @click="showNewOrderModal = false"
                  >
                    Cancel
                  </button>
                  <button
                    type="button"
                    class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000]"
                    @click="submitOrder"
                    :disabled="!canSubmitOrder"
                  >
                    Submit Order
                  </button>
                </div>
              </DialogPanel>
            </TransitionChild>
          </div>
        </div>
      </Dialog>
    </TransitionRoot>

    <!-- Payment Modal -->
    <TransitionRoot as="template" :show="isPaymentModalOpen">
      <Dialog as="div" class="relative z-10" @close="isPaymentModalOpen = false">
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
          <div class="flex min-h-full items-center justify-center p-4">
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
                  Process Payment
                </DialogTitle>

                <div class="mt-4">
                  <div class="space-y-4">
                    <!-- Order Summary -->
                    <div>
                      <h4 class="text-sm font-medium text-gray-500">Order Summary</h4>
                      <div class="mt-2">
                        <div class="flex justify-between">
                          <span class="text-gray-900">Subtotal</span>
                          <span class="text-gray-900">${{ selectedOrder?.total.toFixed(2) }}</span>
                        </div>
                        <div class="mt-1 flex justify-between text-sm">
                          <span class="text-gray-500">Tax (10%)</span>
                          <span class="text-gray-500">${{ (selectedOrder?.total * 0.1).toFixed(2) }}</span>
                        </div>
                        <div class="mt-4 flex justify-between text-lg font-medium">
                          <span class="text-gray-900">Total</span>
                          <span class="text-gray-900">${{ (selectedOrder?.total * 1.1).toFixed(2) }}</span>
                        </div>
                      </div>
                    </div>

                    <!-- Payment Method -->
                    <div>
                      <label class="text-sm font-medium text-gray-700">Payment Method</label>
                      <div class="mt-2 space-y-2">
                        <div
                          v-for="method in paymentMethods"
                          :key="method.id"
                          class="flex items-center"
                        >
                          <input
                            :id="method.id"
                            type="radio"
                            v-model="selectedPaymentMethod"
                            :value="method.id"
                            class="h-4 w-4 text-[#FF6B00] focus:ring-[#FF6B00] border-gray-300"
                          />
                          <label :for="method.id" class="ml-3 block text-sm font-medium text-gray-700">
                            {{ method.name }}
                          </label>
                        </div>
                      </div>
                    </div>

                    <!-- Split Payment -->
                    <div class="flex items-center">
                      <input
                        id="split-payment"
                        type="checkbox"
                        v-model="splitPayment"
                        class="h-4 w-4 text-[#FF6B00] focus:ring-[#FF6B00] border-gray-300 rounded"
                      />
                      <label for="split-payment" class="ml-3 block text-sm font-medium text-gray-700">
                        Split Payment
                      </label>
                    </div>

                    <div v-if="splitPayment" class="space-y-2">
                      <label class="block text-sm font-medium text-gray-700">Number of Ways</label>
                      <input
                        type="number"
                        v-model="splitWays"
                        min="2"
                        max="10"
                        class="block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] sm:text-sm"
                      />
                      <p class="text-sm text-gray-500">
                        Amount per person: ${{ (selectedOrder?.total * 1.1 / splitWays).toFixed(2) }}
                      </p>
                    </div>
                  </div>
                </div>

                <div class="mt-6 flex justify-end space-x-4">
                  <button
                    type="button"
                    class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
                    @click="isPaymentModalOpen = false"
                  >
                    Cancel
                  </button>
                  <button
                    type="button"
                    class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000]"
                    @click="processPayment"
                    :disabled="!selectedPaymentMethod"
                  >
                    Process Payment
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
  PlusIcon,
  MinusCircleIcon,
  PlusCircleIcon,
  TrashIcon,
  PencilIcon,
  TableCellsIcon,
  CheckIcon,
  ClockIcon,
  CurrencyDollarIcon,
  CheckCircleIcon
} from '@heroicons/vue/24/outline'

// Interfaces
interface MenuItem {
  id: string
  categoryId: string
  name: string
  description: string
  price: number
  quantity?: number
}

interface OrderItem extends MenuItem {
  quantity: number
}

interface Table {
  id: string
  number: number
  capacity: number
}

interface Order {
  id: string
  tableNumber: number
  status: 'preparing' | 'ready' | 'served'
  startTime: string
  items: OrderItem[]
  total: number
}

interface MenuCategory {
  id: string
  name: string
}

interface PaymentMethod {
  id: string
  name: string
}

// State
const showNewOrderModal = ref(false)
const isPaymentModalOpen = ref(false)
const selectedTable = ref<Table | null>(null)
const selectedCategory = ref<string>('main-courses')
const orderItems = ref<OrderItem[]>([])
const orderInstructions = ref('')
const selectedOrder = ref<Order | null>(null)
const selectedPaymentMethod = ref<string>('')
const splitPayment = ref(false)
const splitWays = ref(2)

// Mock data - Replace with API calls
const activeOrders = ref<Order[]>([
  {
    id: '1',
    tableNumber: 1,
    status: 'preparing',
    startTime: '2:30 PM',
    items: [
      { id: '1', name: 'Margherita Pizza', quantity: 1, price: 14.99, categoryId: 'main-courses', description: '' },
      { id: '2', name: 'Coke', quantity: 2, price: 2.99, categoryId: 'beverages', description: '' }
    ],
    total: 20.97
  },
  {
    id: '2',
    tableNumber: 3,
    status: 'ready',
    startTime: '2:45 PM',
    items: [
      { id: '3', name: 'Chicken Wings', quantity: 2, price: 12.99, categoryId: 'appetizers', description: '' },
      { id: '4', name: 'French Fries', quantity: 1, price: 4.99, categoryId: 'appetizers', description: '' }
    ],
    total: 30.97
  }
])

const availableTables = ref<Table[]>([
  { id: '1', number: 1, capacity: 4 },
  { id: '2', number: 2, capacity: 2 },
  { id: '3', number: 3, capacity: 6 },
  { id: '4', number: 4, capacity: 4 }
])

const menuCategories = ref<MenuCategory[]>([
  { id: 'appetizers', name: 'Appetizers' },
  { id: 'main-courses', name: 'Main Courses' },
  { id: 'desserts', name: 'Desserts' },
  { id: 'beverages', name: 'Beverages' }
])

const menuItems = ref<MenuItem[]>([
  {
    id: '1',
    categoryId: 'main-courses',
    name: 'Margherita Pizza',
    description: 'Fresh tomatoes, mozzarella, basil, and olive oil',
    price: 14.99
  },
  {
    id: '2',
    categoryId: 'appetizers',
    name: 'Chicken Wings',
    description: 'Crispy wings tossed in our signature hot sauce',
    price: 12.99
  }
])

const paymentMethods: PaymentMethod[] = [
  { id: 'cash', name: 'Cash' },
  { id: 'credit', name: 'Credit Card' },
  { id: 'debit', name: 'Debit Card' }
]

// Computed
const occupiedTables = computed(() => activeOrders.value.length)

const todayRevenue = computed(() => {
  return activeOrders.value.reduce((sum, order) => sum + order.total, 0)
})

const completedOrders = computed(() => 15) // Mock data

const filteredMenuItems = computed(() => {
  return menuItems.value.filter(item => item.categoryId === selectedCategory.value)
})

const orderSubtotal = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0)
})

const orderTax = computed(() => orderSubtotal.value * 0.1)

const orderTotal = computed(() => orderSubtotal.value + orderTax.value)

const canSubmitOrder = computed(() => {
  return selectedTable.value !== null && orderItems.value.length > 0
})

// Methods
const selectTable = (table: Table) => {
  selectedTable.value = table
}

const addItemToOrder = (item: MenuItem) => {
  const existingItem = orderItems.value.find(i => i.id === item.id)
  if (existingItem) {
    existingItem.quantity++
  } else {
    orderItems.value.push({
      ...item,
      quantity: 1
    })
  }
}

const incrementItem = (item: OrderItem) => {
  item.quantity++
}

const decrementItem = (item: OrderItem) => {
  if (item.quantity > 1) {
    item.quantity--
  } else {
    removeItem(item)
  }
}

const removeItem = (item: OrderItem) => {
  const index = orderItems.value.findIndex(i => i.id === item.id)
  if (index !== -1) {
    orderItems.value.splice(index, 1)
  }
}

const submitOrder = () => {
  if (!selectedTable.value) return
  
  const newOrder: Order = {
    id: String(activeOrders.value.length + 1),
    tableNumber: selectedTable.value.number,
    status: 'preparing',
    startTime: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    items: [...orderItems.value],
    total: orderTotal.value
  }
  
  activeOrders.value.push(newOrder)
  showNewOrderModal.value = false
  resetOrderForm()
}

const resetOrderForm = () => {
  selectedTable.value = null
  orderItems.value = []
  orderInstructions.value = ''
}

const editOrder = (order: Order) => {
  // TODO: Implement edit order functionality
  console.log('Edit order:', order)
}

const markAsServed = (order: Order) => {
  order.status = 'served'
}

const openPaymentModal = (order: Order) => {
  selectedOrder.value = order
  isPaymentModalOpen.value = true
}

const processPayment = () => {
  if (!selectedOrder.value) return
  
  // TODO: Process payment through API
  const orderIndex = activeOrders.value.findIndex(o => o.id === selectedOrder.value.id)
  if (orderIndex !== -1) {
    activeOrders.value.splice(orderIndex, 1)
  }
  isPaymentModalOpen.value = false
  selectedOrder.value = null
  selectedPaymentMethod.value = ''
  splitPayment.value = false
  splitWays.value = 2
}
</script>

<style scoped>
/* Add any component-specific styles here */
</style> 