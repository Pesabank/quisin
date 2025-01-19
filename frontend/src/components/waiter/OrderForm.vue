&lt;template&gt;
  &lt;div class="bg-white rounded-lg shadow p-6"&gt;
    &lt;div class="mb-6"&gt;
      &lt;h2 class="text-lg font-medium text-gray-900"&gt;New Order - Table {{ tableNumber }}&lt;/h2&gt;
      &lt;p class="mt-1 text-sm text-gray-500"&gt;Add items to the order&lt;/p&gt;
    &lt;/div&gt;

    &lt;div class="space-y-6"&gt;
      &lt;div class="flex items-center space-x-4"&gt;
        &lt;div class="flex-1"&gt;
          &lt;select
            v-model="selectedCategory"
            class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-md"
          &gt;
            &lt;option value=""&gt;All Categories&lt;/option&gt;
            &lt;option v-for="category in categories" :key="category" :value="category"&gt;
              {{ category }}
            &lt;/option&gt;
          &lt;/select&gt;
        &lt;/div&gt;

        &lt;div class="flex-1"&gt;
          &lt;input
            type="text"
            v-model="searchQuery"
            placeholder="Search menu items..."
            class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
          &gt;
        &lt;/div&gt;
      &lt;/div&gt;

      &lt;div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3"&gt;
        &lt;div
          v-for="item in filteredMenuItems"
          :key="item.id"
          class="relative flex items-center space-x-3 rounded-lg border border-gray-300 bg-white px-6 py-5 shadow-sm hover:border-gray-400"
        &gt;
          &lt;div class="flex-1 min-w-0"&gt;
            &lt;p class="text-sm font-medium text-gray-900"&gt;{{ item.name }}&lt;/p&gt;
            &lt;p class="text-sm text-gray-500 truncate"&gt;{{ item.description }}&lt;/p&gt;
            &lt;p class="text-sm font-medium text-gray-900 mt-1"&gt;{{ formatPrice(item.price) }}&lt;/p&gt;
          &lt;/div&gt;
          &lt;div class="flex items-center space-x-2"&gt;
            &lt;button
              @click="decrementItem(item)"
              class="inline-flex items-center p-1 border border-transparent rounded-full text-gray-400 hover:bg-gray-100"
              :disabled="!getItemQuantity(item.id)"
            &gt;
              &lt;svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"&gt;
                &lt;path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4" /&gt;
              &lt;/svg&gt;
            &lt;/button&gt;
            &lt;span class="text-sm font-medium text-gray-900"&gt;{{ getItemQuantity(item.id) }}&lt;/span&gt;
            &lt;button
              @click="incrementItem(item)"
              class="inline-flex items-center p-1 border border-transparent rounded-full text-gray-400 hover:bg-gray-100"
            &gt;
              &lt;svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"&gt;
                &lt;path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" /&gt;
              &lt;/svg&gt;
            &lt;/button&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/div&gt;

      &lt;div class="mt-6 border-t border-gray-200 pt-6"&gt;
        &lt;h3 class="text-lg font-medium text-gray-900"&gt;Order Summary&lt;/h3&gt;
        &lt;div class="mt-4 space-y-4"&gt;
          &lt;div v-for="item in orderItems" :key="item.id" class="flex justify-between text-sm"&gt;
            &lt;div&gt;
              &lt;span class="font-medium text-gray-900"&gt;{{ item.quantity }}x&lt;/span&gt;
              &lt;span class="ml-2 text-gray-500"&gt;{{ item.name }}&lt;/span&gt;
            &lt;/div&gt;
            &lt;span class="font-medium text-gray-900"&gt;{{ formatPrice(item.price * item.quantity) }}&lt;/span&gt;
          &lt;/div&gt;
          &lt;div class="border-t border-gray-200 pt-4 flex justify-between"&gt;
            &lt;span class="font-medium text-gray-900"&gt;Total&lt;/span&gt;
            &lt;span class="font-medium text-gray-900"&gt;{{ formatPrice(orderTotal) }}&lt;/span&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/div&gt;

      &lt;div class="mt-6 flex justify-end space-x-3"&gt;
        &lt;button
          type="button"
          @click="$emit('cancel')"
          class="inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
        &gt;
          Cancel
        &lt;/button&gt;
        &lt;button
          type="button"
          @click="submitOrder"
          :disabled="!orderItems.length"
          class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:bg-gray-300"
        &gt;
          Place Order
        &lt;/button&gt;
      &lt;/div&gt;
    &lt;/div&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, computed } from 'vue'

interface MenuItem {
  id: string
  name: string
  description: string
  price: number
  category: string
}

interface OrderItem extends MenuItem {
  quantity: number
}

const props = defineProps&lt;{
  tableNumber: number
  menuItems: MenuItem[]
  categories: string[]
}&gt;()

const emit = defineEmits(['submit', 'cancel'])

const selectedCategory = ref('')
const searchQuery = ref('')
const orderItems = ref&lt;OrderItem[]&gt;([])

const filteredMenuItems = computed(() =&gt; {
  let items = props.menuItems

  if (selectedCategory.value) {
    items = items.filter(item =&gt; item.category === selectedCategory.value)
  }

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    items = items.filter(item =&gt;
      item.name.toLowerCase().includes(query) ||
      item.description.toLowerCase().includes(query)
    )
  }

  return items
})

const orderTotal = computed(() =&gt; {
  return orderItems.value.reduce((total, item) =&gt; total + (item.price * item.quantity), 0)
})

const formatPrice = (price: number) =&gt; {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(price)
}

const getItemQuantity = (itemId: string) =&gt; {
  const item = orderItems.value.find(item =&gt; item.id === itemId)
  return item ? item.quantity : 0
}

const incrementItem = (item: MenuItem) =&gt; {
  const existingItem = orderItems.value.find(orderItem =&gt; orderItem.id === item.id)
  if (existingItem) {
    existingItem.quantity++
  } else {
    orderItems.value.push({ ...item, quantity: 1 })
  }
}

const decrementItem = (item: MenuItem) =&gt; {
  const index = orderItems.value.findIndex(orderItem =&gt; orderItem.id === item.id)
  if (index !== -1) {
    if (orderItems.value[index].quantity === 1) {
      orderItems.value.splice(index, 1)
    } else {
      orderItems.value[index].quantity--
    }
  }
}

const submitOrder = () =&gt; {
  emit('submit', {
    tableNumber: props.tableNumber,
    items: orderItems.value,
    total: orderTotal.value
  })
}
&lt;/script&gt;
