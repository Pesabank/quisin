import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createTestingPinia } from '@pinia/testing'
import SuperadminDashboard from '@/pages/superadmin/dashboard.vue'

describe('Superadmin Dashboard', () => {
  let wrapper: any
  const mockApiClient = {
    get: vi.fn(),
    post: vi.fn()
  }

  beforeEach(() => {
    vi.resetAllMocks()
    
    mockApiClient.get.mockResolvedValue({
      data: {
        value: {
          stats: {
            totalRestaurants: 10,
            activeRestaurants: 7,
            totalUsers: 50
          },
          restaurants: [
            { 
              id: '1', 
              name: 'Test Restaurant', 
              status: 'ACTIVE', 
              createdAt: '2023-01-01' 
            }
          ]
        }
      },
      error: { value: null }
    })

    wrapper = mount(SuperadminDashboard, {
      global: {
        plugins: [
          createTestingPinia({
            stubActions: false,
            initialState: {
              auth: { 
                user: { role: 'SUPERADMIN' },
                isAuthenticated: true 
              }
            }
          })
        ],
        provide: {
          apiClient: mockApiClient
        },
        stubs: {
          StatCard: true,
          DataTable: true
        }
      }
    })
  })

  it('fetches and displays dashboard statistics', async () => {
    await wrapper.vm.$nextTick()

    expect(wrapper.vm.stats.totalRestaurants).toBe(10)
    expect(wrapper.vm.stats.activeRestaurants).toBe(7)
    expect(wrapper.vm.stats.totalUsers).toBe(50)
  })

  it('renders restaurant data in table', async () => {
    await wrapper.vm.$nextTick()

    expect(wrapper.vm.restaurantData.length).toBe(1)
    expect(wrapper.vm.restaurantData[0].name).toBe('Test Restaurant')
  })

  it('handles restaurant status toggle', async () => {
    mockApiClient.post.mockResolvedValue({
      data: { success: true }
    })

    await wrapper.vm.$nextTick()

    const toggleAction = wrapper.vm.restaurantActions.find(
      (action: any) => action.label === 'Toggle Status'
    )

    await toggleAction.handler({ id: '1' })

    expect(mockApiClient.post).toHaveBeenCalledWith(
      '/superadmin/restaurants/1/toggle-status'
    )
  })

  it('handles logout action', async () => {
    const logoutSpy = vi.spyOn(wrapper.vm.$store.auth, 'logout')
    
    await wrapper.find('button').trigger('click')

    expect(logoutSpy).toHaveBeenCalled()
  })
})
