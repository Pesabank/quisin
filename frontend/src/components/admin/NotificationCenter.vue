&lt;template&gt;
  &lt;div class="fixed inset-y-0 right-0 w-96 bg-white shadow-lg transform transition-transform duration-300"
    :class="{ 'translate-x-full': !isOpen }"
  &gt;
    &lt;div class="h-full flex flex-col"&gt;
      &lt;div class="p-4 border-b flex justify-between items-center"&gt;
        &lt;h2 class="text-lg font-semibold text-gray-900"&gt;Notifications&lt;/h2&gt;
        &lt;div class="flex items-center space-x-4"&gt;
          &lt;button
            v-if="notifications.length > 0"
            @click="clearAll"
            class="text-sm text-gray-500 hover:text-gray-700"
          &gt;
            Clear All
          &lt;/button&gt;
          &lt;button @click="$emit('close')" class="text-gray-500 hover:text-gray-700"&gt;
            &lt;XIcon class="h-5 w-5" /&gt;
          &lt;/button&gt;
        &lt;/div&gt;
      &lt;/div&gt;

      &lt;div class="flex-1 overflow-y-auto p-4 space-y-4"&gt;
        &lt;TransitionGroup
          enter-active-class="transition duration-300 ease-out"
          enter-from-class="transform translate-x-full opacity-0"
          enter-to-class="transform translate-x-0 opacity-100"
          leave-active-class="transition duration-200 ease-in"
          leave-from-class="transform translate-x-0 opacity-100"
          leave-to-class="transform translate-x-full opacity-0"
        &gt;
          &lt;div
            v-for="notification in sortedNotifications"
            :key="notification.timestamp"
            class="bg-white border rounded-lg shadow-sm p-4"
            :class="{
              'border-blue-200 bg-blue-50': notification.severity === 'info',
              'border-yellow-200 bg-yellow-50': notification.severity === 'warning',
              'border-red-200 bg-red-50': notification.severity === 'error',
              'border-green-200 bg-green-50': notification.severity === 'success'
            }"
          &gt;
            &lt;div class="flex justify-between items-start"&gt;
              &lt;div class="flex-1"&gt;
                &lt;h3 class="text-sm font-medium text-gray-900"&gt;
                  {{ notification.title }}
                &lt;/h3&gt;
                &lt;p class="mt-1 text-sm text-gray-600"&gt;
                  {{ notification.message }}
                &lt;/p&gt;
                &lt;div v-if="notification.type === 'analytics_alert'" class="mt-2"&gt;
                  &lt;div class="text-xs text-gray-500"&gt;
                    Current Value: {{ formatValue(notification.data.value, notification.data.metric) }}
                  &lt;/div&gt;
                  &lt;div class="text-xs text-gray-500"&gt;
                    Threshold: {{ formatValue(notification.data.threshold, notification.data.metric) }}
                  &lt;/div&gt;
                &lt;/div&gt;
                &lt;div class="mt-2 text-xs text-gray-400"&gt;
                  {{ formatTime(notification.timestamp) }}
                &lt;/div&gt;
              &lt;/div&gt;
              &lt;button
                @click="removeNotification(notification)"
                class="ml-4 text-gray-400 hover:text-gray-500"
              &gt;
                &lt;XIcon class="h-4 w-4" /&gt;
              &lt;/button&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/TransitionGroup&gt;

        &lt;div
          v-if="notifications.length === 0"
          class="text-center text-gray-500 py-8"
        &gt;
          No notifications
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/div&gt;
  &lt;/div&gt;

  &lt;!-- Notification Badge --&gt;
  &lt;div
    v-if="!isOpen"
    class="fixed right-0 top-20 bg-white shadow-lg rounded-l-lg p-2 cursor-pointer"
    @click="$emit('open')"
  &gt;
    &lt;div class="relative"&gt;
      &lt;BellIcon class="h-6 w-6 text-gray-500" /&gt;
      &lt;div
        v-if="notifications.length > 0"
        class="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center"
      &gt;
        {{ notifications.length }}
      &lt;/div&gt;
    &lt;/div&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { XIcon, BellIcon } from '@heroicons/vue/outline'
import { useStore } from 'vuex'
import { format, formatDistanceToNow } from 'date-fns'

interface Notification {
  type: string;
  title: string;
  message: string;
  severity: 'info' | 'warning' | 'error' | 'success';
  data?: any;
  timestamp: Date;
}

const props = defineProps&lt;{
  isOpen: boolean;
}&gt;()

const emit = defineEmits(['open', 'close'])
const store = useStore()
const notifications = ref&lt;Notification[]&gt;([])
let ws: WebSocket | null = null

const sortedNotifications = computed(() => {
  return [...notifications.value].sort((a, b) => 
    new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
  )
})

const formatTime = (timestamp: Date) => {
  const date = new Date(timestamp)
  return formatDistanceToNow(date, { addSuffix: true })
}

const formatValue = (value: number, metric: string) => {
  switch (metric) {
    case 'totalSales':
      return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
      }).format(value)
    case 'orderCount':
      return `${value} orders`
    case 'customerSatisfaction':
      return `${value.toFixed(1)} stars`
    case 'orderCompletionRate':
      return `${value.toFixed(1)}%`
    default:
      return value.toString()
  }
}

const setupWebSocket = () => {
  const restaurantId = store.state.currentRestaurant.id
  const userId = store.state.user.id
  ws = new WebSocket(`${process.env.VUE_APP_WS_URL}/notifications`)

  ws.onopen = () => {
    ws?.send(JSON.stringify({
      type: 'subscribe',
      restaurantId,
      userId
    }))
  }

  ws.onmessage = (event) => {
    const notification = JSON.parse(event.data)
    notifications.value.push({
      ...notification,
      timestamp: new Date(notification.timestamp)
    })
  }

  ws.onclose = () => {
    setTimeout(setupWebSocket, 1000) // Reconnect after 1 second
  }
}

const removeNotification = (notification: Notification) => {
  const index = notifications.value.findIndex(n => 
    n.timestamp === notification.timestamp && n.message === notification.message
  )
  if (index !== -1) {
    notifications.value.splice(index, 1)
  }
}

const clearAll = async () => {
  try {
    const restaurantId = store.state.currentRestaurant.id
    await axios.delete(`/api/notifications/${restaurantId}`)
    notifications.value = []
  } catch (err) {
    console.error('Error clearing notifications:', err)
  }
}

onMounted(() => {
  setupWebSocket()
})

onUnmounted(() => {
  if (ws) {
    ws.close()
  }
})
&lt;/script&gt;
