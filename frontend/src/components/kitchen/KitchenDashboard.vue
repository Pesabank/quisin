&lt;template&gt;
  &lt;div class="min-h-screen bg-gray-100"&gt;
    &lt;header class="bg-white shadow"&gt;
      &lt;div class="max-w-7xl mx-auto py-4 px-4 sm:px-6 lg:px-8 flex justify-between items-center"&gt;
        &lt;h1 class="text-2xl font-bold text-gray-900"&gt;Kitchen Dashboard&lt;/h1&gt;
        &lt;div class="flex items-center space-x-4"&gt;
          &lt;SoundToggle /&gt;
          &lt;span class="text-sm text-gray-500"&gt;{{ pendingOrders.length }} pending orders&lt;/span&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/header&gt;

    &lt;main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8"&gt;
      &lt;div class="px-4 py-6 sm:px-0"&gt;
        &lt;div class="grid grid-cols-1 gap-6 lg:grid-cols-2 xl:grid-cols-3"&gt;
          &lt;!-- Pending Orders --&gt;
          &lt;div
            v-for="order in pendingOrders"
            :key="order.id"
            class="bg-white overflow-hidden shadow rounded-lg"
          &gt;
            &lt;div class="px-4 py-5 sm:p-6"&gt;
              &lt;div class="flex justify-between items-start"&gt;
                &lt;div&gt;
                  &lt;h3 class="text-lg font-medium text-gray-900"&gt;
                    Order #{{ order.number }}
                  &lt;/h3&gt;
                  &lt;p class="mt-1 text-sm text-gray-500"&gt;
                    Table {{ order.tableNumber }}
                  &lt;/p&gt;
                &lt;/div&gt;
                &lt;span
                  :class="[
                    'px-2 py-1 text-xs font-medium rounded-full',
                    order.priority === 'high'
                      ? 'bg-red-100 text-red-800'
                      : order.priority === 'medium'
                      ? 'bg-yellow-100 text-yellow-800'
                      : 'bg-green-100 text-green-800'
                  ]"
                &gt;
                  {{ order.priority }}
                &lt;/span&gt;
              &lt;/div&gt;

              &lt;div class="mt-4 space-y-2"&gt;
                &lt;div
                  v-for="item in order.items"
                  :key="item.id"
                  class="flex justify-between items-start"
                &gt;
                  &lt;div class="flex-1"&gt;
                    &lt;p class="text-sm font-medium text-gray-900"&gt;
                      {{ item.quantity }}x {{ item.name }}
                    &lt;/p&gt;
                    &lt;p v-if="item.notes" class="mt-1 text-xs text-gray-500"&gt;
                      Note: {{ item.notes }}
                    &lt;/p&gt;
                  &lt;/div&gt;
                  &lt;button
                    @click="toggleItemStatus(order.id, item.id)"
                    :class="[
                      'px-2 py-1 text-xs font-medium rounded',
                      item.completed
                        ? 'bg-green-100 text-green-800'
                        : 'bg-gray-100 text-gray-800'
                    ]"
                  &gt;
                    {{ item.completed ? 'Done' : 'Mark Done' }}
                  &lt;/button&gt;
                &lt;/div&gt;
              &lt;/div&gt;

              &lt;div class="mt-6 flex justify-end space-x-3"&gt;
                &lt;button
                  @click="completeOrder(order.id)"
                  class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700"
                  :disabled="!allItemsCompleted(order)"
                &gt;
                  Complete Order
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
import SoundToggle from '@/components/common/SoundToggle.vue'
import { soundService } from '@/services/sound'

const store = useStore()
const pendingOrders = ref([])
let wsConnection: WebSocket | null = null

const allItemsCompleted = (order: any) => {
  return order.items.every((item: any) => item.completed)
}

const toggleItemStatus = async (orderId: string, itemId: string) => {
  try {
    await axios.post(`/api/kitchen/orders/${orderId}/items/${itemId}/toggle`)
    await fetchOrders()
  } catch (err) {
    console.error('Error toggling item status:', err)
  }
}

const completeOrder = async (orderId: string) => {
  try {
    await axios.post(`/api/kitchen/orders/${orderId}/complete`)
    await fetchOrders()
  } catch (err) {
    console.error('Error completing order:', err)
  }
}

const fetchOrders = async () => {
  try {
    const response = await axios.get('/api/kitchen/orders/pending')
    pendingOrders.value = response.data
  } catch (err) {
    console.error('Error fetching orders:', err)
  }
}

const setupWebSocket = () => {
  const restaurantId = store.state.currentRestaurant.id
  wsConnection = new WebSocket(`${process.env.VUE_APP_WS_URL}/kitchen`)

  wsConnection.onmessage = (event) => {
    const data = JSON.parse(event.data)
    
    if (data.type === 'new_order') {
      soundService.play('new_order')
      fetchOrders()
    }
  }

  wsConnection.onclose = () => {
    setTimeout(setupWebSocket, 1000) // Reconnect after 1 second
  }
}

onMounted(() => {
  fetchOrders()
  setupWebSocket()
})

onUnmounted(() => {
  if (wsConnection) {
    wsConnection.close()
  }
})
&lt;/script&gt;
