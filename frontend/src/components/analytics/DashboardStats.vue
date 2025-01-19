&lt;template&gt;
  &lt;div class="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3"&gt;
    &lt;div
      v-for="stat in stats"
      :key="stat.name"
      class="relative overflow-hidden rounded-lg bg-white px-4 pb-12 pt-5 shadow sm:px-6 sm:pt-6"
    &gt;
      &lt;dt&gt;
        &lt;div class="absolute rounded-md bg-blue-500 p-3"&gt;
          &lt;component :is="stat.icon" class="h-6 w-6 text-white" aria-hidden="true" /&gt;
        &lt;/div&gt;
        &lt;p class="ml-16 truncate text-sm font-medium text-gray-500"&gt;{{ stat.name }}&lt;/p&gt;
      &lt;/dt&gt;
      &lt;dd class="ml-16 flex items-baseline pb-6 sm:pb-7"&gt;
        &lt;p class="text-2xl font-semibold text-gray-900"&gt;{{ stat.value }}&lt;/p&gt;
        &lt;p
          :class="[
            stat.changeType === 'increase' ? 'text-green-600' : 'text-red-600',
            'ml-2 flex items-baseline text-sm font-semibold'
          ]"
        &gt;
          &lt;component
            :is="stat.changeType === 'increase' ? 'ArrowUpIcon' : 'ArrowDownIcon'"
            class="h-5 w-5 flex-shrink-0 self-center"
            aria-hidden="true"
          /&gt;
          &lt;span class="sr-only"&gt;
            {{ stat.changeType === 'increase' ? 'Increased' : 'Decreased' }} by
          &lt;/span&gt;
          {{ stat.change }}
        &lt;/p&gt;
        &lt;div class="absolute inset-x-0 bottom-0 bg-gray-50 px-4 py-4 sm:px-6"&gt;
          &lt;div class="text-sm"&gt;
            &lt;a href="#" class="font-medium text-blue-600 hover:text-blue-500"&gt;
              View details
              &lt;span class="sr-only"&gt; {{ stat.name }} stats&lt;/span&gt;
            &lt;/a&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/dd&gt;
    &lt;/div&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { computed } from 'vue'
import {
  CurrencyDollarIcon,
  UsersIcon,
  ShoppingBagIcon,
  ClockIcon,
  ChartBarIcon,
  ArrowUpIcon,
  ArrowDownIcon,
} from '@heroicons/vue/24/outline'

interface Stat {
  name: string
  value: string | number
  change: string
  changeType: 'increase' | 'decrease'
  icon: any
}

const props = defineProps&lt;{
  revenue: number
  orders: number
  customers: number
  avgOrderTime: number
  tableTurnover: number
}&gt;()

const stats = computed&lt;Stat[]&gt;(() =&gt; [
  {
    name: 'Revenue',
    value: new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(props.revenue),
    change: '12%',
    changeType: 'increase',
    icon: CurrencyDollarIcon,
  },
  {
    name: 'Orders',
    value: props.orders,
    change: '5.4%',
    changeType: 'increase',
    icon: ShoppingBagIcon,
  },
  {
    name: 'Customers',
    value: props.customers,
    change: '3.2%',
    changeType: 'increase',
    icon: UsersIcon,
  },
  {
    name: 'Average Order Time',
    value: `${props.avgOrderTime} min`,
    change: '4.1%',
    changeType: 'decrease',
    icon: ClockIcon,
  },
  {
    name: 'Table Turnover',
    value: `${props.tableTurnover}/day`,
    change: '8.3%',
    changeType: 'increase',
    icon: ChartBarIcon,
  },
])
&lt;/script&gt;
