&lt;template&gt;
  &lt;div class="min-h-full"&gt;
    &lt;header class="bg-white shadow"&gt;
      &lt;div class="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8"&gt;
        &lt;h1 class="text-3xl font-bold leading-tight tracking-tight text-gray-900"&gt;Inventory Management&lt;/h1&gt;
      &lt;/div&gt;
    &lt;/header&gt;

    &lt;main&gt;
      &lt;div class="mx-auto max-w-7xl py-6 sm:px-6 lg:px-8"&gt;
        &lt;div v-if="loading" class="flex justify-center py-12"&gt;
          &lt;div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"&gt;&lt;/div&gt;
        &lt;/div&gt;
        
        &lt;div v-else-if="error" class="rounded-md bg-red-50 p-4 mb-4"&gt;
          &lt;div class="flex"&gt;
            &lt;div class="flex-shrink-0"&gt;
              &lt;XCircleIcon class="h-5 w-5 text-red-400" aria-hidden="true" /&gt;
            &lt;/div&gt;
            &lt;div class="ml-3"&gt;
              &lt;h3 class="text-sm font-medium text-red-800"&gt;Error loading inventory&lt;/h3&gt;
              &lt;div class="mt-2 text-sm text-red-700"&gt;
                &lt;p&gt;{{ error }}&lt;/p&gt;
              &lt;/div&gt;
              &lt;div class="mt-4"&gt;
                &lt;button
                  type="button"
                  class="rounded-md bg-red-50 px-2 py-1.5 text-sm font-medium text-red-800 hover:bg-red-100"
                  @click="fetchInventory"
                &gt;
                  Try again
                &lt;/button&gt;
              &lt;/div&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;
        
        &lt;div v-else&gt;
          &lt;div v-if="successMessage" class="rounded-md bg-green-50 p-4 mb-4"&gt;
            &lt;div class="flex"&gt;
              &lt;div class="flex-shrink-0"&gt;
                &lt;CheckCircleIcon class="h-5 w-5 text-green-400" aria-hidden="true" /&gt;
              &lt;/div&gt;
              &lt;div class="ml-3"&gt;
                &lt;p class="text-sm font-medium text-green-800"&gt;{{ successMessage }}&lt;/p&gt;
              &lt;/div&gt;
            &lt;/div&gt;
          &lt;/div&gt;

          &lt;div class="flex justify-end mb-4"&gt;
            &lt;div class="flex items-center space-x-2"&gt;
              &lt;label class="text-sm text-gray-600"&gt;Items per page:&lt;/label&gt;
              &lt;select
                v-model="itemsPerPage"
                class="rounded-md border-gray-300 py-1.5 text-gray-900 shadow-sm focus:ring-2 focus:ring-blue-500"
                @change="handleItemsPerPageChange"
              &gt;
                &lt;option :value="10"&gt;10&lt;/option&gt;
                &lt;option :value="25"&gt;25&lt;/option&gt;
                &lt;option :value="50"&gt;50&lt;/option&gt;
                &lt;option :value="100"&gt;100&lt;/option&gt;
              &lt;/select&gt;
            &lt;/div&gt;
          &lt;/div&gt;

          &lt;InventoryManager
            :items="paginatedItems"
            :categories="categories"
            @update="handleInventoryUpdate"
          /&gt;

          &lt;!-- Pagination --&gt;
          &lt;div class="mt-6 flex items-center justify-between border-t border-gray-200 bg-white px-4 py-3 sm:px-6"&gt;
            &lt;div class="flex flex-1 justify-between sm:hidden"&gt;
              &lt;button
                @click="previousPage"
                :disabled="currentPage === 1"
                class="relative inline-flex items-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
                :class="{ 'opacity-50 cursor-not-allowed': currentPage === 1 }"
              &gt;
                Previous
              &lt;/button&gt;
              &lt;button
                @click="nextPage"
                :disabled="currentPage === totalPages"
                class="relative ml-3 inline-flex items-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
                :class="{ 'opacity-50 cursor-not-allowed': currentPage === totalPages }"
              &gt;
                Next
              &lt;/button&gt;
            &lt;/div&gt;
            &lt;div class="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between"&gt;
              &lt;div&gt;
                &lt;p class="text-sm text-gray-700"&gt;
                  Showing
                  &lt;span class="font-medium"&gt;{{ startIndex + 1 }}&lt;/span&gt;
                  to
                  &lt;span class="font-medium"&gt;{{ Math.min(endIndex, totalItems) }}&lt;/span&gt;
                  of
                  &lt;span class="font-medium"&gt;{{ totalItems }}&lt;/span&gt;
                  results
                &lt;/p&gt;
              &lt;/div&gt;
              &lt;div&gt;
                &lt;nav class="isolate inline-flex -space-x-px rounded-md shadow-sm" aria-label="Pagination"&gt;
                  &lt;button
                    @click="previousPage"
                    :disabled="currentPage === 1"
                    class="relative inline-flex items-center rounded-l-md px-2 py-2 text-gray-400 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:z-20 focus:outline-offset-0"
                    :class="{ 'opacity-50 cursor-not-allowed': currentPage === 1 }"
                  &gt;
                    &lt;span class="sr-only"&gt;Previous&lt;/span&gt;
                    &lt;ChevronLeftIcon class="h-5 w-5" aria-hidden="true" /&gt;
                  &lt;/button&gt;
                  &lt;button
                    v-for="page in displayedPages"
                    :key="page"
                    @click="goToPage(page)"
                    :class="[
                      page === currentPage
                        ? 'relative z-10 inline-flex items-center bg-blue-600 px-4 py-2 text-sm font-semibold text-white focus:z-20 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600'
                        : 'relative inline-flex items-center px-4 py-2 text-sm font-semibold text-gray-900 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:z-20 focus:outline-offset-0',
                    ]"
                  &gt;
                    {{ page }}
                  &lt;/button&gt;
                  &lt;button
                    @click="nextPage"
                    :disabled="currentPage === totalPages"
                    class="relative inline-flex items-center rounded-r-md px-2 py-2 text-gray-400 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:z-20 focus:outline-offset-0"
                    :class="{ 'opacity-50 cursor-not-allowed': currentPage === totalPages }"
                  &gt;
                    &lt;span class="sr-only"&gt;Next&lt;/span&gt;
                    &lt;ChevronRightIcon class="h-5 w-5" aria-hidden="true" /&gt;
                  &lt;/button&gt;
                &lt;/nav&gt;
              &lt;/div&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/main&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, computed, onMounted } from 'vue'
import { ChevronLeftIcon, ChevronRightIcon, XCircleIcon, CheckCircleIcon } from '@heroicons/vue/20/solid'
import InventoryManager from '@/components/inventory/InventoryManager.vue'
import axios from 'axios'

const inventoryItems = ref([])
const loading = ref(true)
const error = ref<string | null>(null)
const successMessage = ref<string | null>(null)
const currentPage = ref(1)
const itemsPerPage = ref(10)
const categories = ref<string[]>([])

// Pagination logic
const totalItems = computed(() =&gt; inventoryItems.value.length)
const totalPages = computed(() =&gt; Math.ceil(totalItems.value / itemsPerPage.value))
const startIndex = computed(() =&gt; (currentPage.value - 1) * itemsPerPage.value)
const endIndex = computed(() =&gt; startIndex.value + itemsPerPage.value)

const paginatedItems = computed(() =&gt; {
  return inventoryItems.value.slice(startIndex.value, endIndex.value)
})

const displayedPages = computed(() =&gt; {
  const pages = []
  const maxDisplayed = 5
  let start = Math.max(1, currentPage.value - 2)
  let end = Math.min(totalPages.value, start + maxDisplayed - 1)

  if (end - start + 1 < maxDisplayed) {
    start = Math.max(1, end - maxDisplayed + 1)
  }

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

const showSuccessMessage = (message: string) =&gt; {
  successMessage.value = message
  setTimeout(() =&gt; {
    successMessage.value = null
  }, 3000)
}

const fetchInventory = async () =&gt; {
  loading.value = true
  error.value = null
  try {
    const [itemsResponse, categoriesResponse] = await Promise.all([
      axios.get('/api/inventory'),
      axios.get('/api/inventory/categories')
    ])
    inventoryItems.value = itemsResponse.data
    categories.value = categoriesResponse.data
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'An error occurred while fetching inventory'
  } finally {
    loading.value = false
  }
}

const handleInventoryUpdate = async (updatedItem: any) =&gt; {
  loading.value = true
  error.value = null
  try {
    await axios.put(`/api/inventory/${updatedItem.id}`, updatedItem)
    const itemIndex = inventoryItems.value.findIndex(item =&gt; item.id === updatedItem.id)
    if (itemIndex !== -1) {
      inventoryItems.value[itemIndex] = updatedItem
    }
    showSuccessMessage('Inventory updated successfully')
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'An error occurred while updating inventory'
  } finally {
    loading.value = false
  }
}

const handleItemsPerPageChange = () =&gt; {
  currentPage.value = 1
}

const previousPage = () =&gt; {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

const nextPage = () =&gt; {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

const goToPage = (page: number) =&gt; {
  currentPage.value = page
}

onMounted(() =&gt; {
  fetchInventory()
})
&lt;/script&gt;
