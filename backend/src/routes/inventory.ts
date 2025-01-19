import { Router } from 'express'
import { prisma } from '../db'
import { authenticateToken } from '../middleware/auth'
import { z } from 'zod'

const router = Router()

// Inventory item schema validation
const inventoryItemSchema = z.object({
  name: z.string().min(1),
  description: z.string().optional(),
  quantity: z.number().min(0),
  unit: z.string().min(1),
  minThreshold: z.number().min(0),
  cost: z.number().min(0).optional()
})

// Stock adjustment schema validation
const stockAdjustmentSchema = z.object({
  type: z.enum(['add', 'remove', 'set']),
  amount: z.number().min(0),
  reason: z.string().min(1)
})

// Get all inventory items
router.get('/', authenticateToken, async (req, res) => {
  try {
    const items = await prisma.inventoryItem.findMany({
      where: {
        restaurantId: req.user.restaurantId
      },
      include: {
        menuItems: {
          include: {
            menuItem: {
              select: {
                id: true,
                name: true
              }
            }
          }
        }
      },
      orderBy: {
        name: 'asc'
      }
    })

    // Transform the data to match the frontend interface
    const transformedItems = items.map(item => ({
      ...item,
      menuItems: item.menuItems.map(mi => ({
        id: mi.menuItem.id,
        name: mi.menuItem.name,
        quantity: mi.quantity
      }))
    }))

    res.json(transformedItems)
  } catch (error) {
    console.error('Error fetching inventory items:', error)
    res.status(500).json({ error: 'Failed to fetch inventory items' })
  }
})

// Get inventory item by ID
router.get('/:id', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params
    const item = await prisma.inventoryItem.findUnique({
      where: {
        id,
        restaurantId: req.user.restaurantId
      },
      include: {
        menuItems: {
          include: {
            menuItem: {
              select: {
                id: true,
                name: true
              }
            }
          }
        }
      }
    })

    if (!item) {
      return res.status(404).json({ error: 'Inventory item not found' })
    }

    // Transform the data
    const transformedItem = {
      ...item,
      menuItems: item.menuItems.map(mi => ({
        id: mi.menuItem.id,
        name: mi.menuItem.name,
        quantity: mi.quantity
      }))
    }

    res.json(transformedItem)
  } catch (error) {
    console.error('Error fetching inventory item:', error)
    res.status(500).json({ error: 'Failed to fetch inventory item' })
  }
})

// Create new inventory item
router.post('/', authenticateToken, async (req, res) => {
  try {
    const validatedData = inventoryItemSchema.parse(req.body)

    const item = await prisma.inventoryItem.create({
      data: {
        ...validatedData,
        restaurant: {
          connect: {
            id: req.user.restaurantId
          }
        }
      }
    })

    res.status(201).json(item)
  } catch (error) {
    if (error instanceof z.ZodError) {
      return res.status(400).json({ error: error.errors })
    }
    console.error('Error creating inventory item:', error)
    res.status(500).json({ error: 'Failed to create inventory item' })
  }
})

// Update inventory item
router.patch('/:id', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params
    const validatedData = inventoryItemSchema.partial().parse(req.body)

    // Check if item exists and belongs to the restaurant
    const existingItem = await prisma.inventoryItem.findUnique({
      where: {
        id,
        restaurantId: req.user.restaurantId
      }
    })

    if (!existingItem) {
      return res.status(404).json({ error: 'Inventory item not found' })
    }

    const item = await prisma.inventoryItem.update({
      where: { id },
      data: validatedData
    })

    res.json(item)
  } catch (error) {
    if (error instanceof z.ZodError) {
      return res.status(400).json({ error: error.errors })
    }
    console.error('Error updating inventory item:', error)
    res.status(500).json({ error: 'Failed to update inventory item' })
  }
})

// Delete inventory item
router.delete('/:id', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params

    // Check if item exists and belongs to the restaurant
    const existingItem = await prisma.inventoryItem.findUnique({
      where: {
        id,
        restaurantId: req.user.restaurantId
      },
      include: {
        menuItems: true
      }
    })

    if (!existingItem) {
      return res.status(404).json({ error: 'Inventory item not found' })
    }

    // Check if item is used in any menu items
    if (existingItem.menuItems.length > 0) {
      return res.status(400).json({
        error: 'Cannot delete item that is used in menu items'
      })
    }

    await prisma.inventoryItem.delete({
      where: { id }
    })

    res.status(204).send()
  } catch (error) {
    console.error('Error deleting inventory item:', error)
    res.status(500).json({ error: 'Failed to delete inventory item' })
  }
})

// Adjust stock level
router.post('/:id/stock', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params
    const adjustment = stockAdjustmentSchema.parse(req.body)

    // Check if item exists and belongs to the restaurant
    const existingItem = await prisma.inventoryItem.findUnique({
      where: {
        id,
        restaurantId: req.user.restaurantId
      }
    })

    if (!existingItem) {
      return res.status(404).json({ error: 'Inventory item not found' })
    }

    // Calculate new quantity
    let newQuantity: number
    switch (adjustment.type) {
      case 'add':
        newQuantity = existingItem.quantity + adjustment.amount
        break
      case 'remove':
        newQuantity = existingItem.quantity - adjustment.amount
        if (newQuantity < 0) {
          return res.status(400).json({ error: 'Insufficient stock' })
        }
        break
      case 'set':
        newQuantity = adjustment.amount
        break
    }

    // Update stock level
    const updatedItem = await prisma.inventoryItem.update({
      where: { id },
      data: {
        quantity: newQuantity
      }
    })

    // Create stock adjustment record
    await prisma.stockAdjustment.create({
      data: {
        inventoryItem: {
          connect: { id }
        },
        type: adjustment.type,
        amount: adjustment.amount,
        reason: adjustment.reason,
        previousQuantity: existingItem.quantity,
        newQuantity,
        adjustedBy: {
          connect: { id: req.user.id }
        }
      }
    })

    res.json(updatedItem)
  } catch (error) {
    if (error instanceof z.ZodError) {
      return res.status(400).json({ error: error.errors })
    }
    console.error('Error adjusting stock:', error)
    res.status(500).json({ error: 'Failed to adjust stock' })
  }
})

// Get stock adjustment history
router.get('/:id/stock-history', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params
    const history = await prisma.stockAdjustment.findMany({
      where: {
        inventoryItemId: id,
        inventoryItem: {
          restaurantId: req.user.restaurantId
        }
      },
      include: {
        adjustedBy: {
          select: {
            id: true,
            name: true
          }
        }
      },
      orderBy: {
        createdAt: 'desc'
      }
    })

    res.json(history)
  } catch (error) {
    console.error('Error fetching stock history:', error)
    res.status(500).json({ error: 'Failed to fetch stock history' })
  }
})

export default router 