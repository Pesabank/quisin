import { ref, computed } from 'vue'

export interface InventoryItem {
  id: string
  name: string
  description?: string
  quantity: number
  unit: string
  minThreshold: number
  cost?: number
  menuItems?: {
    id: string
    name: string
    quantity: number
  }[]
  updatedAt: Date
}

const inventoryItems = ref<InventoryItem[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)

export const useInventory = () => {
  // Fetch inventory items
  const fetchInventoryItems = async () => {
    isLoading.value = true
    error.value = null
    try {
      const response = await fetch('/api/inventory')
      const data = await response.json()
      inventoryItems.value = data
    } catch (err) {
      error.value = 'Failed to fetch inventory items'
      console.error(err)
    } finally {
      isLoading.value = false
    }
  }

  // Add new inventory item
  const addInventoryItem = async (itemData: Omit<InventoryItem, 'id' | 'updatedAt'>) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await fetch('/api/inventory', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(itemData),
      })
      const newItem = await response.json()
      inventoryItems.value.push(newItem)
      return newItem
    } catch (err) {
      error.value = 'Failed to add inventory item'
      console.error(err)
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // Update inventory item
  const updateInventoryItem = async (id: string, itemData: Partial<InventoryItem>) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await fetch(`/api/inventory/${id}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(itemData),
      })
      const updatedItem = await response.json()
      const index = inventoryItems.value.findIndex(item => item.id === id)
      if (index !== -1) {
        inventoryItems.value[index] = updatedItem
      }
      return updatedItem
    } catch (err) {
      error.value = 'Failed to update inventory item'
      console.error(err)
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // Delete inventory item
  const deleteInventoryItem = async (id: string) => {
    isLoading.value = true
    error.value = null
    try {
      await fetch(`/api/inventory/${id}`, {
        method: 'DELETE',
      })
      inventoryItems.value = inventoryItems.value.filter(item => item.id !== id)
    } catch (err) {
      error.value = 'Failed to delete inventory item'
      console.error(err)
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // Adjust stock level
  const adjustStock = async (id: string, adjustment: {
    type: 'add' | 'remove' | 'set'
    amount: number
    reason: string
  }) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await fetch(`/api/inventory/${id}/stock`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(adjustment),
      })
      const updatedItem = await response.json()
      const index = inventoryItems.value.findIndex(item => item.id === id)
      if (index !== -1) {
        inventoryItems.value[index] = updatedItem
      }
      return updatedItem
    } catch (err) {
      error.value = 'Failed to adjust stock level'
      console.error(err)
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // Get low stock items
  const lowStockItems = computed(() => {
    return inventoryItems.value.filter(item => item.quantity <= item.minThreshold)
  })

  // Get total inventory value
  const totalValue = computed(() => {
    return inventoryItems.value.reduce((total, item) => {
      return total + (item.cost || 0) * item.quantity
    }, 0)
  })

  // Get item by ID
  const getItemById = (id: string) => {
    return inventoryItems.value.find(item => item.id === id)
  }

  // Check if item has enough stock for a recipe
  const hasEnoughStock = (itemId: string, requiredQuantity: number) => {
    const item = getItemById(itemId)
    return item ? item.quantity >= requiredQuantity : false
  }

  // Get items by menu item
  const getItemsByMenuItem = (menuItemId: string) => {
    return inventoryItems.value.filter(item => 
      item.menuItems?.some(menuItem => menuItem.id === menuItemId)
    )
  }

  return {
    inventoryItems,
    isLoading,
    error,
    fetchInventoryItems,
    addInventoryItem,
    updateInventoryItem,
    deleteInventoryItem,
    adjustStock,
    lowStockItems,
    totalValue,
    getItemById,
    hasEnoughStock,
    getItemsByMenuItem,
  }
} 