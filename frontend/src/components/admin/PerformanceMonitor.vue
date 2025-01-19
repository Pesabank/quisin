&lt;template&gt;
  &lt;div class="bg-white shadow rounded-lg p-6"&gt;
    &lt;div class="flex justify-between items-center mb-6"&gt;
      &lt;h2 class="text-xl font-semibold text-gray-900"&gt;System Performance&lt;/h2&gt;
      &lt;button
        @click="refreshMetrics"
        class="inline-flex items-center px-3 py-2 border border-transparent text-sm leading-4 font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
      &gt;
        &lt;RefreshIcon class="h-4 w-4 mr-1" /&gt;
        Refresh
      &lt;/button&gt;
    &lt;/div&gt;

    &lt;div v-if="loading" class="flex justify-center items-center h-64"&gt;
      &lt;div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"&gt;&lt;/div&gt;
    &lt;/div&gt;

    &lt;div v-else-if="error" class="text-center text-red-600 p-4"&gt;
      {{ error }}
    &lt;/div&gt;

    &lt;div v-else class="space-y-6"&gt;
      &lt;!-- Overall Stats --&gt;
      &lt;div class="grid grid-cols-1 md:grid-cols-3 gap-4"&gt;
        &lt;div class="bg-gray-50 rounded-lg p-4"&gt;
          &lt;h3 class="text-sm font-medium text-gray-500"&gt;Total Requests&lt;/h3&gt;
          &lt;p class="mt-2 text-3xl font-semibold text-gray-900"&gt;
            {{ totalRequests }}
          &lt;/p&gt;
        &lt;/div&gt;
        &lt;div class="bg-gray-50 rounded-lg p-4"&gt;
          &lt;h3 class="text-sm font-medium text-gray-500"&gt;Average Response Time&lt;/h3&gt;
          &lt;p class="mt-2 text-3xl font-semibold text-gray-900"&gt;
            {{ formatTime(averageResponseTime) }}
          &lt;/p&gt;
        &lt;/div&gt;
        &lt;div class="bg-gray-50 rounded-lg p-4"&gt;
          &lt;h3 class="text-sm font-medium text-gray-500"&gt;Error Rate&lt;/h3&gt;
          &lt;p class="mt-2 text-3xl font-semibold"
            :class="errorRate > 5 ? 'text-red-600' : 'text-green-600'"&gt;
            {{ errorRate.toFixed(2) }}%
          &lt;/p&gt;
        &lt;/div&gt;
      &lt;/div&gt;

      &lt;!-- Endpoint Performance Table --&gt;
      &lt;div class="mt-8"&gt;
        &lt;h3 class="text-lg font-medium text-gray-900 mb-4"&gt;Endpoint Performance&lt;/h3&gt;
        &lt;div class="overflow-x-auto"&gt;
          &lt;table class="min-w-full divide-y divide-gray-200"&gt;
            &lt;thead&gt;
              &lt;tr&gt;
                &lt;th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"&gt;
                  Endpoint
                &lt;/th&gt;
                &lt;th class="px-6 py-3 bg-gray-50 text-right text-xs font-medium text-gray-500 uppercase tracking-wider"&gt;
                  Requests
                &lt;/th&gt;
                &lt;th class="px-6 py-3 bg-gray-50 text-right text-xs font-medium text-gray-500 uppercase tracking-wider"&gt;
                  Avg Time
                &lt;/th&gt;
                &lt;th class="px-6 py-3 bg-gray-50 text-right text-xs font-medium text-gray-500 uppercase tracking-wider"&gt;
                  P95 Time
                &lt;/th&gt;
                &lt;th class="px-6 py-3 bg-gray-50 text-right text-xs font-medium text-gray-500 uppercase tracking-wider"&gt;
                  Errors
                &lt;/th&gt;
              &lt;/tr&gt;
            &lt;/thead&gt;
            &lt;tbody class="bg-white divide-y divide-gray-200"&gt;
              &lt;tr v-for="(metric, endpoint) in metrics" :key="endpoint"&gt;
                &lt;td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900"&gt;
                  {{ endpoint }}
                &lt;/td&gt;
                &lt;td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-right"&gt;
                  {{ metric.count }}
                &lt;/td&gt;
                &lt;td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-right"&gt;
                  {{ formatTime(metric.avgTime) }}
                &lt;/td&gt;
                &lt;td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-right"&gt;
                  {{ formatTime(metric.p95Time) }}
                &lt;/td&gt;
                &lt;td class="px-6 py-4 whitespace-nowrap text-sm text-right"
                  :class="(metric.errors / metric.count * 100) > 5 ? 'text-red-600' : 'text-green-600'"&gt;
                  {{ ((metric.errors / metric.count) * 100).toFixed(2) }}%
                &lt;/td&gt;
              &lt;/tr&gt;
            &lt;/tbody&gt;
          &lt;/table&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/div&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, onMounted, computed } from 'vue'
import { RefreshIcon } from '@heroicons/vue/outline'
import axios from 'axios'

interface MetricData {
  count: number
  totalTime: number
  avgTime: number
  p95Time: number
  errors: number
}

const metrics = ref&lt;{ [key: string]: MetricData }&gt;({})
const loading = ref(false)
const error = ref('')

const totalRequests = computed(() => {
  return Object.values(metrics.value).reduce((total, metric) => total + metric.count, 0)
})

const averageResponseTime = computed(() => {
  const totalTime = Object.values(metrics.value).reduce((total, metric) => total + metric.totalTime, 0)
  const totalCount = Object.values(metrics.value).reduce((total, metric) => total + metric.count, 0)
  return totalCount ? totalTime / totalCount : 0
})

const errorRate = computed(() => {
  const totalErrors = Object.values(metrics.value).reduce((total, metric) => total + metric.errors, 0)
  const totalRequests = Object.values(metrics.value).reduce((total, metric) => total + metric.count, 0)
  return totalRequests ? (totalErrors / totalRequests) * 100 : 0
})

const formatTime = (ms: number): string => {
  if (ms < 1) return '&lt;1ms'
  if (ms < 1000) return `${Math.round(ms)}ms`
  return `${(ms / 1000).toFixed(2)}s`
}

const fetchMetrics = async () => {
  try {
    loading.value = true
    const response = await axios.get('/api/monitoring/metrics')
    metrics.value = response.data
  } catch (err) {
    error.value = 'Failed to fetch metrics'
    console.error('Error fetching metrics:', err)
  } finally {
    loading.value = false
  }
}

const refreshMetrics = () => {
  fetchMetrics()
}

onMounted(() => {
  fetchMetrics()
  // Auto-refresh every 5 minutes
  setInterval(fetchMetrics, 5 * 60 * 1000)
})
&lt;/script&gt;
