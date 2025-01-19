&lt;template&gt;
  &lt;div class="bg-white shadow rounded-lg p-6"&gt;
    &lt;div class="flex justify-between items-center mb-6"&gt;
      &lt;h2 class="text-xl font-semibold text-gray-900"&gt;Analytics Alerts&lt;/h2&gt;
      &lt;button
        @click="showAddAlert = true"
        class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700"
      &gt;
        &lt;PlusIcon class="h-5 w-5 mr-2" /&gt;
        Add Alert
      &lt;/button&gt;
    &lt;/div&gt;

    &lt;div class="space-y-4"&gt;
      &lt;div
        v-for="(alert, index) in alerts"
        :key="index"
        class="flex items-center justify-between p-4 bg-gray-50 rounded-lg"
      &gt;
        &lt;div&gt;
          &lt;div class="font-medium text-gray-900"&gt;
            {{ formatMetric(alert.metric) }}
          &lt;/div&gt;
          &lt;div class="text-sm text-gray-500"&gt;
            Alert when {{ alert.condition }} {{ alert.threshold }}
            {{ getMetricUnit(alert.metric) }}
          &lt;/div&gt;
        &lt;/div&gt;
        &lt;button
          @click="removeAlert(index)"
          class="text-red-600 hover:text-red-800"
        &gt;
          &lt;TrashIcon class="h-5 w-5" /&gt;
        &lt;/button&gt;
      &lt;/div&gt;

      &lt;div v-if="alerts.length === 0" class="text-center text-gray-500 py-4"&gt;
        No alerts configured
      &lt;/div&gt;
    &lt;/div&gt;

    &lt;!-- Add Alert Modal --&gt;
    &lt;TransitionRoot appear :show="showAddAlert" as="template"&gt;
      &lt;Dialog as="div" @close="showAddAlert = false" class="relative z-10"&gt;
        &lt;TransitionChild
          enter="ease-out duration-300"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="ease-in duration-200"
          leave-from="opacity-100"
          leave-to="opacity-0"
        &gt;
          &lt;div class="fixed inset-0 bg-black bg-opacity-25" /&gt;
        &lt;/TransitionChild&gt;

        &lt;div class="fixed inset-0 overflow-y-auto"&gt;
          &lt;div class="flex min-h-full items-center justify-center p-4"&gt;
            &lt;TransitionChild
              enter="ease-out duration-300"
              enter-from="opacity-0 scale-95"
              enter-to="opacity-100 scale-100"
              leave="ease-in duration-200"
              leave-from="opacity-100 scale-100"
              leave-to="opacity-0 scale-95"
            &gt;
              &lt;DialogPanel class="w-full max-w-md transform overflow-hidden rounded-2xl bg-white p-6 text-left align-middle shadow-xl transition-all"&gt;
                &lt;DialogTitle as="h3" class="text-lg font-medium leading-6 text-gray-900"&gt;
                  Add Analytics Alert
                &lt;/DialogTitle&gt;

                &lt;div class="mt-4 space-y-4"&gt;
                  &lt;div&gt;
                    &lt;label class="block text-sm font-medium text-gray-700"&gt;
                      Metric
                    &lt;/label&gt;
                    &lt;select
                      v-model="newAlert.metric"
                      class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                    &gt;
                      &lt;option v-for="metric in metrics" :key="metric.value" :value="metric.value"&gt;
                        {{ metric.label }}
                      &lt;/option&gt;
                    &lt;/select&gt;
                  &lt;/div&gt;

                  &lt;div&gt;
                    &lt;label class="block text-sm font-medium text-gray-700"&gt;
                      Condition
                    &lt;/label&gt;
                    &lt;select
                      v-model="newAlert.condition"
                      class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                    &gt;
                      &lt;option value="above"&gt;Above&lt;/option&gt;
                      &lt;option value="below"&gt;Below&lt;/option&gt;
                    &lt;/select&gt;
                  &lt;/div&gt;

                  &lt;div&gt;
                    &lt;label class="block text-sm font-medium text-gray-700"&gt;
                      Threshold
                    &lt;/label&gt;
                    &lt;input
                      type="number"
                      v-model.number="newAlert.threshold"
                      class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                    /&gt;
                  &lt;/div&gt;
                &lt;/div&gt;

                &lt;div class="mt-6 flex justify-end space-x-3"&gt;
                  &lt;button
                    @click="showAddAlert = false"
                    class="inline-flex justify-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
                  &gt;
                    Cancel
                  &lt;/button&gt;
                  &lt;button
                    @click="addAlert"
                    class="inline-flex justify-center rounded-md border border-transparent bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
                  &gt;
                    Add Alert
                  &lt;/button&gt;
                &lt;/div&gt;
              &lt;/DialogPanel&gt;
            &lt;/TransitionChild&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/Dialog&gt;
    &lt;/TransitionRoot&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, onMounted } from 'vue'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import { PlusIcon, TrashIcon } from '@heroicons/vue/outline'
import { useStore } from 'vuex'
import axios from 'axios'

interface Alert {
  metric: string;
  threshold: number;
  condition: 'above' | 'below';
  message: string;
}

const store = useStore()
const alerts = ref&lt;Alert[]&gt;([])
const showAddAlert = ref(false)
const newAlert = ref&lt;Partial&lt;Alert&gt;&gt;({
  metric: '',
  threshold: 0,
  condition: 'below'
})

const metrics = [
  { value: 'totalSales', label: 'Total Sales' },
  { value: 'orderCount', label: 'Order Count' },
  { value: 'customerSatisfaction', label: 'Customer Satisfaction' },
  { value: 'orderCompletionRate', label: 'Order Completion Rate' }
]

const formatMetric = (metric: string) => {
  return metrics.find(m => m.value === metric)?.label || metric
}

const getMetricUnit = (metric: string) => {
  switch (metric) {
    case 'totalSales':
      return '$'
    case 'orderCount':
      return 'orders'
    case 'customerSatisfaction':
      return 'stars'
    case 'orderCompletionRate':
      return '%'
    default:
      return ''
  }
}

const fetchAlerts = async () => {
  try {
    const restaurantId = store.state.currentRestaurant.id
    const response = await axios.get(`/api/analytics/${restaurantId}/alerts`)
    alerts.value = response.data
  } catch (err) {
    console.error('Error fetching alerts:', err)
  }
}

const addAlert = async () => {
  try {
    const restaurantId = store.state.currentRestaurant.id
    const alert: Alert = {
      ...newAlert.value as Alert,
      message: `${formatMetric(newAlert.value.metric!)} is ${newAlert.value.condition} ${newAlert.value.threshold} ${getMetricUnit(newAlert.value.metric!)}`
    }
    
    await axios.post(`/api/analytics/${restaurantId}/alerts`, alert)
    alerts.value.push(alert)
    showAddAlert.value = false
    newAlert.value = {
      metric: '',
      threshold: 0,
      condition: 'below'
    }
  } catch (err) {
    console.error('Error adding alert:', err)
  }
}

const removeAlert = async (index: number) => {
  try {
    const restaurantId = store.state.currentRestaurant.id
    await axios.delete(`/api/analytics/${restaurantId}/alerts/${index}`)
    alerts.value.splice(index, 1)
  } catch (err) {
    console.error('Error removing alert:', err)
  }
}

onMounted(() => {
  fetchAlerts()
})
&lt;/script&gt;
