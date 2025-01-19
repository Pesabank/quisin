import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import StatCard from '@/components/dashboard/StatCard.vue'

describe('StatCard Component', () => {
  it('renders title and value correctly', () => {
    const wrapper = mount(StatCard, {
      props: {
        title: 'Test Stat',
        value: 42,
        icon: 'test-icon'
      }
    })

    expect(wrapper.text()).toContain('Test Stat')
    expect(wrapper.text()).toContain('42')
  })

  it('uses default color when not provided', () => {
    const wrapper = mount(StatCard, {
      props: {
        title: 'Default Color',
        value: 100
      }
    })

    const titleElement = wrapper.find('h2')
    expect(titleElement.attributes('style')).toContain('color: #FF6B00')
  })

  it('uses custom color when provided', () => {
    const wrapper = mount(StatCard, {
      props: {
        title: 'Custom Color',
        value: 200,
        color: '#00FF00'
      }
    })

    const titleElement = wrapper.find('h2')
    expect(titleElement.attributes('style')).toContain('color: #00FF00')
  })

  it('renders icon when provided', () => {
    const wrapper = mount(StatCard, {
      props: {
        title: 'Icon Test',
        value: 123,
        icon: 'chart-line'
      }
    })

    const iconElement = wrapper.find('i')
    expect(iconElement.classes()).toContain('fa-chart-line')
  })
})
