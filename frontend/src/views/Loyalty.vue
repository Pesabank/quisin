&lt;template&gt;
  &lt;div class="min-h-full"&gt;
    &lt;header class="bg-white shadow"&gt;
      &lt;div class="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8"&gt;
        &lt;h1 class="text-3xl font-bold leading-tight tracking-tight text-gray-900"&gt;Customer Loyalty Program&lt;/h1&gt;
      &lt;/div&gt;
    &lt;/header&gt;

    &lt;main&gt;
      &lt;div class="mx-auto max-w-7xl py-6 sm:px-6 lg:px-8"&gt;
        &lt;LoyaltyProgram
          v-if="isEnabled"
          :customers="customers"
          @update:customers="updateCustomers"
        /&gt;
        &lt;div
          v-else
          class="rounded-md bg-yellow-50 p-4"
        &gt;
          &lt;div class="flex"&gt;
            &lt;div class="flex-shrink-0"&gt;
              &lt;ExclamationTriangleIcon class="h-5 w-5 text-yellow-400" aria-hidden="true" /&gt;
            &lt;/div&gt;
            &lt;div class="ml-3"&gt;
              &lt;h3 class="text-sm font-medium text-yellow-800"&gt;Loyalty Program Disabled&lt;/h3&gt;
              &lt;div class="mt-2 text-sm text-yellow-700"&gt;
                &lt;p&gt;
                  The loyalty program is currently disabled. Please contact an administrator to enable it.
                &lt;/p&gt;
              &lt;/div&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/main&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, onMounted } from 'vue'
import { useSettingsStore } from '@/store/modules/settings'
import { ExclamationTriangleIcon } from '@heroicons/vue/24/outline'
import LoyaltyProgram from '@/components/loyalty/LoyaltyProgram.vue'
import axios from 'axios'

const settingsStore = useSettingsStore()
const customers = ref([])

const isEnabled = computed(() =&gt; settingsStore.isLoyaltyEnabled)

const fetchCustomers = async () =&gt; {
  if (!isEnabled.value) return
  
  try {
    const response = await axios.get('/api/loyalty/customers')
    customers.value = response.data
  } catch (error) {
    console.error('Error fetching customers:', error)
  }
}

const updateCustomers = async (updatedCustomers: any[]) =&gt; {
  try {
    await axios.put('/api/loyalty/customers', { customers: updatedCustomers })
    customers.value = updatedCustomers
  } catch (error) {
    console.error('Error updating customers:', error)
  }
}

onMounted(() =&gt; {
  fetchCustomers()
})
&lt;/script&gt;
