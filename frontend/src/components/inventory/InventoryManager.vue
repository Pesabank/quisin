<template>
  <div class="inventory-manager">
    <div class="filters mb-4 flex gap-4">
      <input
        type="search"
        v-model="searchQuery"
        placeholder="Search items..."
        class="px-4 py-2 border rounded-lg"
      />
      <select
        v-model="selectedCategory"
        class="px-4 py-2 border rounded-lg"
      >
        <option value="">All Categories</option>
        <option v-for="category in categories" :key="category" :value="category">
          {{ category }}
        </option>
      </select>
    </div>

    <table class="min-w-full bg-white border rounded-lg">
      <thead>
        <tr>
          <th 
            v-for="header in headers" 
            :key="header.key"
            :class="['px-4 py-2 text-left', { 'sortable cursor-pointer': header.sortable }]"
            @click="header.sortable && sortBy(header.key)"
          >
            {{ header.label }}
          </th>
          <th class="px-4 py-2">Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr 
          v-for="item in filteredAndSortedItems" 
          :key="item.id"
          :class="{ 'low-stock': isLowStock(item) }"
          class="border-t"
        >
          <td class="px-4 py-2">{{ item.name }}</td>
          <td class="px-4 py-2">{{ item.category }}</td>
          <td class="px-4 py-2">{{ item.quantity }} pcs</td>
          <td class="px-4 py-2">${{ (item.price / 100).toFixed(2) }}</td>
          <td class="px-4 py-2">{{ item.supplier }}</td>
          <td class="px-4 py-2 space-x-2">
            <button
              data-test="edit-button"
              class="px-3 py-1 text-sm bg-blue-500 text-white rounded-lg"
              @click="startEdit(item)"
            >
              Edit
            </button>
            <button
              class="px-3 py-1 text-sm bg-gray-500 text-white rounded-lg"
              @click="adjustStock(item)"
            >
              Adjust Stock
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Edit Modal -->
    <div v-if="editingItem" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
      <div class="bg-white p-6 rounded-lg w-96">
        <h3 class="text-lg font-semibold mb-4">Edit Item</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium mb-1">Name</label>
            <input
              data-test="edit-name"
              v-model="editForm.name"
              type="text"
              class="w-full px-3 py-2 border rounded-lg"
              :class="{ 'border-red-500': getFieldError('name') }"
              @input="clearFieldError('name')"
            />
            <p v-if="getFieldError('name')" class="text-red-500 text-xs mt-1">
              {{ getFieldError('name') }}
            </p>
          </div>
          <div>
            <label class="block text-sm font-medium mb-1">Category</label>
            <select
              v-model="editForm.category"
              class="w-full px-3 py-2 border rounded-lg"
              :class="{ 'border-red-500': getFieldError('category') }"
              @input="clearFieldError('category')"
            >
              <option v-for="category in categories" :key="category" :value="category">
                {{ category }}
              </option>
            </select>
            <p v-if="getFieldError('category')" class="text-red-500 text-xs mt-1">
              {{ getFieldError('category') }}
            </p>
          </div>
          <div>
            <label class="block text-sm font-medium mb-1">Price</label>
            <input
              v-model.number="editForm.price"
              type="number"
              class="w-full px-3 py-2 border rounded-lg"
              :class="{ 'border-red-500': getFieldError('price') }"
              @input="clearFieldError('price')"
            />
            <p v-if="getFieldError('price')" class="text-red-500 text-xs mt-1">
              {{ getFieldError('price') }}
            </p>
          </div>
          <div>
            <label class="block text-sm font-medium mb-1">Supplier</label>
            <input
              v-model="editForm.supplier"
              type="text"
              class="w-full px-3 py-2 border rounded-lg"
              :class="{ 'border-red-500': getFieldError('supplier') }"
              @input="clearFieldError('supplier')"
            />
            <p v-if="getFieldError('supplier')" class="text-red-500 text-xs mt-1">
              {{ getFieldError('supplier') }}
            </p>
          </div>
        </div>
        <div class="mt-6 flex justify-end space-x-3">
          <button
            class="px-4 py-2 text-gray-600 bg-gray-100 rounded-lg"
            @click="cancelEdit"
          >
            Cancel
          </button>
          <button
            data-test="save-button"
            class="px-4 py-2 text-white bg-blue-500 rounded-lg"
            @click="saveEdit"
          >
            Save
          </button>
        </div>
      </div>
    </div>

    <!-- Stock Adjustment Modal -->
    <div v-if="adjustingItem" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
      <div class="bg-white p-6 rounded-lg w-96">
        <h3 class="text-lg font-semibold mb-4">Adjust Stock</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium mb-1">Current Stock: {{ adjustingItem.quantity }} pcs</label>
            <div class="flex items-center space-x-4">
              <div class="flex-1">
                <label class="block text-sm font-medium mb-1">Adjustment</label>
                <div class="flex items-center space-x-2">
                  <select
                    v-model="stockAdjustment.type"
                    class="px-3 py-2 border rounded-lg"
                    data-test="adjustment-type"
                  >
                    <option value="add">Add</option>
                    <option value="remove">Remove</option>
                  </select>
                  <input
                    v-model.number="stockAdjustment.quantity"
                    type="number"
                    min="1"
                    class="flex-1 px-3 py-2 border rounded-lg"
                    data-test="adjustment-quantity"
                  />
                </div>
              </div>
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium mb-1">Reason</label>
            <select
              v-model="stockAdjustment.reason"
              class="w-full px-3 py-2 border rounded-lg"
              data-test="adjustment-reason"
            >
              <option value="restock">Restock</option>
              <option value="damage">Damaged/Spoiled</option>
              <option value="correction">Inventory Correction</option>
              <option value="other">Other</option>
            </select>
          </div>
          <div v-if="stockAdjustment.reason === 'other'">
            <label class="block text-sm font-medium mb-1">Specify Reason</label>
            <input
              v-model="stockAdjustment.customReason"
              type="text"
              class="w-full px-3 py-2 border rounded-lg"
              data-test="adjustment-custom-reason"
            />
          </div>
          <div>
            <label class="block text-sm font-medium mb-1">Notes</label>
            <textarea
              v-model="stockAdjustment.notes"
              rows="2"
              class="w-full px-3 py-2 border rounded-lg"
              data-test="adjustment-notes"
            ></textarea>
          </div>
        </div>
        <div class="mt-6 flex justify-end space-x-3">
          <button
            class="px-4 py-2 text-gray-600 bg-gray-100 rounded-lg"
            @click="cancelStockAdjustment"
            data-test="cancel-adjustment"
          >
            Cancel
          </button>
          <button
            class="px-4 py-2 text-white bg-blue-500 rounded-lg"
            @click="saveStockAdjustment"
            :disabled="!isValidAdjustment"
            data-test="save-adjustment"
          >
            Save
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface InventoryItem {
  id: number
  name: string
  category: string
  quantity: number
  price: number
  supplier: string
}

interface StockAdjustment {
  type: 'add' | 'remove'
  quantity: number
  reason: string
  customReason: string
  notes: string
}

interface ValidationError {
  field: string
  message: string
}

const props = defineProps<{
  items: InventoryItem[]
  categories: string[]
}>()

const emit = defineEmits<{
  (e: 'update', item: InventoryItem): void
}>()

const searchQuery = ref('')
const selectedCategory = ref('')
const sortKey = ref('name')
const sortDirection = ref<'asc' | 'desc'>('asc')

const headers = [
  { key: 'name', label: 'Name', sortable: true },
  { key: 'category', label: 'Category', sortable: true },
  { key: 'quantity', label: 'Quantity', sortable: true },
  { key: 'price', label: 'Price', sortable: true },
  { key: 'supplier', label: 'Supplier', sortable: true }
]

const editingItem = ref<InventoryItem | null>(null)
const editForm = ref<Partial<InventoryItem>>({})
const editFormErrors = ref<ValidationError[]>([])

const isValidAdjustment = computed(() => {
  return stockAdjustment.value.quantity > 0 &&
    (stockAdjustment.value.reason !== 'other' || stockAdjustment.value.customReason.trim() !== '')
})

const filteredAndSortedItems = computed(() => {
  let result = [...props.items]

  // Apply search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(item => 
      item.name.toLowerCase().includes(query) ||
      item.category.toLowerCase().includes(query) ||
      item.supplier.toLowerCase().includes(query)
    )
  }

  // Apply category filter
  if (selectedCategory.value) {
    result = result.filter(item => item.category === selectedCategory.value)
  }

  // Apply sorting
  result.sort((a, b) => {
    const aValue = a[sortKey.value as keyof InventoryItem]
    const bValue = b[sortKey.value as keyof InventoryItem]
    
    if (typeof aValue === 'string' && typeof bValue === 'string') {
      return sortDirection.value === 'asc'
        ? aValue.localeCompare(bValue)
        : bValue.localeCompare(aValue)
    }
    
    if (aValue < bValue) return sortDirection.value === 'asc' ? -1 : 1
    if (aValue > bValue) return sortDirection.value === 'asc' ? 1 : -1
    return 0
  })

  return result
})

const sortBy = (key: string) => {
  if (sortKey.value === key) {
    sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortKey.value = key
    sortDirection.value = 'asc'
  }
}

const validateEditForm = (): boolean => {
  const errors: ValidationError[] = []

  if (!editForm.value.name?.trim()) {
    errors.push({ field: 'name', message: 'Name is required' })
  }

  if (!editForm.value.category?.trim()) {
    errors.push({ field: 'category', message: 'Category is required' })
  }

  if (editForm.value.price === undefined || editForm.value.price < 0) {
    errors.push({ field: 'price', message: 'Price must be a positive number' })
  }

  if (!editForm.value.supplier?.trim()) {
    errors.push({ field: 'supplier', message: 'Supplier is required' })
  }

  editFormErrors.value = errors
  return errors.length === 0
}

const getFieldError = (field: string): string => {
  const error = editFormErrors.value.find(err => err.field === field)
  return error ? error.message : ''
}

const startEdit = (item: InventoryItem) => {
  editingItem.value = item
  editForm.value = { ...item }
  editFormErrors.value = []
}

const cancelEdit = () => {
  editingItem.value = null
  editForm.value = {}
  editFormErrors.value = []
}

const saveEdit = () => {
  if (!editingItem.value) return
  
  if (!validateEditForm()) {
    return
  }

  const updatedItem = {
    ...editingItem.value,
    ...editForm.value
  }
  
  emit('update', updatedItem)
  cancelEdit()
}

const adjustStock = (item: InventoryItem) => {
  adjustingItem.value = item
  stockAdjustment.value = {
    type: 'add',
    quantity: 1,
    reason: 'restock',
    customReason: '',
    notes: ''
  }
}

const cancelStockAdjustment = () => {
  adjustingItem.value = null
  stockAdjustment.value = {
    type: 'add',
    quantity: 1,
    reason: 'restock',
    customReason: '',
    notes: ''
  }
}

const saveStockAdjustment = () => {
  if (!adjustingItem.value || !isValidAdjustment.value) return

  const updatedItem = {
    ...adjustingItem.value,
    quantity: adjustingItem.value.quantity + (
      stockAdjustment.value.type === 'add'
        ? stockAdjustment.value.quantity
        : -stockAdjustment.value.quantity
    )
  }

  emit('update', updatedItem)
  cancelStockAdjustment()
}

const isLowStock = (item: InventoryItem) => {
  return item.quantity <= 5
}
</script>

<style scoped>
.low-stock {
  @apply bg-red-50;
}

.sortable:hover {
  @apply bg-gray-50;
}
</style>
