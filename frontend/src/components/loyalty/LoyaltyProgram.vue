&lt;template&gt;
  &lt;div class="bg-white shadow sm:rounded-lg"&gt;
    &lt;div class="px-4 py-5 sm:p-6"&gt;
      &lt;div class="sm:flex sm:items-center"&gt;
        &lt;div class="sm:flex-auto"&gt;
          &lt;h3 class="text-lg font-medium leading-6 text-gray-900"&gt;Customer Loyalty Program&lt;/h3&gt;
          &lt;p class="mt-2 text-sm text-gray-500"&gt;Manage customer rewards and points&lt;/p&gt;
        &lt;/div&gt;
        &lt;div class="mt-4 sm:mt-0 sm:ml-16 sm:flex-none"&gt;
          &lt;button
            @click="openCustomerModal"
            class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700"
          &gt;
            Add Customer
          &lt;/button&gt;
        &lt;/div&gt;
      &lt;/div&gt;

      &lt;!-- Search and Filter --&gt;
      &lt;div class="mt-6"&gt;
        &lt;input
          type="text"
          v-model="searchQuery"
          placeholder="Search customers..."
          class="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
        &gt;
      &lt;/div&gt;

      &lt;!-- Customer Table --&gt;
      &lt;div class="mt-6 flex flex-col"&gt;
        &lt;div class="-my-2 -mx-4 overflow-x-auto sm:-mx-6 lg:-mx-8"&gt;
          &lt;div class="inline-block min-w-full py-2 align-middle md:px-6 lg:px-8"&gt;
            &lt;div class="overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg"&gt;
              &lt;table class="min-w-full divide-y divide-gray-300"&gt;
                &lt;thead class="bg-gray-50"&gt;
                  &lt;tr&gt;
                    &lt;th
                      v-for="header in tableHeaders"
                      :key="header.key"
                      class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900"
                    &gt;
                      &lt;div class="flex items-center"&gt;
                        {{ header.label }}
                        &lt;button
                          v-if="header.sortable"
                          @click="sort(header.key)"
                          class="ml-2"
                        &gt;
                          &lt;component
                            :is="getSortIcon(header.key)"
                            class="h-4 w-4 text-gray-400"
                          /&gt;
                        &lt;/button&gt;
                      &lt;/div&gt;
                    &lt;/th&gt;
                    &lt;th class="relative py-3.5 pl-3 pr-4 sm:pr-6"&gt;
                      &lt;span class="sr-only"&gt;Actions&lt;/span&gt;
                    &lt;/th&gt;
                  &lt;/tr&gt;
                &lt;/thead&gt;
                &lt;tbody class="divide-y divide-gray-200 bg-white"&gt;
                  &lt;tr
                    v-for="customer in filteredCustomers"
                    :key="customer.id"
                    class="hover:bg-gray-50"
                  &gt;
                    &lt;td class="whitespace-nowrap px-3 py-4 text-sm"&gt;
                      &lt;div class="flex items-center"&gt;
                        &lt;div class="h-10 w-10 flex-shrink-0"&gt;
                          &lt;span
                            class="inline-flex h-10 w-10 items-center justify-center rounded-full bg-gray-500"
                          &gt;
                            &lt;span class="text-lg font-medium leading-none text-white"&gt;
                              {{ getInitials(customer.name) }}
                            &lt;/span&gt;
                          &lt;/span&gt;
                        &lt;/div&gt;
                        &lt;div class="ml-4"&gt;
                          &lt;div class="font-medium text-gray-900"&gt;{{ customer.name }}&lt;/div&gt;
                          &lt;div class="text-gray-500"&gt;{{ customer.email }}&lt;/div&gt;
                        &lt;/div&gt;
                      &lt;/div&gt;
                    &lt;/td&gt;
                    &lt;td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500"&gt;
                      {{ customer.points }}
                    &lt;/td&gt;
                    &lt;td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500"&gt;
                      {{ customer.tier }}
                    &lt;/td&gt;
                    &lt;td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500"&gt;
                      {{ formatDate(customer.joinDate) }}
                    &lt;/td&gt;
                    &lt;td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500"&gt;
                      {{ formatCurrency(customer.totalSpent) }}
                    &lt;/td&gt;
                    &lt;td class="relative whitespace-nowrap py-4 pl-3 pr-4 text-right text-sm font-medium sm:pr-6"&gt;
                      &lt;button
                        @click="editCustomer(customer)"
                        class="text-blue-600 hover:text-blue-900 mr-4"
                      &gt;
                        Edit
                      &lt;/button&gt;
                      &lt;button
                        @click="addPoints(customer)"
                        class="text-green-600 hover:text-green-900"
                      &gt;
                        Add Points
                      &lt;/button&gt;
                    &lt;/td&gt;
                  &lt;/tr&gt;
                &lt;/tbody&gt;
              &lt;/table&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/div&gt;

    &lt;!-- Customer Modal --&gt;
    &lt;TransitionRoot appear :show="isModalOpen" as="template"&gt;
      &lt;Dialog as="div" @close="closeModal" class="relative z-10"&gt;
        &lt;TransitionChild
          as="template"
          enter="duration-300 ease-out"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="duration-200 ease-in"
          leave-from="opacity-100"
          leave-to="opacity-0"
        &gt;
          &lt;div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" /&gt;
        &lt;/TransitionChild&gt;

        &lt;div class="fixed inset-0 z-10 overflow-y-auto"&gt;
          &lt;div class="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0"&gt;
            &lt;TransitionChild
              as="template"
              enter="duration-300 ease-out"
              enter-from="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
              enter-to="opacity-100 translate-y-0 sm:scale-100"
              leave="duration-200 ease-in"
              leave-from="opacity-100 translate-y-0 sm:scale-100"
              leave-to="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
            &gt;
              &lt;DialogPanel class="relative transform overflow-hidden rounded-lg bg-white px-4 pt-5 pb-4 text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-lg sm:p-6"&gt;
                &lt;form @submit.prevent="saveCustomer"&gt;
                  &lt;div&gt;
                    &lt;div class="mt-3 sm:mt-5"&gt;
                      &lt;DialogTitle as="h3" class="text-lg font-medium leading-6 text-gray-900"&gt;
                        {{ editingCustomer ? 'Edit Customer' : 'Add New Customer' }}
                      &lt;/DialogTitle&gt;
                      
                      &lt;div class="mt-6 grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-6"&gt;
                        &lt;div class="sm:col-span-6"&gt;
                          &lt;label class="block text-sm font-medium text-gray-700"&gt;Name&lt;/label&gt;
                          &lt;input
                            type="text"
                            v-model="customerForm.name"
                            required
                            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
                          &gt;
                        &lt;/div&gt;

                        &lt;div class="sm:col-span-6"&gt;
                          &lt;label class="block text-sm font-medium text-gray-700"&gt;Email&lt;/label&gt;
                          &lt;input
                            type="email"
                            v-model="customerForm.email"
                            required
                            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
                          &gt;
                        &lt;/div&gt;

                        &lt;div class="sm:col-span-3"&gt;
                          &lt;label class="block text-sm font-medium text-gray-700"&gt;Points&lt;/label&gt;
                          &lt;input
                            type="number"
                            v-model.number="customerForm.points"
                            required
                            min="0"
                            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
                          &gt;
                        &lt;/div&gt;

                        &lt;div class="sm:col-span-3"&gt;
                          &lt;label class="block text-sm font-medium text-gray-700"&gt;Tier&lt;/label&gt;
                          &lt;select
                            v-model="customerForm.tier"
                            required
                            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
                          &gt;
                            &lt;option v-for="tier in tiers" :key="tier" :value="tier"&gt;
                              {{ tier }}
                            &lt;/option&gt;
                          &lt;/select&gt;
                        &lt;/div&gt;

                        &lt;div class="sm:col-span-3"&gt;
                          &lt;label class="block text-sm font-medium text-gray-700"&gt;Total Spent&lt;/label&gt;
                          &lt;input
                            type="number"
                            v-model.number="customerForm.totalSpent"
                            required
                            min="0"
                            step="0.01"
                            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
                          &gt;
                        &lt;/div&gt;
                      &lt;/div&gt;
                    &lt;/div&gt;
                  &lt;/div&gt;

                  &lt;div class="mt-5 sm:mt-6 sm:grid sm:grid-cols-2 sm:gap-3 sm:grid-flow-row-dense"&gt;
                    &lt;button
                      type="submit"
                      class="inline-flex w-full justify-center rounded-md border border-transparent bg-blue-600 px-4 py-2 text-base font-medium text-white shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:col-start-2 sm:text-sm"
                    &gt;
                      Save
                    &lt;/button&gt;
                    &lt;button
                      type="button"
                      @click="closeModal"
                      class="mt-3 inline-flex w-full justify-center rounded-md border border-gray-300 bg-white px-4 py-2 text-base font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:col-start-1 sm:mt-0 sm:text-sm"
                    &gt;
                      Cancel
                    &lt;/button&gt;
                  &lt;/div&gt;
                &lt;/form&gt;
              &lt;/DialogPanel&gt;
            &lt;/TransitionChild&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/Dialog&gt;
    &lt;/TransitionRoot&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, computed } from 'vue'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import {
  ChevronUpIcon,
  ChevronDownIcon,
  ChevronUpDownIcon,
} from '@heroicons/vue/24/outline'
import { format } from 'date-fns'

interface Customer {
  id: string
  name: string
  email: string
  points: number
  tier: string
  joinDate: string
  totalSpent: number
}

const props = defineProps&lt;{
  customers: Customer[]
}&gt;()

const emit = defineEmits(['update:customers'])

const searchQuery = ref('')
const sortBy = ref('')
const sortDirection = ref('asc')
const isModalOpen = ref(false)
const editingCustomer = ref&lt;Customer | null&gt;(null)

const customerForm = ref({
  name: '',
  email: '',
  points: 0,
  tier: 'Bronze',
  totalSpent: 0,
})

const tiers = ['Bronze', 'Silver', 'Gold', 'Platinum']

const tableHeaders = [
  { key: 'name', label: 'Customer', sortable: true },
  { key: 'points', label: 'Points', sortable: true },
  { key: 'tier', label: 'Tier', sortable: true },
  { key: 'joinDate', label: 'Join Date', sortable: true },
  { key: 'totalSpent', label: 'Total Spent', sortable: true },
]

const filteredCustomers = computed(() =&gt; {
  let customers = [...props.customers]

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    customers = customers.filter(customer =&gt;
      customer.name.toLowerCase().includes(query) ||
      customer.email.toLowerCase().includes(query)
    )
  }

  if (sortBy.value) {
    customers.sort((a, b) =&gt; {
      const aValue = a[sortBy.value as keyof Customer]
      const bValue = b[sortBy.value as keyof Customer]
      
      if (typeof aValue === 'string' && typeof bValue === 'string') {
        return sortDirection.value === 'asc'
          ? aValue.localeCompare(bValue)
          : bValue.localeCompare(aValue)
      }
      
      return sortDirection.value === 'asc'
        ? (aValue as number) - (bValue as number)
        : (bValue as number) - (aValue as number)
    })
  }

  return customers
})

const formatDate = (date: string) =&gt; {
  return format(new Date(date), 'MMM d, yyyy')
}

const formatCurrency = (amount: number) =&gt; {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(amount)
}

const getInitials = (name: string) =&gt; {
  return name
    .split(' ')
    .map(word =&gt; word[0])
    .join('')
    .toUpperCase()
    .slice(0, 2)
}

const getSortIcon = (key: string) =&gt; {
  if (sortBy.value !== key) return ChevronUpDownIcon
  return sortDirection.value === 'asc' ? ChevronUpIcon : ChevronDownIcon
}

const sort = (key: string) =&gt; {
  if (sortBy.value === key) {
    sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortBy.value = key
    sortDirection.value = 'asc'
  }
}

const openCustomerModal = () =&gt; {
  editingCustomer.value = null
  customerForm.value = {
    name: '',
    email: '',
    points: 0,
    tier: 'Bronze',
    totalSpent: 0,
  }
  isModalOpen.value = true
}

const editCustomer = (customer: Customer) =&gt; {
  editingCustomer.value = customer
  customerForm.value = { ...customer }
  isModalOpen.value = true
}

const closeModal = () =&gt; {
  isModalOpen.value = false
  editingCustomer.value = null
}

const saveCustomer = () =&gt; {
  const newCustomer = {
    id: editingCustomer.value?.id || `new-${Date.now()}`,
    joinDate: editingCustomer.value?.joinDate || new Date().toISOString(),
    ...customerForm.value,
  }

  const customers = [...props.customers]
  const index = customers.findIndex(customer =&gt; customer.id === newCustomer.id)

  if (index !== -1) {
    customers[index] = newCustomer
  } else {
    customers.push(newCustomer)
  }

  emit('update:customers', customers)
  closeModal()
}

const addPoints = (customer: Customer) =&gt; {
  // Implement points addition logic
}
&lt;/script&gt;
