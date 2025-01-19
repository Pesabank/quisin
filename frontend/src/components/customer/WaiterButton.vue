&lt;template&gt;
  &lt;div class="fixed bottom-8 right-8"&gt;
    &lt;Transition
      enter-active-class="transform transition duration-200 ease-out"
      enter-from-class="translate-y-8 opacity-0"
      enter-to-class="translate-y-0 opacity-100"
      leave-active-class="transform transition duration-150 ease-in"
      leave-from-class="translate-y-0 opacity-100"
      leave-to-class="translate-y-8 opacity-0"
    &gt;
      &lt;div
        v-if="showSuccessMessage"
        class="absolute bottom-full mb-4 right-0 bg-green-100 text-green-800 px-4 py-2 rounded-lg shadow-lg text-sm"
      &gt;
        Waiter has been called!
      &lt;/div&gt;
    &lt;/Transition&gt;

    &lt;button
      @click="hailWaiter"
      :disabled="!canHail"
      class="relative group bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white rounded-full p-4 shadow-lg transition-all duration-200"
      :class="{ 'animate-pulse': !canHail }"
    &gt;
      &lt;BellIcon class="h-8 w-8" /&gt;

      &lt;!-- Cooldown Timer --&gt;
      &lt;div
        v-if="!canHail"
        class="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full h-6 w-6 flex items-center justify-center"
      &gt;
        {{ formatTime(remainingTime) }}
      &lt;/div&gt;

      &lt;!-- Tooltip --&gt;
      &lt;div
        class="absolute bottom-full mb-2 right-0 bg-gray-900 text-white text-sm px-3 py-1 rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 whitespace-nowrap"
      &gt;
        {{ tooltipText }}
      &lt;/div&gt;
    &lt;/button&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, onMounted, onUnmounted } from 'vue'
import { BellIcon } from '@heroicons/vue/solid'
import { useStore } from 'vuex'
import axios from 'axios'

const store = useStore()
const canHail = ref(true)
const remainingTime = ref(0)
const showSuccessMessage = ref(false)
let statusCheckInterval: number | null = null

const tooltipText = computed(() => {
  if (canHail.value) {
    return 'Call a waiter'
  }
  return `Please wait ${formatTime(remainingTime.value)} before calling again`
})

const formatTime = (seconds: number) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
}

const checkHailStatus = async () => {
  try {
    const restaurantId = store.state.currentRestaurant.id
    const tableId = store.state.currentTable.id
    const response = await axios.get(`/api/waiter/${restaurantId}/${tableId}/status`)
    
    canHail.value = response.data.canHail
    remainingTime.value = response.data.remainingTime || 0

    if (canHail.value && statusCheckInterval) {
      clearInterval(statusCheckInterval)
      statusCheckInterval = null
    }
  } catch (err) {
    console.error('Error checking hail status:', err)
  }
}

const hailWaiter = async () => {
  if (!canHail.value) return

  try {
    const restaurantId = store.state.currentRestaurant.id
    const tableId = store.state.currentTable.id
    
    const response = await axios.post('/api/waiter/hail', {
      restaurantId,
      tableId
    })

    canHail.value = false
    remainingTime.value = response.data.cooldownPeriod
    showSuccessMessage.value = true

    // Start countdown
    statusCheckInterval = setInterval(() => {
      if (remainingTime.value > 0) {
        remainingTime.value--
      } else {
        canHail.value = true
        if (statusCheckInterval) {
          clearInterval(statusCheckInterval)
          statusCheckInterval = null
        }
      }
    }, 1000)

    // Hide success message after 3 seconds
    setTimeout(() => {
      showSuccessMessage.value = false
    }, 3000)
  } catch (err) {
    if (axios.isAxiosError(err) && err.response?.status === 429) {
      remainingTime.value = err.response.data.remainingTime
      canHail.value = false
    }
    console.error('Error hailing waiter:', err)
  }
}

onMounted(() => {
  checkHailStatus()
})

onUnmounted(() => {
  if (statusCheckInterval) {
    clearInterval(statusCheckInterval)
  }
})
&lt;/script&gt;
