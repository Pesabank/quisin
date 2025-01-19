&lt;template&gt;
  &lt;div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4"&gt;
    &lt;div
      v-for="table in tables"
      :key="table.id"
      class="relative bg-white rounded-lg shadow-sm overflow-hidden hover:shadow-md transition-shadow duration-200"
      :class="{
        'border-2 border-green-500': table.status === 'available',
        'border-2 border-red-500': table.status === 'occupied',
        'border-2 border-yellow-500': table.status === 'reserved'
      }"
    &gt;
      &lt;div class="p-4"&gt;
        &lt;div class="flex justify-between items-start"&gt;
          &lt;div&gt;
            &lt;h3 class="text-lg font-medium text-gray-900"&gt;Table {{ table.number }}&lt;/h3&gt;
            &lt;p class="text-sm text-gray-500"&gt;{{ table.capacity }} seats&lt;/p&gt;
          &lt;/div&gt;
          &lt;span
            class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
            :class="statusClasses[table.status]"
          &gt;
            {{ table.status }}
          &lt;/span&gt;
        &lt;/div&gt;

        &lt;div class="mt-4 space-y-2"&gt;
          &lt;div v-if="table.currentOrder" class="text-sm"&gt;
            &lt;p class="font-medium text-gray-700"&gt;Current Order:&lt;/p&gt;
            &lt;p class="text-gray-600"&gt;Order #{{ table.currentOrder.id }}&lt;/p&gt;
            &lt;p class="text-gray-500"&gt;{{ table.currentOrder.items }} items&lt;/p&gt;
            &lt;p class="text-gray-500"&gt;Started: {{ formatTime(table.currentOrder.startTime) }}&lt;/p&gt;
          &lt;/div&gt;

          &lt;div v-if="table.reservation" class="text-sm"&gt;
            &lt;p class="font-medium text-gray-700"&gt;Reserved for:&lt;/p&gt;
            &lt;p class="text-gray-600"&gt;{{ table.reservation.customerName }}&lt;/p&gt;
            &lt;p class="text-gray-500"&gt;{{ formatTime(table.reservation.time) }}&lt;/p&gt;
          &lt;/div&gt;
        &lt;/div&gt;

        &lt;div class="mt-4 flex justify-between items-center"&gt;
          &lt;div class="flex space-x-2"&gt;
            &lt;button
              v-if="table.status === 'available'"
              @click="$emit('seat-guests', table)"
              class="inline-flex items-center px-2.5 py-1.5 border border-transparent text-xs font-medium rounded text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            &gt;
              Seat Guests
            &lt;/button&gt;
            &lt;button
              v-if="table.status === 'occupied'"
              @click="$emit('view-order', table)"
              class="inline-flex items-center px-2.5 py-1.5 border border-transparent text-xs font-medium rounded text-blue-700 bg-blue-100 hover:bg-blue-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            &gt;
              View Order
            &lt;/button&gt;
          &lt;/div&gt;

          &lt;div class="flex items-center"&gt;
            &lt;button
              @click="$emit('manage-table', table)"
              class="inline-flex items-center p-1 border border-transparent rounded-full text-gray-400 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            &gt;
              &lt;svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"&gt;
                &lt;path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z" /&gt;
              &lt;/svg&gt;
            &lt;/button&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/div&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { format } from 'date-fns'

interface TableOrder {
  id: string
  items: number
  startTime: string
}

interface Reservation {
  customerName: string
  time: string
}

interface Table {
  id: string
  number: number
  capacity: number
  status: 'available' | 'occupied' | 'reserved'
  currentOrder?: TableOrder
  reservation?: Reservation
}

const props = defineProps&lt;{
  tables: Table[]
}&gt;()

defineEmits(['seat-guests', 'view-order', 'manage-table'])

const statusClasses = {
  available: 'bg-green-100 text-green-800',
  occupied: 'bg-red-100 text-red-800',
  reserved: 'bg-yellow-100 text-yellow-800'
}

const formatTime = (time: string) => {
  return format(new Date(time), 'HH:mm')
}
&lt;/script&gt;
