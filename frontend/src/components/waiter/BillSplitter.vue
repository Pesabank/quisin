&lt;template&gt;
  &lt;div class="bg-white shadow sm:rounded-lg"&gt;
    &lt;div class="px-4 py-5 sm:p-6"&gt;
      &lt;h3 class="text-lg font-medium leading-6 text-gray-900"&gt;Split Bill&lt;/h3&gt;
      
      &lt;div class="mt-4 space-y-4"&gt;
        &lt;!-- Split Type Selection --&gt;
        &lt;div&gt;
          &lt;label class="text-sm font-medium text-gray-700"&gt;Split Type&lt;/label&gt;
          &lt;div class="mt-2 space-x-4"&gt;
            &lt;label class="inline-flex items-center"&gt;
              &lt;input
                type="radio"
                v-model="splitType"
                value="equal"
                class="form-radio h-4 w-4 text-blue-600"
              &gt;
              &lt;span class="ml-2 text-sm text-gray-700"&gt;Equal Split&lt;/span&gt;
            &lt;/label&gt;
            &lt;label class="inline-flex items-center"&gt;
              &lt;input
                type="radio"
                v-model="splitType"
                value="custom"
                class="form-radio h-4 w-4 text-blue-600"
              &gt;
              &lt;span class="ml-2 text-sm text-gray-700"&gt;Custom Split&lt;/span&gt;
            &lt;/label&gt;
          &lt;/div&gt;
        &lt;/div&gt;

        &lt;!-- Number of People (Equal Split) --&gt;
        &lt;div v-if="splitType === 'equal'"&gt;
          &lt;label class="text-sm font-medium text-gray-700"&gt;Number of People&lt;/label&gt;
          &lt;input
            type="number"
            v-model.number="numberOfPeople"
            min="2"
            class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
          &gt;
        &lt;/div&gt;

        &lt;!-- Custom Split Items --&gt;
        &lt;div v-if="splitType === 'custom'" class="space-y-4"&gt;
          &lt;div v-for="(item, index) in orderItems" :key="item.id" class="border-b pb-4"&gt;
            &lt;div class="flex justify-between items-start"&gt;
              &lt;div&gt;
                &lt;p class="text-sm font-medium text-gray-900"&gt;{{ item.name }}&lt;/p&gt;
                &lt;p class="text-sm text-gray-500"&gt;
                  {{ item.quantity }}x @ {{ formatPrice(item.price) }}
                &lt;/p&gt;
              &lt;/div&gt;
              &lt;div class="flex items-center space-x-2"&gt;
                &lt;button
                  @click="addSplit(index)"
                  class="inline-flex items-center p-1 border border-transparent rounded-full text-blue-600 hover:bg-blue-100"
                &gt;
                  &lt;svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"&gt;
                    &lt;path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" /&gt;
                  &lt;/svg&gt;
                &lt;/button&gt;
              &lt;/div&gt;
            &lt;/div&gt;
            
            &lt;div class="mt-2 grid grid-cols-2 gap-2"&gt;
              &lt;div
                v-for="(split, splitIndex) in item.splits"
                :key="splitIndex"
                class="flex items-center space-x-2"
              &gt;
                &lt;input
                  type="text"
                  v-model="split.name"
                  placeholder="Name"
                  class="block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                &gt;
                &lt;input
                  type="number"
                  v-model.number="split.percentage"
                  min="0"
                  max="100"
                  class="block w-20 border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                &gt;
                &lt;span class="text-sm text-gray-500"&gt;%&lt;/span&gt;
                &lt;button
                  @click="removeSplit(index, splitIndex)"
                  class="text-red-600 hover:text-red-700"
                &gt;
                  &lt;svg class="h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"&gt;
                    &lt;path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /&gt;
                  &lt;/svg&gt;
                &lt;/button&gt;
              &lt;/div&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;

        &lt;!-- Split Summary --&gt;
        &lt;div class="mt-6 border-t border-gray-200 pt-4"&gt;
          &lt;h4 class="text-sm font-medium text-gray-900"&gt;Split Summary&lt;/h4&gt;
          &lt;div class="mt-2 space-y-2"&gt;
            &lt;template v-if="splitType === 'equal'"&gt;
              &lt;div
                v-for="n in numberOfPeople"
                :key="n"
                class="flex justify-between text-sm"
              &gt;
                &lt;span class="text-gray-500"&gt;Person {{ n }}&lt;/span&gt;
                &lt;span class="font-medium text-gray-900"&gt;{{ formatPrice(equalSplitAmount) }}&lt;/span&gt;
              &lt;/div&gt;
            &lt;/template&gt;
            
            &lt;template v-else&gt;
              &lt;div
                v-for="person in customSplitSummary"
                :key="person.name"
                class="flex justify-between text-sm"
              &gt;
                &lt;span class="text-gray-500"&gt;{{ person.name }}&lt;/span&gt;
                &lt;span class="font-medium text-gray-900"&gt;{{ formatPrice(person.amount) }}&lt;/span&gt;
              &lt;/div&gt;
            &lt;/template&gt;
          &lt;/div&gt;
        &lt;/div&gt;

        &lt;div class="mt-5"&gt;
          &lt;button
            type="button"
            @click="applyBillSplit"
            class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          &gt;
            Apply Split
          &lt;/button&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/div&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, computed } from 'vue'

interface OrderItem {
  id: string
  name: string
  price: number
  quantity: number
  splits: Split[]
}

interface Split {
  name: string
  percentage: number
}

const props = defineProps&lt;{
  orderItems: OrderItem[]
  total: number
}&gt;()

const emit = defineEmits(['split-applied'])

const splitType = ref('equal')
const numberOfPeople = ref(2)

// Initialize splits for each item
props.orderItems.forEach(item =&gt; {
  item.splits = [{ name: '', percentage: 100 }]
})

const equalSplitAmount = computed(() =&gt; {
  return props.total / numberOfPeople.value
})

const customSplitSummary = computed(() =&gt; {
  const summary: { [key: string]: number } = {}

  props.orderItems.forEach(item =&gt; {
    item.splits.forEach(split =&gt; {
      if (split.name) {
        const itemAmount = (item.price * item.quantity * split.percentage) / 100
        summary[split.name] = (summary[split.name] || 0) + itemAmount
      }
    })
  })

  return Object.entries(summary).map(([name, amount]) =&gt; ({
    name,
    amount,
  }))
})

const formatPrice = (price: number) =&gt; {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(price)
}

const addSplit = (itemIndex: number) =&gt; {
  const item = props.orderItems[itemIndex]
  const currentTotal = item.splits.reduce((sum, split) =&gt; sum + split.percentage, 0)
  
  if (currentTotal &lt; 100) {
    item.splits.push({ name: '', percentage: 100 - currentTotal })
  }
}

const removeSplit = (itemIndex: number, splitIndex: number) =&gt; {
  const item = props.orderItems[itemIndex]
  if (item.splits.length &gt; 1) {
    item.splits.splice(splitIndex, 1)
    // Redistribute remaining percentage
    const remaining = 100 - item.splits.reduce((sum, split) =&gt; sum + split.percentage, 0)
    if (remaining &gt; 0) {
      item.splits[0].percentage += remaining
    }
  }
}

const applyBillSplit = () =&gt; {
  if (splitType.value === 'equal') {
    emit('split-applied', Array(numberOfPeople.value).fill(equalSplitAmount.value))
  } else {
    emit('split-applied', customSplitSummary.value)
  }
}
&lt;/script&gt;
