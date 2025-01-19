import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import InventoryManager from '../InventoryManager.vue'

describe('InventoryManager', () => {
  const mockItems = [
    { id: 1, name: 'Test Item 1', category: 'Category 1', quantity: 10, price: 999, supplier: 'Supplier 1' },
    { id: 2, name: 'Test Item 2', category: 'Category 2', quantity: 5, price: 1499, supplier: 'Supplier 2' }
  ]

  let wrapper: any

  beforeEach(() => {
    wrapper = mount(InventoryManager, {
      props: {
        items: mockItems,
        categories: ['Category 1', 'Category 2']
      }
    })
  })

  it('renders properly', () => {
    expect(wrapper.exists()).toBe(true)
    expect(wrapper.find('table').exists()).toBe(true)
  })

  it('displays all items', () => {
    const rows = wrapper.findAll('tbody tr')
    expect(rows).toHaveLength(2)
    expect(rows[0].text()).toContain('Test Item 1')
    expect(rows[1].text()).toContain('Test Item 2')
  })

  it('highlights low stock items', () => {
    const lowStockRow = wrapper.findAll('tbody tr')[1]
    expect(lowStockRow.classes()).toContain('low-stock')
  })

  it('filters items by search query', async () => {
    const searchInput = wrapper.find('input[type="search"]')
    await searchInput.setValue('Test Item 1')
    
    const rows = wrapper.findAll('tbody tr')
    expect(rows).toHaveLength(1)
    expect(rows[0].text()).toContain('Test Item 1')
  })

  it('filters items by category', async () => {
    const categorySelect = wrapper.find('select')
    await categorySelect.setValue('Category 1')
    
    const rows = wrapper.findAll('tbody tr')
    expect(rows).toHaveLength(1)
    expect(rows[0].text()).toContain('Test Item 1')
  })

  it('sorts items when clicking column headers', async () => {
    const nameHeader = wrapper.find('th.sortable')
    
    // Items start in ascending order by default
    let rows = wrapper.findAll('tbody tr')
    expect(rows[0].text()).toContain('Test Item 1')
    expect(rows[1].text()).toContain('Test Item 2')
    
    // First click - sort descending
    await nameHeader.trigger('click')
    rows = wrapper.findAll('tbody tr')
    expect(rows[0].text()).toContain('Test Item 2')
    expect(rows[1].text()).toContain('Test Item 1')
    
    // Second click - sort ascending
    await nameHeader.trigger('click')
    rows = wrapper.findAll('tbody tr')
    expect(rows[0].text()).toContain('Test Item 1')
    expect(rows[1].text()).toContain('Test Item 2')
  })

  it('emits update event when editing item', async () => {
    const editButton = wrapper.find('[data-test="edit-button"]')
    await editButton.trigger('click')
    
    const nameInput = wrapper.find('[data-test="edit-name"]')
    await nameInput.setValue('Updated Item')
    
    const saveButton = wrapper.find('[data-test="save-button"]')
    await saveButton.trigger('click')
    
    expect(wrapper.emitted('update')).toBeTruthy()
    expect(wrapper.emitted('update')[0][0]).toEqual(
      expect.objectContaining({
        id: 1,
        name: 'Updated Item'
      })
    )
  })

  describe('Stock Adjustment Modal', () => {
    it('opens stock adjustment modal when clicking adjust stock button', async () => {
      const adjustButton = wrapper.find('button:contains("Adjust Stock")')
      await adjustButton.trigger('click')

      const modal = wrapper.find('[data-test="adjustment-type"]')
      expect(modal.exists()).toBe(true)
    })

    it('validates stock adjustment form', async () => {
      const adjustButton = wrapper.find('button:contains("Adjust Stock")')
      await adjustButton.trigger('click')

      const saveButton = wrapper.find('[data-test="save-adjustment"]')
      expect(saveButton.attributes('disabled')).toBeFalsy()

      // Set invalid quantity
      const quantityInput = wrapper.find('[data-test="adjustment-quantity"]')
      await quantityInput.setValue(-1)
      expect(saveButton.attributes('disabled')).toBeTruthy()

      // Set valid quantity
      await quantityInput.setValue(5)
      expect(saveButton.attributes('disabled')).toBeFalsy()

      // Select 'other' reason without custom reason
      const reasonSelect = wrapper.find('[data-test="adjustment-reason"]')
      await reasonSelect.setValue('other')
      expect(saveButton.attributes('disabled')).toBeTruthy()

      // Add custom reason
      const customReasonInput = wrapper.find('[data-test="adjustment-custom-reason"]')
      await customReasonInput.setValue('Test reason')
      expect(saveButton.attributes('disabled')).toBeFalsy()
    })

    it('emits update event with correct values when saving stock adjustment', async () => {
      const adjustButton = wrapper.find('button:contains("Adjust Stock")')
      await adjustButton.trigger('click')

      // Set adjustment values
      await wrapper.find('[data-test="adjustment-type"]').setValue('add')
      await wrapper.find('[data-test="adjustment-quantity"]').setValue(5)
      await wrapper.find('[data-test="adjustment-reason"]').setValue('restock')
      await wrapper.find('[data-test="adjustment-notes"]').setValue('Test notes')

      // Save adjustment
      await wrapper.find('[data-test="save-adjustment"]').trigger('click')

      // Check emitted event
      const emitted = wrapper.emitted('update')
      expect(emitted).toBeTruthy()
      expect(emitted[0][0]).toEqual({
        ...mockItems[0],
        quantity: 15 // Original 10 + 5
      })
    })

    it('handles stock reduction correctly', async () => {
      const adjustButton = wrapper.find('button:contains("Adjust Stock")')
      await adjustButton.trigger('click')

      // Set adjustment values for reduction
      await wrapper.find('[data-test="adjustment-type"]').setValue('remove')
      await wrapper.find('[data-test="adjustment-quantity"]').setValue(3)
      await wrapper.find('[data-test="adjustment-reason"]').setValue('damage')
      await wrapper.find('[data-test="adjustment-notes"]').setValue('Damaged items')

      // Save adjustment
      await wrapper.find('[data-test="save-adjustment"]').trigger('click')

      // Check emitted event
      const emitted = wrapper.emitted('update')
      expect(emitted).toBeTruthy()
      expect(emitted[0][0]).toEqual({
        ...mockItems[0],
        quantity: 7 // Original 10 - 3
      })
    })
  })

  describe('Edit Form Validation', () => {
    it('shows validation errors for empty required fields', async () => {
      const editButton = wrapper.find('[data-test="edit-button"]')
      await editButton.trigger('click')

      // Clear required fields
      await wrapper.find('[data-test="edit-name"]').setValue('')
      await wrapper.find('[data-test="edit-supplier"]').setValue('')

      // Try to save
      await wrapper.find('[data-test="save-button"]').trigger('click')

      // Check for error messages
      const nameError = wrapper.find('p:contains("Name is required")')
      const supplierError = wrapper.find('p:contains("Supplier is required")')
      expect(nameError.exists()).toBe(true)
      expect(supplierError.exists()).toBe(true)

      // Verify no update event was emitted
      expect(wrapper.emitted('update')).toBeFalsy()
    })

    it('validates price must be positive', async () => {
      const editButton = wrapper.find('[data-test="edit-button"]')
      await editButton.trigger('click')

      // Set negative price
      await wrapper.find('input[type="number"]').setValue(-100)

      // Try to save
      await wrapper.find('[data-test="save-button"]').trigger('click')

      // Check for error message
      const priceError = wrapper.find('p:contains("Price must be a positive number")')
      expect(priceError.exists()).toBe(true)

      // Verify no update event was emitted
      expect(wrapper.emitted('update')).toBeFalsy()
    })

    it('clears validation errors when fixing invalid fields', async () => {
      const editButton = wrapper.find('[data-test="edit-button"]')
      await editButton.trigger('click')

      // Create validation errors
      await wrapper.find('[data-test="edit-name"]').setValue('')
      await wrapper.find('[data-test="save-button"]').trigger('click')

      // Verify error is shown
      let nameError = wrapper.find('p:contains("Name is required")')
      expect(nameError.exists()).toBe(true)

      // Fix the error
      await wrapper.find('[data-test="edit-name"]').setValue('Fixed Name')

      // Verify error is cleared
      nameError = wrapper.find('p:contains("Name is required")')
      expect(nameError.exists()).toBe(false)
    })
  })
})
