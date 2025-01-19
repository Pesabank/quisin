&lt;template&gt;
  &lt;div class="bg-white shadow rounded-lg p-6"&gt;
    &lt;div class="flex justify-between items-center mb-6"&gt;
      &lt;h2 class="text-xl font-semibold text-gray-900"&gt;Restaurant Analytics&lt;/h2&gt;
      &lt;div class="flex space-x-4"&gt;
        &lt;div class="relative"&gt;
          &lt;button
            @click="showExportMenu = !showExportMenu"
            class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700"
          &gt;
            &lt;DownloadIcon class="h-5 w-5 mr-2" /&gt;
            Export Report
          &lt;/button&gt;
          &lt;div
            v-if="showExportMenu"
            class="absolute right-0 mt-2 w-48 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5"
          &gt;
            &lt;div class="py-1" role="menu"&gt;
              &lt;button
                @click="downloadReport('pdf')"
                class="block w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                role="menuitem"
              &gt;
                Download PDF
              &lt;/button&gt;
              &lt;button
                @click="downloadReport('excel')"
                class="block w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                role="menuitem"
              &gt;
                Download Excel
              &lt;/button&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;
        &lt;button
          @click="refreshData"
          class="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
        &gt;
          &lt;RefreshIcon class="h-5 w-5 mr-2" /&gt;
          Refresh
        &lt;/button&gt;
      &lt;/div&gt;
    &lt;/div&gt;

    &lt;div v-if="loading" class="flex justify-center items-center h-64"&gt;
      &lt;div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"&gt;&lt;/div&gt;
    &lt;/div&gt;

    &lt;div v-else-if="error" class="text-center text-red-600 p-4"&gt;
      {{ error }}
    &lt;/div&gt;

    &lt;div v-else&gt;
      &lt;!-- Time Period Selector --&gt;
      &lt;div class="flex space-x-4 mb-6"&gt;
        &lt;button
          v-for="period in ['daily', 'weekly', 'monthly']"
          :key="period"
          @click="selectedPeriod = period"
          :class="[
            'px-4 py-2 text-sm font-medium rounded-md',
            selectedPeriod === period
              ? 'bg-blue-100 text-blue-700'
              : 'text-gray-500 hover:text-gray-700'
          ]"
        &gt;
          {{ period.charAt(0).toUpperCase() + period.slice(1) }}
        &lt;/button&gt;
      &lt;/div&gt;

      &lt;div class="grid grid-cols-1 md:grid-cols-2 gap-6"&gt;
        &lt;!-- Sales Metrics --&gt;
        &lt;div class="bg-gray-50 rounded-lg p-6"&gt;
          &lt;h3 class="text-lg font-medium text-gray-900 mb-4"&gt;Sales Performance&lt;/h3&gt;
          &lt;div class="space-y-4"&gt;
            &lt;div class="flex justify-between"&gt;
              &lt;span class="text-gray-500"&gt;Total Sales&lt;/span&gt;
              &lt;span class="font-medium"&gt;{{ formatCurrency(currentMetrics.sales.totalSales) }}&lt;/span&gt;
            &lt;/div&gt;
            &lt;div class="flex justify-between"&gt;
              &lt;span class="text-gray-500"&gt;Average Order Value&lt;/span&gt;
              &lt;span class="font-medium"&gt;{{ formatCurrency(currentMetrics.sales.averageOrderValue) }}&lt;/span&gt;
            &lt;/div&gt;
            &lt;div class="flex justify-between"&gt;
              &lt;span class="text-gray-500"&gt;Order Count&lt;/span&gt;
              &lt;span class="font-medium"&gt;{{ currentMetrics.sales.orderCount }}&lt;/span&gt;
            &lt;/div&gt;
          &lt;/div&gt;

          &lt;h4 class="text-md font-medium text-gray-900 mt-6 mb-4"&gt;Top Selling Items&lt;/h4&gt;
          &lt;div class="space-y-2"&gt;
            &lt;div
              v-for="item in currentMetrics.sales.topSellingItems.slice(0, 5)"
              :key="item.itemId"
              class="flex justify-between items-center"
            &gt;
              &lt;span class="text-sm text-gray-500"&gt;{{ item.name }}&lt;/span&gt;
              &lt;div class="text-sm"&gt;
                &lt;span class="font-medium"&gt;{{ item.quantity }}x&lt;/span&gt;
                &lt;span class="text-gray-500 ml-2"&gt;{{ formatCurrency(item.revenue) }}&lt;/span&gt;
              &lt;/div&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;

        &lt;!-- Performance Metrics --&gt;
        &lt;div class="bg-gray-50 rounded-lg p-6"&gt;
          &lt;h3 class="text-lg font-medium text-gray-900 mb-4"&gt;Performance Metrics&lt;/h3&gt;
          &lt;div class="space-y-4"&gt;
            &lt;div class="flex justify-between"&gt;
              &lt;span class="text-gray-500"&gt;Avg Preparation Time&lt;/span&gt;
              &lt;span class="font-medium"&gt;{{ formatTime(currentMetrics.performance.averagePreparationTime) }}&lt;/span&gt;
            &lt;/div&gt;
            &lt;div class="flex justify-between"&gt;
              &lt;span class="text-gray-500"&gt;Avg Delivery Time&lt;/span&gt;
              &lt;span class="font-medium"&gt;{{ formatTime(currentMetrics.performance.averageDeliveryTime) }}&lt;/span&gt;
            &lt;/div&gt;
            &lt;div class="flex justify-between"&gt;
              &lt;span class="text-gray-500"&gt;Order Completion Rate&lt;/span&gt;
              &lt;span class="font-medium"&gt;{{ formatPercentage(currentMetrics.performance.orderCompletionRate) }}&lt;/span&gt;
            &lt;/div&gt;
            &lt;div class="flex justify-between"&gt;
              &lt;span class="text-gray-500"&gt;Customer Satisfaction&lt;/span&gt;
              &lt;div class="flex items-center"&gt;
                &lt;span class="font-medium mr-2"&gt;
                  {{ currentMetrics.performance.customerSatisfactionScore.toFixed(1) }}
                &lt;/span&gt;
                &lt;div class="flex"&gt;
                  &lt;StarIcon
                    v-for="i in 5"
                    :key="i"
                    :class="[
                      'h-5 w-5',
                      i &lt;= currentMetrics.performance.customerSatisfactionScore
                        ? 'text-yellow-400'
                        : 'text-gray-300'
                    ]"
                  /&gt;
                &lt;/div&gt;
              &lt;/div&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/div&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, computed, onMounted } from 'vue'
import { DownloadIcon, RefreshIcon, StarIcon } from '@heroicons/vue/solid'
import axios from 'axios'
import { useStore } from 'vuex'

interface Analytics {
  dailyMetrics: {
    sales: SalesMetrics;
    performance: PerformanceMetrics;
  };
  weeklyMetrics: {
    sales: SalesMetrics;
    performance: PerformanceMetrics;
  };
  monthlyMetrics: {
    sales: SalesMetrics;
    performance: PerformanceMetrics;
  };
}

const store = useStore()
const loading = ref(false)
const error = ref('')
const analytics = ref&lt;Analytics | null&gt;(null)
const selectedPeriod = ref('daily')
const showExportMenu = ref(false)

const currentMetrics = computed(() => {
  if (!analytics.value) return null
  return analytics.value[`${selectedPeriod.value}Metrics`]
})

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(value)
}

const formatTime = (minutes: number) => {
  if (minutes &lt; 60) {
    return `${Math.round(minutes)}min`
  }
  const hours = Math.floor(minutes / 60)
  const remainingMinutes = Math.round(minutes % 60)
  return `${hours}h ${remainingMinutes}min`
}

const formatPercentage = (value: number) => {
  return `${value.toFixed(1)}%`
}

const fetchAnalytics = async () => {
  try {
    loading.value = true
    const restaurantId = store.state.currentRestaurant.id
    const response = await axios.get(`/api/analytics/${restaurantId}`)
    analytics.value = response.data
  } catch (err) {
    error.value = 'Failed to fetch analytics data'
    console.error('Error fetching analytics:', err)
  } finally {
    loading.value = false
  }
}

const downloadReport = async (format: 'pdf' | 'excel') => {
  try {
    const restaurantId = store.state.currentRestaurant.id
    const response = await axios.get(`/api/analytics/${restaurantId}/report`, {
      params: { format },
      responseType: 'blob'
    })
    
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `analytics-report.${format === 'pdf' ? 'pdf' : 'xlsx'}`)
    document.body.appendChild(link)
    link.click()
    link.remove()
    showExportMenu.value = false
  } catch (err) {
    console.error('Error downloading report:', err)
  }
}

const refreshData = () => {
  fetchAnalytics()
}

onMounted(() => {
  fetchAnalytics()
  // Auto-refresh every 5 minutes
  setInterval(fetchAnalytics, 5 * 60 * 1000)
})
&lt;/script&gt;
