&lt;template&gt;
  &lt;div class="min-h-screen bg-gray-100"&gt;
    &lt;header class="bg-white shadow"&gt;
      &lt;div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8"&gt;
        &lt;h1 class="text-3xl font-bold text-gray-900"&gt;Welcome, {{ customerName }}&lt;/h1&gt;
      &lt;/div&gt;
    &lt;/header&gt;

    &lt;main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8"&gt;
      &lt;div class="px-4 py-6 sm:px-0"&gt;
        &lt;div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3"&gt;
          &lt;!-- Order Status Card --&gt;
          &lt;div class="bg-white overflow-hidden shadow rounded-lg"&gt;
            &lt;div class="px-4 py-5 sm:p-6"&gt;
              &lt;h3 class="text-lg font-medium text-gray-900"&gt;Current Order&lt;/h3&gt;
              &lt;div v-if="currentOrder" class="mt-4"&gt;
                &lt;div class="flex justify-between text-sm text-gray-500"&gt;
                  &lt;span&gt;Status:&lt;/span&gt;
                  &lt;span class="font-medium"&gt;{{ currentOrder.status }}&lt;/span&gt;
                &lt;/div&gt;
                &lt;div class="flex justify-between text-sm text-gray-500 mt-2"&gt;
                  &lt;span&gt;Total:&lt;/span&gt;
                  &lt;span class="font-medium"&gt;{{ formatCurrency(currentOrder.total) }}&lt;/span&gt;
                &lt;/div&gt;
              &lt;/div&gt;
              &lt;div v-else class="mt-4 text-sm text-gray-500"&gt;
                No active orders
              &lt;/div&gt;
            &lt;/div&gt;
          &lt;/div&gt;

          &lt;!-- Table Information Card --&gt;
          &lt;div class="bg-white overflow-hidden shadow rounded-lg"&gt;
            &lt;div class="px-4 py-5 sm:p-6"&gt;
              &lt;h3 class="text-lg font-medium text-gray-900"&gt;Your Table&lt;/h3&gt;
              &lt;div class="mt-4 space-y-2"&gt;
                &lt;div class="flex justify-between text-sm text-gray-500"&gt;
                  &lt;span&gt;Table Number:&lt;/span&gt;
                  &lt;span class="font-medium"&gt;{{ tableNumber }}&lt;/span&gt;
                &lt;/div&gt;
                &lt;div class="flex justify-between text-sm text-gray-500"&gt;
                  &lt;span&gt;Section:&lt;/span&gt;
                  &lt;span class="font-medium"&gt;{{ section }}&lt;/span&gt;
                &lt;/div&gt;
              &lt;/div&gt;
            &lt;/div&gt;
          &lt;/div&gt;

          &lt;!-- Quick Actions Card --&gt;
          &lt;div class="bg-white overflow-hidden shadow rounded-lg"&gt;
            &lt;div class="px-4 py-5 sm:p-6"&gt;
              &lt;h3 class="text-lg font-medium text-gray-900"&gt;Quick Actions&lt;/h3&gt;
              &lt;div class="mt-4 space-y-3"&gt;
                &lt;button
                  @click="viewMenu"
                  class="w-full inline-flex justify-center items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700"
                &gt;
                  &lt;MenuIcon class="h-5 w-5 mr-2" /&gt;
                  View Menu
                &lt;/button&gt;
                &lt;button
                  @click="viewBill"
                  class="w-full inline-flex justify-center items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700"
                &gt;
                  &lt;ReceiptTaxIcon class="h-5 w-5 mr-2" /&gt;
                  View Bill
                &lt;/button&gt;
              &lt;/div&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/main&gt;

    &lt;!-- Floating Waiter Button --&gt;
    &lt;WaiterButton /&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, computed, onMounted } from 'vue'
import { MenuIcon, ReceiptTaxIcon } from '@heroicons/vue/outline'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import WaiterButton from '@/components/customer/WaiterButton.vue'

const store = useStore()
const router = useRouter()
const currentOrder = ref(null)

const customerName = computed(() => store.state.user.name)
const tableNumber = computed(() => store.state.currentTable.number)
const section = computed(() => store.state.currentTable.section)

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(amount)
}

const viewMenu = () => {
  router.push('/menu')
}

const viewBill = () => {
  router.push('/bill')
}

const fetchCurrentOrder = async () => {
  try {
    const response = await axios.get(`/api/orders/current`)
    currentOrder.value = response.data
  } catch (err) {
    console.error('Error fetching current order:', err)
  }
}

onMounted(() => {
  fetchCurrentOrder()
})
&lt;/script&gt;
