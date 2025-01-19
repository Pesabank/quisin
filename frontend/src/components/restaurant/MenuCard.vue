&lt;template&gt;
  &lt;div class="bg-white shadow rounded-lg overflow-hidden"&gt;
    &lt;div class="relative"&gt;
      &lt;img
        :src="item.image || '/images/default-dish.jpg'"
        :alt="item.name"
        class="w-full h-48 object-cover"
      &gt;
      &lt;div
        v-if="item.category"
        class="absolute top-2 right-2 px-2 py-1 text-xs font-semibold rounded-full"
        :class="categoryColors[item.category] || 'bg-gray-100 text-gray-800'"
      &gt;
        {{ item.category }}
      &lt;/div&gt;
    &lt;/div&gt;
    
    &lt;div class="p-4"&gt;
      &lt;div class="flex justify-between items-start"&gt;
        &lt;div&gt;
          &lt;h3 class="text-lg font-medium text-gray-900"&gt;{{ item.name }}&lt;/h3&gt;
          &lt;p class="mt-1 text-sm text-gray-500 line-clamp-2"&gt;{{ item.description }}&lt;/p&gt;
        &lt;/div&gt;
        &lt;p class="text-lg font-semibold text-gray-900"&gt;{{ formatPrice(item.price) }}&lt;/p&gt;
      &lt;/div&gt;
      
      &lt;div class="mt-4 flex items-center justify-between"&gt;
        &lt;div class="flex items-center"&gt;
          &lt;span
            class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
            :class="item.available ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
          &gt;
            {{ item.available ? 'Available' : 'Unavailable' }}
          &lt;/span&gt;
        &lt;/div&gt;
        &lt;div class="flex space-x-2"&gt;
          &lt;button
            @click="$emit('edit', item)"
            class="inline-flex items-center p-1 border border-transparent rounded-full text-blue-600 hover:bg-blue-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          &gt;
            &lt;svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"&gt;
              &lt;path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" /&gt;
            &lt;/svg&gt;
          &lt;/button&gt;
          &lt;button
            @click="$emit('toggle-availability', item)"
            class="inline-flex items-center p-1 border border-transparent rounded-full text-yellow-600 hover:bg-yellow-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-yellow-500"
          &gt;
            &lt;svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"&gt;
              &lt;path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /&gt;
            &lt;/svg&gt;
          &lt;/button&gt;
          &lt;button
            @click="$emit('delete', item)"
            class="inline-flex items-center p-1 border border-transparent rounded-full text-red-600 hover:bg-red-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
          &gt;
            &lt;svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"&gt;
              &lt;path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /&gt;
            &lt;/svg&gt;
          &lt;/button&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/div&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
interface MenuItem {
  id: string
  name: string
  description: string
  price: number
  category: string
  image?: string
  available: boolean
}

interface Props {
  item: MenuItem
}

defineProps&lt;Props&gt;()
defineEmits(['edit', 'toggle-availability', 'delete'])

const categoryColors = {
  'Appetizers': 'bg-yellow-100 text-yellow-800',
  'Main Course': 'bg-green-100 text-green-800',
  'Desserts': 'bg-pink-100 text-pink-800',
  'Beverages': 'bg-blue-100 text-blue-800',
  'Specials': 'bg-purple-100 text-purple-800'
}

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(price)
}
&lt;/script&gt;
