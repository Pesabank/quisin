&lt;template&gt;
  &lt;div class="min-h-full"&gt;
    &lt;header class="bg-white shadow"&gt;
      &lt;div class="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8"&gt;
        &lt;h1 class="text-3xl font-bold leading-tight tracking-tight text-gray-900"&gt;Analytics Dashboard&lt;/h1&gt;
      &lt;/div&gt;
    &lt;/header&gt;

    &lt;main&gt;
      &lt;div class="mx-auto max-w-7xl py-6 sm:px-6 lg:px-8"&gt;
        &lt;div class="space-y-6"&gt;
          &lt;DashboardStats :stats="stats" /&gt;
          &lt;RevenueChart :revenue-data="revenueData" /&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/main&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, onMounted } from 'vue'
import DashboardStats from '@/components/analytics/DashboardStats.vue'
import RevenueChart from '@/components/analytics/RevenueChart.vue'
import axios from 'axios'

const stats = ref({
  revenue: { value: 0, change: 0 },
  orders: { value: 0, change: 0 },
  customers: { value: 0, change: 0 },
  avgOrderTime: { value: 0, change: 0 }
})

const revenueData = ref([])

const fetchAnalytics = async () =&gt; {
  try {
    const [statsResponse, revenueResponse] = await Promise.all([
      axios.get('/api/analytics/stats'),
      axios.get('/api/analytics/revenue')
    ])
    
    stats.value = statsResponse.data
    revenueData.value = revenueResponse.data
  } catch (error) {
    console.error('Error fetching analytics:', error)
  }
}

onMounted(() =&gt; {
  fetchAnalytics()
})
&lt;/script&gt;
