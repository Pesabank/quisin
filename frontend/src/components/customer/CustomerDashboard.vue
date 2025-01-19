&lt;template&gt;
  &lt;div class="min-h-screen bg-gray-50"&gt;
    &lt;header class="bg-white shadow"&gt;
      &lt;div class="max-w-7xl mx-auto py-4 px-4 sm:px-6 lg:px-8"&gt;
        &lt;h1 class="text-2xl font-bold text-gray-900"&gt;Welcome to {{ restaurantName }}&lt;/h1&gt;
      &lt;/div&gt;
    &lt;/header&gt;

    &lt;main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8"&gt;
      &lt;div class="px-4 sm:px-0"&gt;
        &lt;!-- Table Information --&gt;
        &lt;div class="bg-white overflow-hidden shadow rounded-lg mb-6"&gt;
          &lt;div class="px-4 py-5 sm:p-6"&gt;
            &lt;div class="flex justify-between items-center"&gt;
              &lt;div&gt;
                &lt;h2 class="text-lg font-medium text-gray-900"&gt;Table {{ tableNumber }}&lt;/h2&gt;
                &lt;p class="mt-1 text-sm text-gray-500"&gt;Your server: {{ serverName }}&lt;/p&gt;
              &lt;/div&gt;
              &lt;WaiterButton /&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;

        &lt;div class="grid grid-cols-1 gap-6 lg:grid-cols-2"&gt;
          &lt;!-- Current Order Status --&gt;
          &lt;div class="bg-white overflow-hidden shadow rounded-lg"&gt;
            &lt;div class="px-4 py-5 sm:p-6"&gt;
              &lt;h2 class="text-lg font-medium text-gray-900 mb-4"&gt;Current Order&lt;/h2&gt;
              &lt;div v-if="currentOrder" class="space-y-4"&gt;
                &lt;div class="flex justify-between items-start"&gt;
                  &lt;div&gt;
                    &lt;p class="text-sm font-medium text-gray-900"&gt;Order #{{ currentOrder.number }}&lt;/p&gt;
                    &lt;p class="mt-1 text-sm text-gray-500"&gt;
                      Status: {{ currentOrder.status }}
                    &lt;/p&gt;
                  &lt;/div&gt;
                  &lt;span
                    :class="[
                      'px-2 py-1 text-xs font-medium rounded-full',
                      currentOrder.status === 'preparing'
                        ? 'bg-yellow-100 text-yellow-800'
                        : currentOrder.status === 'ready'
                        ? 'bg-green-100 text-green-800'
                        : 'bg-gray-100 text-gray-800'
                    ]"
                  &gt;
                    {{ currentOrder.estimatedTime }} min
                  &lt;/span&gt;
                &lt;/div&gt;

                &lt;ul class="divide-y divide-gray-200"&gt;
                  &lt;li
                    v-for="item in currentOrder.items"
                    :key="item.id"
                    class="py-3"
                  &gt;
                    &lt;div class="flex justify-between"&gt;
                      &lt;div&gt;
                        &lt;p class="text-sm font-medium text-gray-900"&gt;
                          {{ item.quantity }}x {{ item.name }}
                        &lt;/p&gt;
                        &lt;p v-if="item.notes" class="mt-1 text-xs text-gray-500"&gt;
                          Note: {{ item.notes }}
                        &lt;/p&gt;
                      &lt;/div&gt;
                      &lt;p class="text-sm text-gray-500"&gt;
                        {{ formatPrice(item.price * item.quantity) }}
                      &lt;/p&gt;
                    &lt;/div&gt;
                  &lt;/li&gt;
                &lt;/ul&gt;

                &lt;div class="pt-4 border-t border-gray-200"&gt;
                  &lt;div class="flex justify-between text-sm font-medium"&gt;
                    &lt;p class="text-gray-900"&gt;Total&lt;/p&gt;
                    &lt;p class="text-gray-900"&gt;{{ formatPrice(orderTotal) }}&lt;/p&gt;
                  &lt;/div&gt;
                &lt;/div&gt;
              &lt;/div&gt;
              &lt;div v-else class="text-center py-6 text-gray-500"&gt;
                No active order
              &lt;/div&gt;
            &lt;/div&gt;
          &lt;/div&gt;

          &lt;!-- Quick Actions --&gt;
          &lt;div class="bg-white overflow-hidden shadow rounded-lg"&gt;
            &lt;div class="px-4 py-5 sm:p-6"&gt;
              &lt;h2 class="text-lg font-medium text-gray-900 mb-4"&gt;Quick Actions&lt;/h2&gt;
              &lt;div class="grid grid-cols-2 gap-4"&gt;
                &lt;button
                  @click="viewMenu"
                  class="inline-flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700"
                &gt;
                  &lt;MenuIcon class="h-5 w-5 mr-2" /&gt;
                  View Menu
                &lt;/button&gt;
                &lt;button
                  @click="viewBill"
                  class="inline-flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700"
                &gt;
                  &lt;ReceiptTaxIcon class="h-5 w-5 mr-2" /&gt;
                  View Bill
                &lt;/button&gt;
                &lt;button
                  @click="orderStatus"
                  class="inline-flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-purple-600 hover:bg-purple-700"
                &gt;
                  &lt;ClockIcon class="h-5 w-5 mr-2" /&gt;
                  Order Status
                &lt;/button&gt;
                &lt;button
                  @click="specialRequests"
                  class="inline-flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-yellow-600 hover:bg-yellow-700"
                &gt;
                  &lt;ChatAltIcon class="h-5 w-5 mr-2" /&gt;
                  Special Requests
                &lt;/button&gt;
              &lt;/div&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/main&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useStore } from 'vuex'
import {
  MenuIcon,
  ReceiptTaxIcon,
  ClockIcon,
  ChatAltIcon,
} from '@heroicons/vue/outline'
import WaiterButton from '@/components/customer/WaiterButton.vue'

const store = useStore()
let wsConnection: WebSocket | null = null

const restaurantName = ref('')
const tableNumber = ref('')
const serverName = ref('')
const currentOrder = ref(null)

const orderTotal = computed(() => {
  if (!currentOrder.value?.items) return 0
  return currentOrder.value.items.reduce(
    (total: number, item: any) => total + item.price * item.quantity,
    0
  )
})

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(price)
}

const viewMenu = () => {
  // Implement menu view logic
}

const viewBill = () => {
  // Implement bill view logic
}

const orderStatus = () => {
  // Implement order status view logic
}

const specialRequests = () => {
  // Implement special requests logic
}

const setupWebSocket = () => {
  const tableId = store.state.currentTable.id
  wsConnection = new WebSocket(`${process.env.VUE_APP_WS_URL}/customer/${tableId}`)

  wsConnection.onmessage = (event) => {
    const data = JSON.parse(event.data)
    
    if (data.type === 'order_update') {
      currentOrder.value = data.order
    }
  }

  wsConnection.onclose = () => {
    setTimeout(setupWebSocket, 1000) // Reconnect after 1 second
  }
}

const fetchInitialData = async () => {
  try {
    const tableResponse = await axios.get('/api/customer/table-info')
    tableNumber.value = tableResponse.data.number
    serverName.value = tableResponse.data.server.name
    restaurantName.value = tableResponse.data.restaurant.name

    const orderResponse = await axios.get('/api/customer/current-order')
    currentOrder.value = orderResponse.data
  } catch (err) {
    console.error('Error fetching initial data:', err)
  }
}

onMounted(() => {
  fetchInitialData()
  setupWebSocket()
})

onUnmounted(() => {
  if (wsConnection) {
    wsConnection.close()
  }
})
&lt;/script&gt;
