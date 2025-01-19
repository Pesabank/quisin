&lt;template&gt;
  &lt;div class="bg-white shadow sm:rounded-lg"&gt;
    &lt;div class="px-4 py-5 sm:p-6"&gt;
      &lt;h3 class="text-lg font-medium leading-6 text-gray-900"&gt;Export Analytics Data&lt;/h3&gt;
      &lt;div class="mt-2 max-w-xl text-sm text-gray-500"&gt;
        &lt;p&gt;Export your analytics data in CSV or JSON format&lt;/p&gt;
      &lt;/div&gt;
      &lt;form @submit.prevent="exportData" class="mt-5"&gt;
        &lt;div class="space-y-4"&gt;
          &lt;div class="grid grid-cols-1 gap-4 sm:grid-cols-2"&gt;
            &lt;div&gt;
              &lt;label class="block text-sm font-medium text-gray-700"&gt;Start Date&lt;/label&gt;
              &lt;input
                type="date"
                v-model="startDate"
                class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
              &gt;
            &lt;/div&gt;
            &lt;div&gt;
              &lt;label class="block text-sm font-medium text-gray-700"&gt;End Date&lt;/label&gt;
              &lt;input
                type="date"
                v-model="endDate"
                class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
              &gt;
            &lt;/div&gt;
          &lt;/div&gt;

          &lt;div&gt;
            &lt;label class="block text-sm font-medium text-gray-700"&gt;Format&lt;/label&gt;
            &lt;select
              v-model="format"
              class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
            &gt;
              &lt;option value="csv"&gt;CSV&lt;/option&gt;
              &lt;option value="json"&gt;JSON&lt;/option&gt;
            &lt;/select&gt;
          &lt;/div&gt;
        &lt;/div&gt;

        &lt;div class="mt-5"&gt;
          &lt;button
            type="submit"
            :disabled="loading"
            class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
          &gt;
            &lt;span v-if="loading" class="mr-2"&gt;
              &lt;svg class="animate-spin h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"&gt;
                &lt;circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"&gt;&lt;/circle&gt;
                &lt;path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"&gt;&lt;/path&gt;
              &lt;/svg&gt;
            &lt;/span&gt;
            {{ loading ? 'Exporting...' : 'Export Data' }}
          &lt;/button&gt;
        &lt;/div&gt;
      &lt;/form&gt;

      &lt;div v-if="error" class="mt-4 rounded-md bg-red-50 p-4"&gt;
        &lt;div class="flex"&gt;
          &lt;div class="flex-shrink-0"&gt;
            &lt;XCircleIcon class="h-5 w-5 text-red-400" aria-hidden="true" /&gt;
          &lt;/div&gt;
          &lt;div class="ml-3"&gt;
            &lt;h3 class="text-sm font-medium text-red-800"&gt;{{ error }}&lt;/h3&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/div&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref } from 'vue'
import { XCircleIcon } from '@heroicons/vue/24/solid'
import axios from 'axios'
import { format } from 'date-fns'

const startDate = ref(format(new Date(Date.now() - 30 * 24 * 60 * 60 * 1000), 'yyyy-MM-dd'))
const endDate = ref(format(new Date(), 'yyyy-MM-dd'))
const format = ref('csv')
const loading = ref(false)
const error = ref('')

const exportData = async () => {
  loading.value = true
  error.value = ''

  try {
    const response = await axios.get('/api/analytics/export', {
      params: {
        startDate: startDate.value,
        endDate: endDate.value,
        format: format.value
      },
      responseType: format.value === 'csv' ? 'blob' : 'json'
    })

    if (format.value === 'csv') {
      // Download CSV file
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', `analytics_${startDate.value}_${endDate.value}.csv`)
      document.body.appendChild(link)
      link.click()
      link.remove()
    } else {
      // Save JSON data
      const jsonStr = JSON.stringify(response.data, null, 2)
      const blob = new Blob([jsonStr], { type: 'application/json' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', `analytics_${startDate.value}_${endDate.value}.json`)
      document.body.appendChild(link)
      link.click()
      link.remove()
    }
  } catch (err) {
    error.value = 'Failed to export data. Please try again.'
    console.error('Export error:', err)
  } finally {
    loading.value = false
  }
}
&lt;/script&gt;
