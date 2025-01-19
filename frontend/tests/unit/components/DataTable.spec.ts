import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import DataTable from '@/components/dashboard/DataTable.vue'

describe('DataTable Component', () => {
  const columns = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'name', label: 'Name', sortable: true }
  ]

  const data = [
    { id: 1, name: 'John' },
    { id: 2, name: 'Jane' }
  ]

  const actions = [
    {
      label: 'View',
      handler: vi.fn(),
      class: 'test-action'
    }
  ]

  it('renders columns correctly', () => {
    const wrapper = mount(DataTable, {
      props: { columns, data }
    })

    const headerCells = wrapper.findAll('th')
    expect(headerCells.length).toBe(2)
    expect(headerCells[0].text()).toBe('ID')
    expect(headerCells[1].text()).toBe('Name')
  })

  it('renders data rows correctly', () => {
    const wrapper = mount(DataTable, {
      props: { columns, data }
    })

    const rows = wrapper.findAll('tbody tr')
    expect(rows.length).toBe(2)
    expect(rows[0].text()).toContain('1')
    expect(rows[0].text()).toContain('John')
  })

  it('shows empty message when no data', () => {
    const wrapper = mount(DataTable, {
      props: { 
        columns, 
        data: [], 
        emptyMessage: 'No data available' 
      }
    })

    expect(wrapper.text()).toContain('No data available')
  })

  it('renders actions when provided', () => {
    const wrapper = mount(DataTable, {
      props: { columns, data, actions }
    })

    const actionButtons = wrapper.findAll('button')
    expect(actionButtons.length).toBe(2)  // 1 per row
    expect(actionButtons[0].text()).toBe('View')
  })

  it('calls action handler when action button clicked', async () => {
    const wrapper = mount(DataTable, {
      props: { columns, data, actions }
    })

    const actionButton = wrapper.find('button')
    await actionButton.trigger('click')

    expect(actions[0].handler).toHaveBeenCalledWith(data[0])
  })

  it('supports sorting columns', async () => {
    const wrapper = mount(DataTable, {
      props: { columns, data }
    })

    const idHeader = wrapper.findAll('th')[0]
    await idHeader.trigger('click')

    // Additional assertions for sorting behavior
    // This might require more complex implementation
  })
})
