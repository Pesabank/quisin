&lt;template&gt;
  &lt;div class="space-y-6"&gt;
    &lt;div&gt;
      &lt;label class="block text-sm font-medium text-gray-700"&gt;Restaurant Type&lt;/label&gt;
      &lt;div class="mt-2 space-x-4"&gt;
        &lt;label class="inline-flex items-center"&gt;
          &lt;input
            type="radio"
            v-model="isChain"
            :value="false"
            class="form-radio h-4 w-4 text-blue-600"
          &gt;
          &lt;span class="ml-2"&gt;Single Restaurant&lt;/span&gt;
        &lt;/label&gt;
        &lt;label class="inline-flex items-center"&gt;
          &lt;input
            type="radio"
            v-model="isChain"
            :value="true"
            class="form-radio h-4 w-4 text-blue-600"
          &gt;
          &lt;span class="ml-2"&gt;Chain Restaurant&lt;/span&gt;
        &lt;/label&gt;
      &lt;/div&gt;
    &lt;/div&gt;

    &lt;div v-if="isChain" class="space-y-4"&gt;
      &lt;div&gt;
        &lt;label class="block text-sm font-medium text-gray-700"&gt;Parent Restaurant&lt;/label&gt;
        &lt;select
          v-model="selectedParent"
          class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
          :disabled="loading"
        &gt;
          &lt;option value=""&gt;Select parent restaurant&lt;/option&gt;
          &lt;option v-for="restaurant in parentRestaurants" :key="restaurant._id" :value="restaurant._id"&gt;
            {{ restaurant.name }}
          &lt;/option&gt;
        &lt;/select&gt;
      &lt;/div&gt;

      &lt;div&gt;
        &lt;label class="block text-sm font-medium text-gray-700"&gt;Menu Type&lt;/label&gt;
        &lt;select
          v-model="menuType"
          class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
        &gt;
          &lt;option value="shared"&gt;Shared with Parent&lt;/option&gt;
          &lt;option value="custom"&gt;Custom Menu&lt;/option&gt;
          &lt;option value="independent"&gt;Independent Menu&lt;/option&gt;
        &lt;/select&gt;
      &lt;/div&gt;
    &lt;/div&gt;

    &lt;div&gt;
      &lt;label class="block text-sm font-medium text-gray-700"&gt;Currency&lt;/label&gt;
      &lt;select
        v-model="selectedCurrency"
        class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
      &gt;
        &lt;option v-for="currency in currencies" :key="currency.code" :value="currency"&gt;
          {{ currency.name }} ({{ currency.symbol }})
        &lt;/option&gt;
      &lt;/select&gt;
    &lt;/div&gt;

    &lt;div v-if="error" class="mt-2 text-sm text-red-600"&gt;
      {{ error }}
    &lt;/div&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'

const props = defineProps&lt;{
  modelValue?: {
    isChain: boolean
    chainInfo?: {
      parentRestaurant?: string
      menuType?: 'shared' | 'custom' | 'independent'
    }
    currency?: {
      code: string
      symbol: string
      name: string
    }
  }
}&gt;()

const emit = defineEmits(['update:modelValue'])

const isChain = ref(false)
const selectedParent = ref('')
const menuType = ref('independent')
const selectedCurrency = ref({ code: 'USD', symbol: '$', name: 'US Dollar' })
const parentRestaurants = ref([])
const currencies = ref([])
const loading = ref(false)
const error = ref('')

const fetchParentRestaurants = async () => {
  try {
    loading.value = true
    const response = await axios.get('/api/restaurants', {
      params: { isChain: true }
    })
    parentRestaurants.value = response.data.restaurants
  } catch (err) {
    error.value = 'Failed to fetch parent restaurants'
    console.error('Error fetching parent restaurants:', err)
  } finally {
    loading.value = false
  }
}

const fetchCurrencies = async () => {
  try {
    const response = await axios.get('/api/currencies')
    currencies.value = response.data
  } catch (err) {
    error.value = 'Failed to fetch currencies'
    console.error('Error fetching currencies:', err)
  }
}

watch([isChain, selectedParent, menuType, selectedCurrency], () => {
  emit('update:modelValue', {
    isChain: isChain.value,
    chainInfo: isChain.value ? {
      parentRestaurant: selectedParent.value || undefined,
      menuType: menuType.value
    } : undefined,
    currency: selectedCurrency.value
  })
})

watch(() => props.modelValue, (newValue) => {
  if (newValue) {
    isChain.value = newValue.isChain
    if (newValue.chainInfo) {
      selectedParent.value = newValue.chainInfo.parentRestaurant || ''
      menuType.value = newValue.chainInfo.menuType || 'independent'
    }
    if (newValue.currency) {
      selectedCurrency.value = newValue.currency
    }
  }
}, { deep: true })

onMounted(async () => {
  await Promise.all([
    fetchParentRestaurants(),
    fetchCurrencies()
  ])

  if (props.modelValue) {
    isChain.value = props.modelValue.isChain
    if (props.modelValue.chainInfo) {
      selectedParent.value = props.modelValue.chainInfo.parentRestaurant || ''
      menuType.value = props.modelValue.chainInfo.menuType || 'independent'
    }
    if (props.modelValue.currency) {
      selectedCurrency.value = props.modelValue.currency
    }
  }
})
&lt;/script&gt;
