import { defineStore } from 'pinia'

export type NotificationType = 'success' | 'error' | 'warning' | 'info'

export interface Notification {
  id: string
  type: NotificationType
  message: string
  title?: string
  timeout?: number
}

export const useNotificationStore = defineStore('notification', {
  state: () => ({
    notifications: [] as Notification[]
  }),

  actions: {
    add(notification: Omit<Notification, 'id'>) {
      const id = Math.random().toString(36).substr(2, 9)
      const newNotification = {
        ...notification,
        id,
        timeout: notification.timeout || 5000
      }

      this.notifications.push(newNotification)

      if (newNotification.timeout > 0) {
        setTimeout(() => {
          this.remove(id)
        }, newNotification.timeout)
      }

      return id
    },

    remove(id: string) {
      const index = this.notifications.findIndex(n => n.id === id)
      if (index > -1) {
        this.notifications.splice(index, 1)
      }
    },

    success(message: string, title?: string) {
      return this.add({ type: 'success', message, title })
    },

    error(message: string, title?: string) {
      return this.add({ type: 'error', message, title })
    },

    warning(message: string, title?: string) {
      return this.add({ type: 'warning', message, title })
    },

    info(message: string, title?: string) {
      return this.add({ type: 'info', message, title })
    }
  }
})
