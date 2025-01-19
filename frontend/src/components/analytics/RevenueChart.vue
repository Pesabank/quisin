&lt;template&gt;
  &lt;div class="bg-white rounded-lg shadow"&gt;
    &lt;div class="p-6"&gt;
      &lt;h3 class="text-lg font-medium leading-6 text-gray-900"&gt;Revenue Overview&lt;/h3&gt;
      &lt;div class="mt-2"&gt;
        &lt;div class="flex items-center justify-between"&gt;
          &lt;div class="flex items-center space-x-4"&gt;
            &lt;select
              v-model="selectedPeriod"
              class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-md"
            &gt;
              &lt;option value="daily"&gt;Daily&lt;/option&gt;
              &lt;option value="weekly"&gt;Weekly&lt;/option&gt;
              &lt;option value="monthly"&gt;Monthly&lt;/option&gt;
              &lt;option value="yearly"&gt;Yearly&lt;/option&gt;
            &lt;/select&gt;
            &lt;div class="flex items-center space-x-2"&gt;
              &lt;label class="text-sm text-gray-500"&gt;Compare with previous period&lt;/label&gt;
              &lt;input
                type="checkbox"
                v-model="showComparison"
                class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              &gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/div&gt;
    &lt;div class="p-6"&gt;
      &lt;Line
        :data="chartData"
        :options="chartOptions"
        class="h-72"
      /&gt;
    &lt;/div&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, computed, watch } from 'vue'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js'
import { Line } from 'vue-chartjs'
import { format, subDays, eachDayOfInterval } from 'date-fns'

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
)

const selectedPeriod = ref('daily')
const showComparison = ref(false)

const props = defineProps&lt;{
  revenueData: {
    date: string
    amount: number
  }[]
}&gt;()

const chartData = computed(() =&gt; {
  const currentData = processData(props.revenueData)
  const previousData = showComparison.value ? processData(generatePreviousPeriodData()) : null

  return {
    labels: currentData.labels,
    datasets: [
      {
        label: 'Current Period',
        data: currentData.values,
        borderColor: 'rgb(59, 130, 246)',
        backgroundColor: 'rgba(59, 130, 246, 0.1)',
        fill: true,
        tension: 0.4,
      },
      ...(previousData ? [{
        label: 'Previous Period',
        data: previousData.values,
        borderColor: 'rgb(156, 163, 175)',
        backgroundColor: 'rgba(156, 163, 175, 0.1)',
        fill: true,
        tension: 0.4,
      }] : []),
    ],
  }
})

const chartOptions = computed(() =&gt; ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'top' as const,
    },
    tooltip: {
      mode: 'index' as const,
      intersect: false,
      callbacks: {
        label: (context: any) =&gt; {
          let label = context.dataset.label || ''
          if (label) {
            label += ': '
          }
          label += new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
          }).format(context.parsed.y)
          return label
        },
      },
    },
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        callback: (value: number) =&gt; {
          return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
            notation: 'compact',
          }).format(value)
        },
      },
    },
  },
  interaction: {
    intersect: false,
    mode: 'index' as const,
  },
}))

const processData = (data: typeof props.revenueData) =&gt; {
  const labels: string[] = []
  const values: number[] = []

  switch (selectedPeriod.value) {
    case 'daily':
      data.forEach(item =&gt; {
        labels.push(format(new Date(item.date), 'MMM d'))
        values.push(item.amount)
      })
      break
    case 'weekly':
      // Group by week and calculate sum
      break
    case 'monthly':
      // Group by month and calculate sum
      break
    case 'yearly':
      // Group by year and calculate sum
      break
  }

  return { labels, values }
}

const generatePreviousPeriodData = () =&gt; {
  // Generate mock data for previous period comparison
  return props.revenueData.map(item =&gt; ({
    date: format(subDays(new Date(item.date), getPeriodDays()), 'yyyy-MM-dd'),
    amount: item.amount * 0.9, // Mock data: 90% of current period
  }))
}

const getPeriodDays = () =&gt; {
  switch (selectedPeriod.value) {
    case 'daily':
      return 1
    case 'weekly':
      return 7
    case 'monthly':
      return 30
    case 'yearly':
      return 365
    default:
      return 1
  }
}

watch([selectedPeriod, showComparison], () =&gt; {
  // Trigger data refresh when period or comparison changes
})
&lt;/script&gt;
