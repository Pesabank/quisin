&lt;template&gt;
  &lt;div
    class="bg-white shadow-sm rounded-lg overflow-hidden border-l-4"
    :class="[
      order.priority === 'high' ? 'border-red-500' : 
      order.priority === 'medium' ? 'border-yellow-500' : 'border-green-500'
    ]"
  &gt;
    &lt;div class="p-4"&gt;
      &lt;div class="flex items-center justify-between"&gt;
        &lt;div class="flex items-center space-x-3"&gt;
          &lt;span class="text-lg font-semibold text-gray-900"&gt;#{{ order.id }}&lt;/span&gt;
          &lt;span
            class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
            :class="statusColors[order.status]"
          &gt;
            {{ order.status }}
          &lt;/span&gt;
        &lt;/div&gt;
        &lt;div class="text-sm text-gray-500"&gt;
          {{ formatTime(order.orderTime) }}
        &lt;/div&gt;
      &lt;/div&gt;

      &lt;div class="mt-4 space-y-2"&gt;
        &lt;div v-for="item in order.items" :key="item.id" class="flex items-center justify-between"&gt;
          &lt;div class="flex items-center"&gt;
            &lt;span class="text-sm font-medium text-gray-900"&gt;{{ item.quantity }}x&lt;/span&gt;
            &lt;span class="ml-2 text-sm text-gray-700"&gt;{{ item.name }}&lt;/span&gt;
            &lt;span v-if="item.notes" class="ml-2 text-xs text-gray-500 italic"&gt;
              ({{ item.notes }})
            &lt;/span&gt;
          &lt;/div&gt;
          &lt;div&gt;
            &lt;button
              v-if="!item.completed"
              @click="$emit('complete-item', { orderId: order.id, itemId: item.id })"
              class="inline-flex items-center p-1 border border-transparent rounded-full text-green-600 hover:bg-green-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
            &gt;
              &lt;svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"&gt;
                &lt;path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" /&gt;
              &lt;/svg&gt;
            &lt;/button&gt;
            &lt;svg
              v-else
              class="h-5 w-5 text-green-500"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 20 20"
              fill="currentColor"
            &gt;
              &lt;path
                fill-rule="evenodd"
                d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                clip-rule="evenodd"
              /&gt;
            &lt;/svg&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/div&gt;

      &lt;div class="mt-4 flex justify-between items-center"&gt;
        &lt;div class="flex items-center space-x-2 text-sm text-gray-500"&gt;
          &lt;svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"&gt;
            &lt;path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /&gt;
          &lt;/svg&gt;
          &lt;span&gt;{{ formatDuration(order.preparationTime) }}&lt;/span&gt;
        &lt;/div&gt;
        
        &lt;div class="flex space-x-2"&gt;
          &lt;button
            v-if="order.status === 'pending'"
            @click="$emit('start-order', order.id)"
            class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-full shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          &gt;
            Start Preparing
          &lt;/button&gt;
          &lt;button
            v-if="order.status === 'preparing'"
            @click="$emit('complete-order', order.id)"
            class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-full shadow-sm text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
          &gt;
            Mark as Ready
          &lt;/button&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/div&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { formatDistanceToNow, format } from 'date-fns'

interface OrderItem {
  id: string
  name: string
  quantity: number
  notes?: string
  completed: boolean
}

interface Order {
  id: string
  status: 'pending' | 'preparing' | 'ready' | 'delivered'
  priority: 'low' | 'medium' | 'high'
  items: OrderItem[]
  orderTime: string
  preparationTime: number
}

const props = defineProps&lt;{
  order: Order
}&gt;()

defineEmits(['complete-item', 'start-order', 'complete-order'])

const statusColors = {
  pending: 'bg-yellow-100 text-yellow-800',
  preparing: 'bg-blue-100 text-blue-800',
  ready: 'bg-green-100 text-green-800',
  delivered: 'bg-gray-100 text-gray-800'
}

const formatTime = (time: string) => {
  return format(new Date(time), 'HH:mm')
}

const formatDuration = (minutes: number) => {
  return `${minutes} min`
}
&lt;/script&gt;
