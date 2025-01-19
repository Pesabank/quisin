import { Db } from 'mongodb'

const sampleInventory = [
  {
    name: 'Tomatoes',
    category: 'Vegetables',
    quantity: 50,
    minQuantity: 20,
    unit: 'kg',
    unitPrice: 2.99,
    supplier: 'Fresh Farms Inc.'
  },
  {
    name: 'Chicken Breast',
    category: 'Meat',
    quantity: 30,
    minQuantity: 15,
    unit: 'kg',
    unitPrice: 8.99,
    supplier: 'Quality Meats Co.'
  },
  {
    name: 'Olive Oil',
    category: 'Pantry',
    quantity: 40,
    minQuantity: 10,
    unit: 'bottles',
    unitPrice: 12.99,
    supplier: 'Mediterranean Imports'
  }
]

export const up = async (db: Db) => {
  await db.collection('inventory').insertMany(
    sampleInventory.map(item => ({
      ...item,
      createdAt: new Date(),
      updatedAt: new Date()
    }))
  )
}

export const down = async (db: Db) => {
  await db.collection('inventory').deleteMany({
    name: { $in: sampleInventory.map(item => item.name) }
  })
}
