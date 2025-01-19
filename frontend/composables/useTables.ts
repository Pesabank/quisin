import { ref, computed } from 'vue'

export interface Table {
  id: string
  number: number
  seats: number
  status: 'available' | 'occupied' | 'reserved'
  location?: string
}

const tables = ref<Table[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)

export const useTables = () => {
  // Fetch tables from the API
  const fetchTables = async () => {
    isLoading.value = true
    error.value = null
    try {
      // TODO: Replace with actual API call
      const response = await fetch('/api/tables')
      const data = await response.json()
      tables.value = data
    } catch (err) {
      error.value = 'Failed to fetch tables'
      console.error(err)
    } finally {
      isLoading.value = false
    }
  }

  // Add a new table
  const addTable = async (tableData: Omit<Table, 'id' | 'status'>) => {
    isLoading.value = true
    error.value = null
    try {
      // TODO: Replace with actual API call
      const response = await fetch('/api/tables', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ ...tableData, status: 'available' }),
      })
      const newTable = await response.json()
      tables.value.push(newTable)
      return newTable
    } catch (err) {
      error.value = 'Failed to add table'
      console.error(err)
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // Update a table
  const updateTable = async (id: string, tableData: Partial<Table>) => {
    isLoading.value = true
    error.value = null
    try {
      // TODO: Replace with actual API call
      const response = await fetch(`/api/tables/${id}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(tableData),
      })
      const updatedTable = await response.json()
      const index = tables.value.findIndex(t => t.id === id)
      if (index !== -1) {
        tables.value[index] = updatedTable
      }
      return updatedTable
    } catch (err) {
      error.value = 'Failed to update table'
      console.error(err)
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // Delete a table
  const deleteTable = async (id: string) => {
    isLoading.value = true
    error.value = null
    try {
      // TODO: Replace with actual API call
      await fetch(`/api/tables/${id}`, {
        method: 'DELETE',
      })
      tables.value = tables.value.filter(t => t.id !== id)
    } catch (err) {
      error.value = 'Failed to delete table'
      console.error(err)
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // Update table status
  const updateTableStatus = async (id: string, status: Table['status']) => {
    return updateTable(id, { status })
  }

  // Get table by ID
  const getTableById = (id: string) => {
    return tables.value.find(t => t.id === id)
  }

  // Get tables by status
  const getTablesByStatus = (status: Table['status']) => {
    return tables.value.filter(t => t.status === status)
  }

  // Get available tables
  const availableTables = computed(() => {
    return tables.value.filter(t => t.status === 'available')
  })

  // Get occupied tables
  const occupiedTables = computed(() => {
    return tables.value.filter(t => t.status === 'occupied')
  })

  // Get reserved tables
  const reservedTables = computed(() => {
    return tables.value.filter(t => t.status === 'reserved')
  })

  return {
    tables,
    isLoading,
    error,
    fetchTables,
    addTable,
    updateTable,
    deleteTable,
    updateTableStatus,
    getTableById,
    getTablesByStatus,
    availableTables,
    occupiedTables,
    reservedTables,
  }
} 