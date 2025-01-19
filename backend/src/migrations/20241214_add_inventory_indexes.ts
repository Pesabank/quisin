import { Db } from 'mongodb'

export const up = async (db: Db) => {
  // Create indexes for frequently queried fields
  await db.collection('inventory').createIndex({ name: 1 })
  await db.collection('inventory').createIndex({ category: 1 })
  await db.collection('inventory').createIndex({ supplier: 1 })
  await db.collection('inventory').createIndex({ quantity: 1 })
  
  // Create compound index for search functionality
  await db.collection('inventory').createIndex({
    name: 'text',
    supplier: 'text',
    category: 'text'
  })

  // Create index for low stock monitoring
  await db.collection('inventory').createIndex({
    quantity: 1,
    minQuantity: 1
  })
}

export const down = async (db: Db) => {
  await db.collection('inventory').dropIndex('name_1')
  await db.collection('inventory').dropIndex('category_1')
  await db.collection('inventory').dropIndex('supplier_1')
  await db.collection('inventory').dropIndex('quantity_1')
  await db.collection('inventory').dropIndex('name_text_supplier_text_category_text')
  await db.collection('inventory').dropIndex('quantity_1_minQuantity_1')
}
