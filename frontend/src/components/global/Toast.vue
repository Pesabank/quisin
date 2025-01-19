<script setup lang="ts">
import { ref, computed } from 'vue'

interface ToastMessage {
  id: number
  type: 'success' | 'error' | 'warning' | 'info'
  message: string
  duration?: number
}

const messages = ref<ToastMessage[]>([])

const toastClasses = {
  success: 'bg-green-500',
  error: 'bg-red-500',
  warning: 'bg-yellow-500',
  info: 'bg-blue-500'
}

function addMessage(message: Omit<ToastMessage, 'id'>) {
  const id = Date.now()
  const toast: ToastMessage = {
    id,
    ...message,
    duration: message.duration || 5000
  }

  messages.value.push(toast)

  // Automatically remove message after duration
  setTimeout(() => {
    removeMessage(id)
  }, toast.duration)
}

function removeMessage(id: number) {
  messages.value = messages.value.filter(msg => msg.id !== id)
}

// Expose methods to be used globally
defineExpose({
  success: (message: string, duration?: number) => 
    addMessage({ type: 'success', message, duration }),
  error: (message: string, duration?: number) => 
    addMessage({ type: 'error', message, duration }),
  warning: (message: string, duration?: number) => 
    addMessage({ type: 'warning', message, duration }),
  info: (message: string, duration?: number) => 
    addMessage({ type: 'info', message, duration })
})
</script>

<template>
  <div class="fixed top-4 right-4 z-50 space-y-2">
    <transition-group 
      name="toast" 
      tag="div"
    >
      <div 
        v-for="msg in messages" 
        :key="msg.id"
        :class="[
          'px-4 py-2 rounded-lg text-white shadow-lg transform transition-all duration-300',
          toastClasses[msg.type]
        ]"
      >
        {{ msg.message }}
        <button 
          @click="removeMessage(msg.id)"
          class="ml-2 text-white hover:opacity-75"
        >
          ×
        </button>
      </div>
    </transition-group>
  </div>
</template>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

.toast-enter-to,
.toast-leave-from {
  opacity: 1;
  transform: translateX(0);
}
</style>
