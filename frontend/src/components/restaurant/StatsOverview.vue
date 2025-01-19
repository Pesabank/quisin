&lt;template&gt;
  &lt;div class="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4"&gt;
    &lt;div
      v-for="stat in stats"
      :key="stat.name"
      class="relative bg-white pt-5 px-4 pb-12 sm:pt-6 sm:px-6 shadow rounded-lg overflow-hidden"
    &gt;
      &lt;dt&gt;
        &lt;div class="absolute bg-blue-500 rounded-md p-3"&gt;
          &lt;component
            :is="stat.icon"
            class="h-6 w-6 text-white"
            aria-hidden="true"
          /&gt;
        &lt;/div&gt;
        &lt;p class="ml-16 text-sm font-medium text-gray-500 truncate"&gt;
          {{ stat.name }}
        &lt;/p&gt;
      &lt;/dt&gt;
      &lt;dd class="ml-16 pb-6 flex items-baseline sm:pb-7"&gt;
        &lt;p class="text-2xl font-semibold text-gray-900"&gt;{{ stat.value }}&lt;/p&gt;
        &lt;p
          :class="[
            stat.changeType === 'increase' ? 'text-green-600' : 'text-red-600',
            'ml-2 flex items-baseline text-sm font-semibold'
          ]"
        &gt;
          &lt;component
            :is="stat.changeType === 'increase' ? TrendingUpIcon : TrendingDownIcon"
            class="self-center flex-shrink-0 h-5 w-5"
            aria-hidden="true"
          /&gt;
          &lt;span class="ml-1"&gt;{{ stat.change }}&lt;/span&gt;
          &lt;span class="sr-only"&gt;
            {{ stat.changeType === 'increase' ? 'Increased' : 'Decreased' }} by
          &lt;/span&gt;
        &lt;/p&gt;
        &lt;div class="absolute bottom-0 inset-x-0 bg-gray-50 px-4 py-4 sm:px-6"&gt;
          &lt;div class="text-sm"&gt;
            &lt;a
              :href="stat.href"
              class="font-medium text-blue-600 hover:text-blue-500"
            &gt;
              View all&lt;span class="sr-only"&gt; {{ stat.name }} stats&lt;/span&gt;
            &lt;/a&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/dd&gt;
    &lt;/div&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { defineComponent } from 'vue'

// Icons
const TrendingUpIcon = defineComponent({
  template: `&lt;svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"&gt;
    &lt;path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" /&gt;
  &lt;/svg&gt;`
})

const TrendingDownIcon = defineComponent({
  template: `&lt;svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"&gt;
    &lt;path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 17h8m0 0V9m0 8l-8-8-4 4-6-6" /&gt;
  &lt;/svg&gt;`
})

const OrdersIcon = defineComponent({
  template: `&lt;svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"&gt;
    &lt;path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" /&gt;
  &lt;/svg&gt;`
})

const RevenueIcon = defineComponent({
  template: `&lt;svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"&gt;
    &lt;path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /&gt;
  &lt;/svg&gt;`
})

const CustomersIcon = defineComponent({
  template: `&lt;svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"&gt;
    &lt;path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" /&gt;
  &lt;/svg&gt;`
})

const MenuIcon = defineComponent({
  template: `&lt;svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"&gt;
    &lt;path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" /&gt;
  &lt;/svg&gt;`
})

defineProps&lt;{
  stats: Array&lt;{
    name: string
    value: string | number
    change: string
    changeType: 'increase' | 'decrease'
    icon: any
    href: string
  }&gt;
}&gt;()
&lt;/script&gt;
