import { mount, VueWrapper } from '@vue/test-utils'
import type { Component } from 'vue'
import { createTestingPinia } from '@pinia/testing'
import { vi } from 'vitest'

export interface MountOptions {
  props?: Record<string, any>
  global?: Record<string, any>
  attachTo?: string | HTMLElement
  slots?: Record<string, any>
}

export const createWrapper = (
  component: Component,
  options: MountOptions = {}
): VueWrapper => {
  return mount(component, {
    global: {
      plugins: [
        createTestingPinia({
          createSpy: vi.fn
        })
      ],
      ...options.global
    },
    props: options.props,
    attachTo: options.attachTo,
    slots: options.slots
  })
}

export const flushPromises = async () => {
  return new Promise(resolve => setTimeout(resolve, 0))
}

export const mockAxiosResponse = (data: any) => {
  return {
    data,
    status: 200,
    statusText: 'OK',
    headers: {},
    config: {}
  }
}

export const mockAxiosError = (status = 400, message = 'Bad Request') => {
  const error = new Error()
  return Object.assign(error, {
    response: {
      status,
      data: { message },
      statusText: message,
      headers: {},
      config: {}
    }
  })
} 