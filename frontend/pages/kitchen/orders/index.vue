<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="sm:flex sm:items-center">
        <div class="sm:flex-auto">
          <h1 class="text-3xl font-bold text-gray-900">Kitchen Orders</h1>
          <p class="mt-2 text-sm text-gray-700">
            Manage incoming orders, track preparation times, and update order status.
          </p>
        </div>
        <div class="mt-4 sm:mt-0 sm:ml-16 sm:flex space-x-4">
          <!-- View Toggle -->
          <button
            @click="toggleView"
            class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
          >
            <Squares2X2Icon v-if="!isGridView" class="h-4 w-4 mr-2" />
            <ListBulletIcon v-else class="h-4 w-4 mr-2" />
            {{ isGridView ? 'List View' : 'Grid View' }}
          </button>
          
          <!-- Auto Refresh -->
          <button
            @click="toggleAutoRefresh"
            :class="[
              autoRefresh ? 'bg-[#FF6B00] text-white' : 'bg-white text-gray-700',
              'inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]'
            ]"
          >
            <ArrowPathIcon class="h-4 w-4 mr-2" :class="{ 'animate-spin': autoRefresh }" />
            {{ autoRefresh ? 'Auto-Refresh On' : 'Auto-Refresh Off' }}
          </button>
        </div>
      </div>

      <!-- Search and Filters -->
      <div class="mt-8 space-y-4 sm:flex sm:space-y-0 sm:space-x-4">
        <!-- Search -->
        <div class="flex-1">
          <div class="relative rounded-md shadow-sm">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <MagnifyingGlassIcon class="h-5 w-5 text-gray-400" />
            </div>
            <input
              type="text"
              v-model="searchQuery"
              class="block w-full pl-10 rounded-md border-gray-300 focus:ring-[#FF6B00] focus:border-[#FF6B00] sm:text-sm"
              placeholder="Search by order number, table, or items..."
            />
          </div>
        </div>

        <!-- Sort By -->
        <div class="w-full sm:w-48">
          <select
            v-model="sortBy"
            class="block w-full rounded-md border-gray-300 focus:ring-[#FF6B00] focus:border-[#FF6B00] sm:text-sm"
          >
            <option value="priority">Sort by Priority</option>
            <option value="time">Sort by Time</option>
            <option value="table">Sort by Table</option>
          </select>
        </div>

        <!-- Priority Filter -->
        <div class="w-full sm:w-48">
          <select
            v-model="priorityFilter"
            class="block w-full rounded-md border-gray-300 focus:ring-[#FF6B00] focus:border-[#FF6B00] sm:text-sm"
          >
            <option value="all">All Priorities</option>
            <option value="high">High Priority</option>
            <option value="medium">Medium Priority</option>
            <option value="low">Low Priority</option>
          </select>
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
                  <dt class="text-sm font-medium text-gray-500 truncate">Pending Orders</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">{{ pendingOrders }}</div>
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
                <FireIcon class="h-6 w-6 text-yellow-500" />
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">In Preparation</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">{{ inPreparationOrders }}</div>
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
                  <dt class="text-sm font-medium text-gray-500 truncate">Ready for Service</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">{{ readyOrders }}</div>
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
                <ChartBarIcon class="h-6 w-6 text-blue-500" />
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Avg. Preparation Time</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">{{ avgPrepTime }} min</div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Order Queue -->
      <div class="mt-8">
        <div class="bg-white shadow rounded-lg">
          <!-- Queue Tabs -->
          <div class="p-6">
            <div class="sm:hidden">
              <label for="tabs" class="sr-only">Select a tab</label>
              <select
                id="tabs"
                v-model="selectedTab"
                class="block w-full rounded-md border-gray-300 focus:border-[#FF6B00] focus:ring-[#FF6B00]"
              >
                <option value="pending">Pending ({{ pendingOrders }})</option>
                <option value="preparing">In Preparation ({{ inPreparationOrders }})</option>
                <option value="ready">Ready ({{ readyOrders }})</option>
              </select>
            </div>
            <div class="hidden sm:block">
              <div class="border-b border-gray-200">
                <nav class="-mb-px flex space-x-8" aria-label="Tabs">
                  <button
                    v-for="tab in tabs"
                    :key="tab.value"
                    @click="selectedTab = tab.value"
                    :class="[
                      selectedTab === tab.value
                        ? 'border-[#FF6B00] text-[#FF6B00]'
                        : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
                      'whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm'
                    ]"
                  >
                    {{ tab.name }} ({{ getOrderCountByStatus(tab.value) }})
                  </button>
                </nav>
              </div>
            </div>
          </div>

          <!-- Grid/List View Toggle -->
          <div class="px-6 py-4">
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              <div
                v-for="order in filteredAndSortedOrders"
                :key="order.id"
                class="bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow duration-200 overflow-hidden"
              >
                <!-- Order Header -->
                <div class="border-b border-gray-100">
                  <div class="p-4">
                    <div class="flex justify-between items-start">
                      <div>
                        <div class="flex items-center space-x-3">
                          <span class="text-lg font-semibold">Order #{{ order.id }}</span>
                          <span
                            :class="[
                              'px-2 py-1 text-xs font-medium rounded-full',
                              order.priority === 'high' ? 'bg-red-100 text-red-800' : '',
                              order.priority === 'medium' ? 'bg-yellow-100 text-yellow-800' : '',
                              order.priority === 'low' ? 'bg-green-100 text-green-800' : ''
                            ]"
                          >
                            {{ order.priority }} priority
                          </span>
                        </div>
                        <div class="mt-2 flex items-center space-x-2">
                          <img
                            :src="order.waiter.avatar"
                            :alt="order.waiter.name"
                            class="h-6 w-6 rounded-full"
                          />
                          <span class="text-sm text-gray-600">{{ order.waiter.name }}</span>
                        </div>
                      </div>
                      <div class="flex items-center space-x-2">
                        <button
                          @click="openDetailsModal(order)"
                          class="inline-flex items-center px-2.5 py-1.5 border border-gray-300 shadow-sm text-xs font-medium rounded text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
                        >
                          Details
                        </button>
                        <button
                          v-if="order.status === 'pending'"
                          @click="startPreparation(order)"
                          class="inline-flex items-center px-3 py-1.5 border border-transparent rounded-md text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000]"
                        >
                          Start
                        </button>
                        <button
                          v-if="order.status === 'preparing'"
                          @click="markAsReady(order)"
                          :disabled="!order.items.every((item: OrderItem) => item.status === 'completed')"
                          :class="[
                            'inline-flex items-center px-3 py-1.5 border border-transparent rounded-md text-sm font-medium',
                            order.items.every((item: OrderItem) => item.status === 'completed')
                              ? 'text-white bg-green-600 hover:bg-green-700'
                              : 'text-gray-400 bg-gray-100 cursor-not-allowed'
                          ]"
                        >
                          Ready
                        </button>
                      </div>
                    </div>
                  </div>
                  <div class="px-4 pb-3 flex items-center justify-between text-sm text-gray-500">
                    <div>Table {{ order.tableNumber }} • {{ order.items.length }} items</div>
                    <div>{{ formatTime(order.orderTime) }}</div>
                  </div>
                </div>

                <!-- Order Items -->
                <div class="p-4">
                  <div class="space-y-3">
                    <div
                      v-for="item in order.items"
                      :key="item.id"
                      class="flex items-center"
                    >
                      <div class="flex-shrink-0">
                        <div
                          :class="[
                            'w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium',
                            item.status === 'completed' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-700'
                          ]"
                        >
                          {{ item.quantity }}
                        </div>
                      </div>
                      <div class="ml-3 flex-1">
                        <div class="flex items-center justify-between">
                          <span class="font-medium">{{ item.name }}</span>
                          <button
                            v-if="order.status === 'preparing'"
                            @click="toggleItemStatus(order, item)"
                            :class="[
                              'px-2 py-1 rounded text-xs font-medium',
                              item.status === 'completed'
                                ? 'bg-green-100 text-green-700'
                                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                            ]"
                          >
                            {{ item.status === 'completed' ? '✓ Done' : 'Mark Done' }}
                          </button>
                        </div>
                        <div class="mt-1">
                          <div v-if="item.notes" class="text-sm text-gray-500">{{ item.notes }}</div>
                          <div v-if="item.modifiers && item.modifiers.length" class="mt-1 flex flex-wrap gap-1">
                            <span
                              v-for="modifier in item.modifiers"
                              :key="modifier"
                              class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-gray-100 text-gray-800"
                            >
                              {{ modifier }}
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Special Instructions -->
                <div
                  v-if="order.specialInstructions"
                  class="mt-2 mx-4 mb-4 p-3 bg-yellow-50 rounded-lg border border-yellow-100"
                >
                  <div class="flex items-start">
                    <ExclamationTriangleIcon class="h-5 w-5 text-yellow-400 mt-0.5" />
                    <div class="ml-2">
                      <p class="text-sm font-medium text-yellow-800">Special Instructions</p>
                      <p class="mt-1 text-sm text-yellow-700">{{ order.specialInstructions }}</p>
                    </div>
                  </div>
                </div>

                <!-- Time Elapsed (for preparing orders) -->
                <div
                  v-if="order.status === 'preparing'"
                  class="border-t border-gray-100 px-4 py-3 bg-gray-50 flex justify-between items-center"
                >
                  <span class="text-sm text-gray-600">Time Elapsed</span>
                  <span
                    :class="[
                      'text-sm font-medium',
                      getElapsedTime(order) > 20 ? 'text-red-600' : 'text-gray-900'
                    ]"
                  >
                    {{ getElapsedTime(order) }} min
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Order Details Modal -->
  <TransitionRoot appear :show="showDetailsModal" as="template">
    <Dialog as="div" class="relative z-20" @close="showDetailsModal = false">
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
            <DialogPanel class="w-full max-w-2xl transform overflow-hidden rounded-2xl bg-white p-6 text-left align-middle shadow-xl transition-all">
              <div class="flex justify-between items-start">
                <div>
                  <DialogTitle as="h3" class="text-lg font-medium leading-6 text-gray-900">
                    Order Details #{{ selectedOrderDetails?.id }}
                  </DialogTitle>
                  <div class="mt-2 flex items-center space-x-3">
                    <img
                      :src="selectedOrderDetails?.waiter.avatar"
                      :alt="selectedOrderDetails?.waiter.name"
                      class="h-8 w-8 rounded-full"
                    />
                    <div>
                      <p class="text-sm font-medium text-gray-900">Waiter</p>
                      <p class="text-sm text-gray-500">{{ selectedOrderDetails?.waiter.name }}</p>
                    </div>
                  </div>
                </div>
                <button
                  type="button"
                  class="rounded-md bg-white text-gray-400 hover:text-gray-500 focus:outline-none focus:ring-2 focus:ring-[#FF6B00] focus:ring-offset-2"
                  @click="showDetailsModal = false"
                >
                  <span class="sr-only">Close</span>
                  <XMarkIcon class="h-6 w-6" aria-hidden="true" />
                </button>
              </div>

              <div class="mt-4">
                <div class="rounded-lg bg-gray-50 p-4">
                  <div class="flex items-center justify-between">
                    <div>
                      <p class="text-sm font-medium text-gray-900">Table {{ selectedOrderDetails?.tableNumber }}</p>
                      <p class="text-sm text-gray-500">Ordered at {{ formatTime(selectedOrderDetails?.orderTime) }}</p>
                    </div>
                    <span
                      :class="[
                        'px-2 py-1 text-xs font-medium rounded-full',
                        selectedOrderDetails?.priority === 'high' ? 'bg-red-100 text-red-800' : '',
                        selectedOrderDetails?.priority === 'medium' ? 'bg-yellow-100 text-yellow-800' : '',
                        selectedOrderDetails?.priority === 'low' ? 'bg-green-100 text-green-800' : ''
                      ]"
                    >
                      {{ selectedOrderDetails?.priority }} priority
                    </span>
                  </div>
                </div>

                <div class="mt-6">
                  <h4 class="text-sm font-medium text-gray-900">Order Items</h4>
                  <div class="mt-3 divide-y divide-gray-100">
                    <div
                      v-for="item in selectedOrderDetails?.items"
                      :key="item.id"
                      class="py-3"
                    >
                      <div class="flex items-center justify-between">
                        <div class="flex items-center space-x-3">
                          <div
                            :class="[
                              'w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium',
                              item.status === 'completed' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-700'
                            ]"
                          >
                            {{ item.quantity }}
                          </div>
                          <div>
                            <p class="text-sm font-medium text-gray-900">{{ item.name }}</p>
                            <p v-if="item.notes" class="mt-0.5 text-sm text-gray-500">{{ item.notes }}</p>
                          </div>
                        </div>
                        <span
                          :class="[
                            'px-2 py-1 text-xs font-medium rounded-full',
                            item.status === 'completed' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-700'
                          ]"
                        >
                          {{ item.status }}
                        </span>
                      </div>
                      <div v-if="item.modifiers?.length" class="mt-2 ml-11">
                        <div class="flex flex-wrap gap-1">
                          <span
                            v-for="modifier in item.modifiers"
                            :key="modifier"
                            class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-gray-100 text-gray-800"
                          >
                            {{ modifier }}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div v-if="selectedOrderDetails?.specialInstructions" class="mt-6">
                  <div class="rounded-lg bg-yellow-50 p-4">
                    <div class="flex">
                      <ExclamationTriangleIcon class="h-5 w-5 text-yellow-400" />
                      <div class="ml-3">
                        <h3 class="text-sm font-medium text-yellow-800">Special Instructions</h3>
                        <div class="mt-2 text-sm text-yellow-700">
                          <p>{{ selectedOrderDetails.specialInstructions }}</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="mt-6 flex justify-end">
                <button
                  type="button"
                  class="inline-flex justify-center rounded-md border border-transparent bg-[#FF6B00] px-4 py-2 text-sm font-medium text-white hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-[#FF6B00] focus:ring-offset-2"
                  @click="showDetailsModal = false"
                >
                  Close
                </button>
              </div>
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { TransitionGroup } from 'vue'
import {
  ClockIcon,
  FireIcon,
  CheckCircleIcon,
  ChartBarIcon,
  PlayIcon,
  CheckIcon,
  ArrowPathIcon,
  ExclamationTriangleIcon,
  MagnifyingGlassIcon,
  Squares2X2Icon,
  ListBulletIcon,
  XMarkIcon
} from '@heroicons/vue/24/outline'
import { TransitionRoot, Dialog, DialogPanel, DialogTitle } from '@headlessui/vue'

interface OrderItem {
  id: string
  name: string
  quantity: number
  notes?: string
  modifiers?: string[]
  status: 'pending' | 'completed'
}

interface Order {
  id: string
  tableNumber: number
  status: 'pending' | 'preparing' | 'ready'
  priority: 'low' | 'medium' | 'high'
  orderTime: Date
  startTime?: Date
  items: OrderItem[]
  specialInstructions?: string
  waiter: {
    id: string
    name: string
    avatar?: string
  }
}

// State
const selectedTab = ref<'pending' | 'preparing' | 'ready'>('pending')
const autoRefresh = ref(true)
const isGridView = ref(true)
const searchQuery = ref('')
const sortBy = ref('priority')
const priorityFilter = ref('all')
let refreshInterval: number | null = null
const showDetailsModal = ref(false)
const selectedOrderDetails = ref<Order | null>(null)

// Mock data - Replace with API calls
const orders = ref<Order[]>([
  {
    id: '1001',
    tableNumber: 5,
    status: 'pending',
    priority: 'high',
    orderTime: new Date(Date.now() - 5 * 60000),
    waiter: {
      id: 'W1',
      name: 'John Smith',
      avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e'
    },
    items: [
      {
        id: '1',
        name: 'Margherita Pizza',
        quantity: 2,
        notes: 'Extra crispy',
        status: 'pending'
      },
      {
        id: '2',
        name: 'Caesar Salad',
        quantity: 1,
        modifiers: ['No croutons', 'Dressing on side'],
        status: 'pending'
      }
    ],
    specialInstructions: 'Allergy alert: Customer is allergic to nuts'
  },
  {
    id: '1002',
    tableNumber: 3,
    status: 'preparing',
    priority: 'medium',
    orderTime: new Date(Date.now() - 15 * 60000),
    startTime: new Date(Date.now() - 10 * 60000),
    items: [
      {
        id: '3',
        name: 'Spaghetti Carbonara',
        quantity: 1,
        status: 'completed'
      },
      {
        id: '4',
        name: 'Garlic Bread',
        quantity: 1,
        status: 'pending'
      }
    ]
  },
  {
    id: '1003',
    tableNumber: 7,
    status: 'ready',
    priority: 'low',
    orderTime: new Date(Date.now() - 25 * 60000),
    startTime: new Date(Date.now() - 20 * 60000),
    items: [
      {
        id: '5',
        name: 'Chicken Wings',
        quantity: 2,
        modifiers: ['Extra spicy'],
        status: 'completed'
      }
    ]
  }
])

const tabs = [
  { name: 'Pending', value: 'pending' },
  { name: 'In Preparation', value: 'preparing' },
  { name: 'Ready', value: 'ready' }
]

// Computed
const filteredAndSortedOrders = computed(() => {
  return orders.value
    .filter(order => {
      // Status filter
      if (order.status !== selectedTab.value) return false
      
      // Priority filter
      if (priorityFilter.value !== 'all' && order.priority !== priorityFilter.value) return false
      
      // Search filter
      if (searchQuery.value) {
        const search = searchQuery.value.toLowerCase()
        return (
          order.id.toLowerCase().includes(search) ||
          order.tableNumber.toString().includes(search) ||
          order.items.some(item => 
            item.name.toLowerCase().includes(search) ||
            item.notes?.toLowerCase().includes(search) ||
            item.modifiers?.some(mod => mod.toLowerCase().includes(search))
          )
        )
      }
      
      return true
    })
    .sort((a, b) => {
      switch (sortBy.value) {
        case 'priority':
          const priorityOrder = { high: 0, medium: 1, low: 2 }
          return priorityOrder[a.priority] - priorityOrder[b.priority]
        case 'time':
          return a.orderTime.getTime() - b.orderTime.getTime()
        case 'table':
          return a.tableNumber - b.tableNumber
        default:
          return 0
      }
    })
})

const pendingOrders = computed(() => orders.value.filter(o => o.status === 'pending').length)
const inPreparationOrders = computed(() => orders.value.filter(o => o.status === 'preparing').length)
const readyOrders = computed(() => orders.value.filter(o => o.status === 'ready').length)

const avgPrepTime = computed(() => {
  const completedOrders = orders.value.filter(o => o.status === 'ready' && o.startTime)
  if (completedOrders.length === 0) return 0

  const totalTime = completedOrders.reduce((sum, order) => {
    return sum + (order.startTime ? (new Date().getTime() - order.startTime.getTime()) : 0)
  }, 0)

  return Math.round(totalTime / (completedOrders.length * 60000))
})

// Methods
const getOrderCountByStatus = (status: string) => {
  return orders.value.filter(o => o.status === status).length
}

const formatTime = (date: Date) => {
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

const getElapsedTime = (order: Order) => {
  if (!order.startTime) return 0
  return Math.round((new Date().getTime() - order.startTime.getTime()) / 60000)
}

const startPreparation = (order: Order) => {
  order.status = 'preparing'
  order.startTime = new Date()
}

const markAsReady = (order: Order) => {
  if (order.items.every(item => item.status === 'completed')) {
    order.status = 'ready'
  }
}

const toggleItemStatus = (order: Order, item: OrderItem) => {
  item.status = item.status === 'completed' ? 'pending' : 'completed'
  
  // Check if all items are completed
  if (order.items.every(item => item.status === 'completed')) {
    // Show a visual indicator that the order can be marked as ready
    // This could be implemented with a notification or highlight
  }
}

const toggleAutoRefresh = () => {
  autoRefresh.value = !autoRefresh.value
  if (autoRefresh.value) {
    startAutoRefresh()
  } else if (refreshInterval) {
    clearInterval(refreshInterval)
  }
}

const startAutoRefresh = () => {
  refreshInterval = window.setInterval(() => {
    // TODO: Fetch new orders from API
    console.log('Refreshing orders...')
  }, 30000) // Refresh every 30 seconds
}

const toggleView = () => {
  isGridView.value = !isGridView.value
}

const openDetailsModal = (order: Order) => {
  selectedOrderDetails.value = order
  showDetailsModal.value = true
}

// Lifecycle
onMounted(() => {
  if (autoRefresh.value) {
    startAutoRefresh()
  }
})

onUnmounted(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
  }
})
</script>

<style scoped>
/* Add any component-specific styles here */
</style> 