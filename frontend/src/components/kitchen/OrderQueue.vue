&lt;template&gt;
  &lt;div class="flex flex-col h-full"&gt;
    &lt;div class="flex-1 min-h-0"&gt;
      &lt;div class="h-full flex space-x-4 overflow-hidden"&gt;
        &lt;!-- Pending Orders --&gt;
        &lt;div class="flex-1 flex flex-col min-w-0"&gt;
          &lt;div class="flex items-center justify-between py-3 px-4 bg-gray-50 rounded-t-lg"&gt;
            &lt;h3 class="text-sm font-medium text-gray-900"&gt;Pending ({{ pendingOrders.length }})&lt;/h3&gt;
            &lt;span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800"&gt;
              New Orders
            &lt;/span&gt;
          &lt;/div&gt;
          &lt;div class="flex-1 overflow-y-auto"&gt;
            &lt;div class="p-2 space-y-3"&gt;
              &lt;OrderCard
                v-for="order in pendingOrders"
                :key="order.id"
                :order="order"
                @complete-item="completeItem"
                @start-order="startOrder"
                @complete-order="completeOrder"
              /&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;

        &lt;!-- In Progress Orders --&gt;
        &lt;div class="flex-1 flex flex-col min-w-0"&gt;
          &lt;div class="flex items-center justify-between py-3 px-4 bg-gray-50 rounded-t-lg"&gt;
            &lt;h3 class="text-sm font-medium text-gray-900"&gt;Preparing ({{ preparingOrders.length }})&lt;/h3&gt;
            &lt;span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800"&gt;
              In Progress
            &lt;/span&gt;
          &lt;/div&gt;
          &lt;div class="flex-1 overflow-y-auto"&gt;
            &lt;div class="p-2 space-y-3"&gt;
              &lt;OrderCard
                v-for="order in preparingOrders"
                :key="order.id"
                :order="order"
                @complete-item="completeItem"
                @start-order="startOrder"
                @complete-order="completeOrder"
              /&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;

        &lt;!-- Ready Orders --&gt;
        &lt;div class="flex-1 flex flex-col min-w-0"&gt;
          &lt;div class="flex items-center justify-between py-3 px-4 bg-gray-50 rounded-t-lg"&gt;
            &lt;h3 class="text-sm font-medium text-gray-900"&gt;Ready ({{ readyOrders.length }})&lt;/h3&gt;
            &lt;span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800"&gt;
              Completed
            &lt;/span&gt;
          &lt;/div&gt;
          &lt;div class="flex-1 overflow-y-auto"&gt;
            &lt;div class="p-2 space-y-3"&gt;
              &lt;OrderCard
                v-for="order in readyOrders"
                :key="order.id"
                :order="order"
                @complete-item="completeItem"
                @start-order="startOrder"
                @complete-order="completeOrder"
              /&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/div&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { computed } from 'vue'
import OrderCard from './OrderCard.vue'
import { useNotificationStore } from '~/stores/notification'

const notificationStore = useNotificationStore()

interface Props {
  orders: Array&lt;{
    id: string
    status: 'pending' | 'preparing' | 'ready' | 'delivered'
    priority: 'low' | 'medium' | 'high'
    items: Array&lt;{
      id: string
      name: string
      quantity: number
      notes?: string
      completed: boolean
    }&gt;
    orderTime: string
    preparationTime: number
  }&gt;
}

const props = defineProps&lt;Props&gt;()
const emit = defineEmits(['update:orders'])

// Filter orders by status
const pendingOrders = computed(() => 
  props.orders.filter(order => order.status === 'pending')
    .sort((a, b) => priorityValue(b.priority) - priorityValue(a.priority))
)

const preparingOrders = computed(() => 
  props.orders.filter(order => order.status === 'preparing')
)

const readyOrders = computed(() => 
  props.orders.filter(order => order.status === 'ready')
)

// Helper function to convert priority to numeric value for sorting
function priorityValue(priority: string): number {
  switch (priority) {
    case 'high': return 3
    case 'medium': return 2
    case 'low': return 1
    default: return 0
  }
}

// Event handlers
const completeItem = ({ orderId, itemId }: { orderId: string, itemId: string }) => {
  const updatedOrders = props.orders.map(order => {
    if (order.id === orderId) {
      return {
        ...order,
        items: order.items.map(item => 
          item.id === itemId ? { ...item, completed: true } : item
        )
      }
    }
    return order
  })
  
  emit('update:orders', updatedOrders)
  notificationStore.success('Item marked as completed')
}

const startOrder = (orderId: string) => {
  const updatedOrders = props.orders.map(order => 
    order.id === orderId ? { ...order, status: 'preparing' } : order
  )
  
  emit('update:orders', updatedOrders)
  notificationStore.info('Order preparation started')
}

const completeOrder = (orderId: string) => {
  const updatedOrders = props.orders.map(order => 
    order.id === orderId ? { ...order, status: 'ready' } : order
  )
  
  emit('update:orders', updatedOrders)
  notificationStore.success('Order marked as ready')
}
&lt;/script&gt;
