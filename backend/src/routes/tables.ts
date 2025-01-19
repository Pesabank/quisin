import { Router } from 'express'
import { prisma } from '../db'
import { authenticateToken } from '../middleware/auth'
import { z } from 'zod'

const router = Router()

// Table schema validation
const tableSchema = z.object({
  number: z.number().int().positive(),
  seats: z.number().int().positive(),
  status: z.enum(['available', 'occupied', 'reserved']).optional(),
  location: z.string().optional()
})

// Get all tables
router.get('/', authenticateToken, async (req, res) => {
  try {
    const tables = await prisma.table.findMany({
      orderBy: {
        number: 'asc'
      }
    })
    res.json(tables)
  } catch (error) {
    console.error('Error fetching tables:', error)
    res.status(500).json({ error: 'Failed to fetch tables' })
  }
})

// Get table by ID
router.get('/:id', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params
    const table = await prisma.table.findUnique({
      where: { id }
    })
    
    if (!table) {
      return res.status(404).json({ error: 'Table not found' })
    }
    
    res.json(table)
  } catch (error) {
    console.error('Error fetching table:', error)
    res.status(500).json({ error: 'Failed to fetch table' })
  }
})

// Create new table
router.post('/', authenticateToken, async (req, res) => {
  try {
    const validatedData = tableSchema.parse(req.body)
    
    // Check if table number already exists
    const existingTable = await prisma.table.findFirst({
      where: {
        number: validatedData.number
      }
    })
    
    if (existingTable) {
      return res.status(400).json({ error: 'Table number already exists' })
    }
    
    const table = await prisma.table.create({
      data: {
        ...validatedData,
        status: validatedData.status || 'available'
      }
    })
    
    res.status(201).json(table)
  } catch (error) {
    if (error instanceof z.ZodError) {
      return res.status(400).json({ error: error.errors })
    }
    console.error('Error creating table:', error)
    res.status(500).json({ error: 'Failed to create table' })
  }
})

// Update table
router.patch('/:id', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params
    const validatedData = tableSchema.partial().parse(req.body)
    
    // Check if table exists
    const existingTable = await prisma.table.findUnique({
      where: { id }
    })
    
    if (!existingTable) {
      return res.status(404).json({ error: 'Table not found' })
    }
    
    // Check if new table number conflicts with existing ones
    if (validatedData.number) {
      const conflictingTable = await prisma.table.findFirst({
        where: {
          number: validatedData.number,
          id: { not: id }
        }
      })
      
      if (conflictingTable) {
        return res.status(400).json({ error: 'Table number already exists' })
      }
    }
    
    const table = await prisma.table.update({
      where: { id },
      data: validatedData
    })
    
    res.json(table)
  } catch (error) {
    if (error instanceof z.ZodError) {
      return res.status(400).json({ error: error.errors })
    }
    console.error('Error updating table:', error)
    res.status(500).json({ error: 'Failed to update table' })
  }
})

// Delete table
router.delete('/:id', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params
    
    // Check if table exists
    const existingTable = await prisma.table.findUnique({
      where: { id }
    })
    
    if (!existingTable) {
      return res.status(404).json({ error: 'Table not found' })
    }
    
    // Check if table has active orders
    const activeOrders = await prisma.order.findMany({
      where: {
        tableId: id,
        status: {
          in: ['pending', 'in_progress', 'ready']
        }
      }
    })
    
    if (activeOrders.length > 0) {
      return res.status(400).json({ error: 'Cannot delete table with active orders' })
    }
    
    await prisma.table.delete({
      where: { id }
    })
    
    res.status(204).send()
  } catch (error) {
    console.error('Error deleting table:', error)
    res.status(500).json({ error: 'Failed to delete table' })
  }
})

// Update table status
router.patch('/:id/status', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params
    const { status } = req.body
    
    if (!['available', 'occupied', 'reserved'].includes(status)) {
      return res.status(400).json({ error: 'Invalid status' })
    }
    
    const table = await prisma.table.update({
      where: { id },
      data: { status }
    })
    
    res.json(table)
  } catch (error) {
    console.error('Error updating table status:', error)
    res.status(500).json({ error: 'Failed to update table status' })
  }
})

export default router 