&lt;template&gt;
  &lt;div class="min-h-screen bg-gray-100"&gt;
    &lt;header class="bg-white shadow"&gt;
      &lt;div class="max-w-7xl mx-auto py-4 px-4 sm:px-6 lg:px-8 flex justify-between items-center"&gt;
        &lt;h1 class="text-2xl font-bold text-gray-900"&gt;Waiter Dashboard&lt;/h1&gt;
        &lt;div class="flex items-center space-x-4"&gt;
          &lt;SoundToggle /&gt;
          &lt;span class="text-sm text-gray-500"&gt;{{ activeRequests.length }} active requests&lt;/span&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/header&gt;

    &lt;main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8"&gt;
      &lt;div class="px-4 py-6 sm:px-0"&gt;
        &lt;div class="grid grid-cols-1 gap-6 lg:grid-cols-2"&gt;
          &lt;!-- Waiter Requests Section --&gt;
          &lt;div class="bg-white shadow overflow-hidden sm:rounded-lg"&gt;
            &lt;div class="px-4 py-5 sm:px-6 flex justify-between items-center"&gt;
              &lt;h2 class="text-lg font-medium text-gray-900"&gt;Active Requests&lt;/h2&gt;
            &lt;/div&gt;
            &lt;ul class="divide-y divide-gray-200"&gt;
              &lt;li
                v-for="request in activeRequests"
                :key="request.id"
                class="px-4 py-4 sm:px-6 hover:bg-gray-50"
              &gt;
                &lt;div class="flex items-center justify-between"&gt;
                  &lt;div class="flex-1"&gt;
                    &lt;div class="flex items-center"&gt;
                      &lt;div class="text-sm font-medium text-gray-900"&gt;
                        Table {{ request.tableNumber }}
                      &lt;/div&gt;
                      &lt;span
                        :class="[
                          'ml-2 px-2 py-1 text-xs font-medium rounded-full',
                          request.type === 'hail' ? 'bg-blue-100 text-blue-800' : 'bg-green-100 text-green-800'
                        ]"
                      &gt;
                        {{ request.type === 'hail' ? 'Assistance' : 'Order' }}
                      &lt;/span&gt;
                    &lt;/div&gt;
                    &lt;div class="mt-1 text-sm text-gray-500"&gt;
                      {{ request.message }}
                    &lt;/div&gt;
                    &lt;div class="mt-1 text-xs text-gray-400"&gt;
                      {{ formatTime(request.timestamp) }}
                    &lt;/div&gt;
                  &lt;/div&gt;
                  &lt;button
                    @click="completeRequest(request.id)"
                    class="ml-4 inline-flex items-center px-3 py-1 border border-transparent text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700"
                  &gt;
                    Complete
                  &lt;/button&gt;
                &lt;/div&gt;
              &lt;/li&gt;
              &lt;li v-if="activeRequests.length === 0" class="px-4 py-8 text-center text-gray-500"&gt;
                No active requests
              &lt;/li&gt;
            &lt;/ul&gt;
          &lt;/div&gt;

          &lt;!-- Assigned Orders Section --&gt;
          &lt;div class="bg-white shadow overflow-hidden sm:rounded-lg"&gt;
            &lt;div class="px-4 py-5 sm:px-6 flex justify-between items-center"&gt;
              &lt;h2 class="text-lg font-medium text-gray-900"&gt;Assigned Orders&lt;/h2&gt;
            &lt;/div&gt;
            &lt;ul class="divide-y divide-gray-200"&gt;
              &lt;li
                v-for="order in assignedOrders"
                :key="order.id"
                class="px-4 py-4 sm:px-6 hover:bg-gray-50"
              &gt;
                &lt;div class="flex items-center justify-between"&gt;
                  &lt;div class="flex-1"&gt;
                    &lt;div class="flex items-center"&gt;
                      &lt;div class="text-sm font-medium text-gray-900"&gt;
                        Order #{{ order.number }}
                      &lt;/div&gt;
                      &lt;span
                        :class="[
                          'ml-2 px-2 py-1 text-xs font-medium rounded-full',
                          order.status === 'ready' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
                        ]"
                      &gt;
                        {{ order.status }}
                      &lt;/span&gt;
                    &lt;/div&gt;
                    &lt;div class="mt-1 text-sm text-gray-500"&gt;
                      Table {{ order.tableNumber }} • {{ order.items.length }} items
                    &lt;/div&gt;
                  &lt;/div&gt;
                  &lt;button
                    v-if="order.status === 'ready'"
                    @click="deliverOrder(order.id)"
                    class="ml-4 inline-flex items-center px-3 py-1 border border-transparent text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700"
                  &gt;
                    Deliver
                  &lt;/button&gt;
                &lt;/div&gt;
              &lt;/li&gt;
              &lt;li v-if="assignedOrders.length === 0" class="px-4 py-8 text-center text-gray-500"&gt;
                No assigned orders
              &lt;/li&gt;
            &lt;/ul&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/main&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, onMounted, onUnmounted } from 'vue'
import { useStore } from 'vuex'
import { format, formatDistanceToNow } from 'date-fns'
import SoundToggle from '@/components/common/SoundToggle.vue'
import { soundService } from '@/services/sound'

const store = useStore()
const activeRequests = ref([])
const assignedOrders = ref([])
let wsConnection: WebSocket | null = null

const formatTime = (timestamp: Date) => {
  return formatDistanceToNow(new Date(timestamp), { addSuffix: true })
}

const completeRequest = async (requestId: string) => {
  try {
    await axios.post(`/api/waiter/requests/${requestId}/complete`)
    await fetchRequests()
  } catch (err) {
    console.error('Error completing request:', err)
  }
}

const deliverOrder = async (orderId: string) => {
  try {
    await axios.post(`/api/waiter/orders/${orderId}/deliver`)
    await fetchOrders()
  } catch (err) {
    console.error('Error delivering order:', err)
  }
}

const fetchRequests = async () => {
  try {
    const response = await axios.get('/api/waiter/requests/active')
    activeRequests.value = response.data
  } catch (err) {
    console.error('Error fetching requests:', err)
  }
}

const fetchOrders = async () => {
  try {
    const response = await axios.get('/api/waiter/orders/assigned')
    assignedOrders.value = response.data
  } catch (err) {
    console.error('Error fetching orders:', err)
  }
}

const setupWebSocket = () => {
  const restaurantId = store.state.currentRestaurant.id
  const waiterId = store.state.user.id
  wsConnection = new WebSocket(`${process.env.VUE_APP_WS_URL}/waiter`)

  wsConnection.onmessage = (event) => {
    const data = JSON.parse(event.data)
    
    switch (data.type) {
      case 'waiter_hail':
        soundService.play('waiter_hail')
        fetchRequests()
        break
      case 'order_assigned':
        soundService.play('order_assigned')
        fetchOrders()
        break
    }
  }

  wsConnection.onclose = () => {
    setTimeout(setupWebSocket, 1000) // Reconnect after 1 second
  }
}

onMounted(() => {
  fetchRequests()
  fetchOrders()
  setupWebSocket()
})

onUnmounted(() => {
  if (wsConnection) {
    wsConnection.close()
  }
})
&lt;/script&gt;
