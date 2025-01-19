<script setup lang="ts">
interface Column {
  key: string
  label: string
  sortable?: boolean
  class?: string
}

interface Action {
  label: string
  handler: (item: any) => void
  class?: string
}

interface DataTableProps {
  columns: Column[]
  data: any[]
  actions?: Action[]
  emptyMessage?: string
}

const props = withDefaults(defineProps<DataTableProps>(), {
  emptyMessage: 'No data available',
  actions: () => []
})

const sortKey = ref('')
const sortDirection = ref<'asc' | 'desc'>('asc')

const sortedData = computed(() => {
  if (!sortKey.value) return props.data

  return [...props.data].sort((a, b) => {
    const modifier = sortDirection.value === 'asc' ? 1 : -1
    return a[sortKey.value] > b[sortKey.value] ? modifier : -modifier
  })
})

function toggleSort(column: Column) {
  if (column.sortable) {
    if (sortKey.value === column.key) {
      sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc'
    } else {
      sortKey.value = column.key
      sortDirection.value = 'asc'
    }
  }
}
</script>

<template>
  <div class="bg-white rounded-lg shadow-md">
    <table class="w-full">
      <thead>
        <tr class="bg-gray-100 text-gray-600">
          <th 
            v-for="column in columns" 
            :key="column.key"
            :class="[
              'p-4 text-left font-semibold cursor-pointer',
              column.sortable ? 'hover:bg-gray-200' : '',
              column.class || ''
            ]"
            @click="toggleSort(column)"
          >
            {{ column.label }}
            <span v-if="column.sortable">
              <i 
                v-if="sortKey === column.key" 
                :class="[
                  'ml-2',
                  sortDirection === 'asc' ? 'fas fa-sort-up' : 'fas fa-sort-down'
                ]"
              ></i>
            </span>
          </th>
          <th v-if="actions.length" class="p-4 text-left font-semibold">
            Actions
          </th>
        </tr>
      </thead>
      <tbody>
        <tr 
          v-if="data.length === 0" 
          class="text-center"
        >
          <td :colspan="columns.length + (actions.length ? 1 : 0)" class="p-4 text-gray-500">
            {{ emptyMessage }}
          </td>
        </tr>
        <tr 
          v-for="(item, index) in sortedData" 
          :key="index"
          class="border-b hover:bg-gray-50 transition"
        >
          <td 
            v-for="column in columns" 
            :key="column.key"
            :class="['p-4', column.class || '']"
          >
            {{ item[column.key] }}
          </td>
          <td v-if="actions.length" class="p-4 space-x-2">
            <button 
              v-for="action in actions" 
              :key="action.label"
              @click="action.handler(item)"
              :class="[
                'px-3 py-1 rounded text-white transition',
                action.class || 'bg-[#FF6B00] hover:bg-orange-700'
              ]"
            >
              {{ action.label }}
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.fas {
  color: #888;
}
</style>
