&lt;template&gt;
  &lt;div class="bg-white shadow sm:rounded-lg"&gt;
    &lt;div class="px-4 py-5 sm:p-6"&gt;
      &lt;h3 class="text-lg font-medium leading-6 text-gray-900"&gt;Loyalty Program Settings&lt;/h3&gt;
      &lt;div class="mt-2 max-w-xl text-sm text-gray-500"&gt;
        &lt;p&gt;Configure your restaurant's loyalty program settings here.&lt;/p&gt;
      &lt;/div&gt;
      
      &lt;form @submit.prevent="saveSettings" class="mt-5"&gt;
        &lt;div class="space-y-6"&gt;
          &lt;!-- Enable/Disable Toggle --&gt;
          &lt;div class="flex items-center justify-between"&gt;
            &lt;span class="flex-grow flex flex-col"&gt;
              &lt;span class="text-sm font-medium text-gray-900"&gt;Enable Loyalty Program&lt;/span&gt;
              &lt;span class="text-sm text-gray-500"&gt;
                Turn the loyalty program on or off for your restaurant
              &lt;/span&gt;
            &lt;/span&gt;
            &lt;button
              type="button"
              :class="[enabled ? 'bg-blue-600' : 'bg-gray-200']"
              class="relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
              @click="toggleProgram"
            &gt;
              &lt;span
                :class="[enabled ? 'translate-x-5' : 'translate-x-0']"
                class="pointer-events-none relative inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out"
              &gt;
                &lt;span
                  :class="[enabled ? 'opacity-0 duration-100 ease-out' : 'opacity-100 duration-200 ease-in']"
                  class="absolute inset-0 flex h-full w-full items-center justify-center transition-opacity"
                &gt;
                  &lt;XMarkIcon class="h-3 w-3 text-gray-400" /&gt;
                &lt;/span&gt;
                &lt;span
                  :class="[enabled ? 'opacity-100 duration-200 ease-in' : 'opacity-0 duration-100 ease-out']"
                  class="absolute inset-0 flex h-full w-full items-center justify-center transition-opacity"
                &gt;
                  &lt;CheckIcon class="h-3 w-3 text-blue-600" /&gt;
                &lt;/span&gt;
              &lt;/span&gt;
            &lt;/button&gt;
          &lt;/div&gt;

          &lt;!-- Points per Dollar --&gt;
          &lt;div&gt;
            &lt;label class="block text-sm font-medium text-gray-700"&gt;
              Points per Dollar Spent
            &lt;/label&gt;
            &lt;div class="mt-1"&gt;
              &lt;input
                type="number"
                v-model.number="pointsPerDollar"
                min="0"
                step="0.1"
                :disabled="!enabled"
                class="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm disabled:bg-gray-100"
              &gt;
            &lt;/div&gt;
          &lt;/div&gt;

          &lt;!-- Tier Thresholds --&gt;
          &lt;div&gt;
            &lt;label class="block text-sm font-medium text-gray-700"&gt;
              Tier Thresholds (Points)
            &lt;/label&gt;
            &lt;div class="mt-2 space-y-4"&gt;
              &lt;div&gt;
                &lt;label class="block text-sm text-gray-500"&gt;Silver&lt;/label&gt;
                &lt;input
                  type="number"
                  v-model.number="tierThresholds.silver"
                  min="0"
                  :disabled="!enabled"
                  class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm disabled:bg-gray-100"
                &gt;
              &lt;/div&gt;
              &lt;div&gt;
                &lt;label class="block text-sm text-gray-500"&gt;Gold&lt;/label&gt;
                &lt;input
                  type="number"
                  v-model.number="tierThresholds.gold"
                  min="0"
                  :disabled="!enabled"
                  class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm disabled:bg-gray-100"
                &gt;
              &lt;/div&gt;
              &lt;div&gt;
                &lt;label class="block text-sm text-gray-500"&gt;Platinum&lt;/label&gt;
                &lt;input
                  type="number"
                  v-model.number="tierThresholds.platinum"
                  min="0"
                  :disabled="!enabled"
                  class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm disabled:bg-gray-100"
                &gt;
              &lt;/div&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;

        &lt;div class="mt-6 flex justify-end space-x-3"&gt;
          &lt;button
            type="button"
            @click="resetForm"
            class="rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
          &gt;
            Reset
          &lt;/button&gt;
          &lt;button
            type="submit"
            :disabled="!hasChanges"
            class="inline-flex justify-center rounded-md border border-transparent bg-blue-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:bg-gray-400"
          &gt;
            Save Changes
          &lt;/button&gt;
        &lt;/div&gt;
      &lt;/form&gt;

      &lt;!-- Success/Error Messages --&gt;
      &lt;div v-if="message" class="mt-4"&gt;
        &lt;div
          :class="[
            'rounded-md p-4',
            message.type === 'success' ? 'bg-green-50' : 'bg-red-50'
          ]"
        &gt;
          &lt;div class="flex"&gt;
            &lt;div class="flex-shrink-0"&gt;
              &lt;CheckCircleIcon
                v-if="message.type === 'success'"
                class="h-5 w-5 text-green-400"
              /&gt;
              &lt;XCircleIcon
                v-else
                class="h-5 w-5 text-red-400"
              /&gt;
            &lt;/div&gt;
            &lt;div class="ml-3"&gt;
              &lt;p
                :class="[
                  'text-sm',
                  message.type === 'success' ? 'text-green-800' : 'text-red-800'
                ]"
              &gt;
                {{ message.text }}
              &lt;/p&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/div&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, computed, onMounted } from 'vue'
import { useSettingsStore } from '@/store/modules/settings'
import {
  CheckIcon,
  XMarkIcon,
  CheckCircleIcon,
  XCircleIcon,
} from '@heroicons/vue/24/outline'

const settingsStore = useSettingsStore()

interface Message {
  type: 'success' | 'error'
  text: string
}

const enabled = ref(false)
const pointsPerDollar = ref(1)
const tierThresholds = ref({
  silver: 1000,
  gold: 5000,
  platinum: 10000,
})
const message = ref&lt;Message | null&gt;(null)

const originalSettings = ref({
  enabled: false,
  pointsPerDollar: 1,
  tierThresholds: {
    silver: 1000,
    gold: 5000,
    platinum: 10000,
  },
})

const hasChanges = computed(() =&gt; {
  return (
    enabled.value !== originalSettings.value.enabled ||
    pointsPerDollar.value !== originalSettings.value.pointsPerDollar ||
    tierThresholds.value.silver !== originalSettings.value.tierThresholds.silver ||
    tierThresholds.value.gold !== originalSettings.value.tierThresholds.gold ||
    tierThresholds.value.platinum !== originalSettings.value.tierThresholds.platinum
  )
})

onMounted(async () =&gt; {
  await settingsStore.fetchSettings()
  const settings = settingsStore.getLoyaltySettings
  enabled.value = settings.enabled
  pointsPerDollar.value = settings.pointsPerDollar
  tierThresholds.value = { ...settings.tierThresholds }
  originalSettings.value = {
    enabled: settings.enabled,
    pointsPerDollar: settings.pointsPerDollar,
    tierThresholds: { ...settings.tierThresholds },
  }
})

const toggleProgram = () =&gt; {
  enabled.value = !enabled.value
}

const resetForm = () =&gt; {
  enabled.value = originalSettings.value.enabled
  pointsPerDollar.value = originalSettings.value.pointsPerDollar
  tierThresholds.value = { ...originalSettings.value.tierThresholds }
  message.value = null
}

const saveSettings = async () =&gt; {
  try {
    await settingsStore.updateLoyaltySettings({
      enabled: enabled.value,
      pointsPerDollar: pointsPerDollar.value,
      tierThresholds: tierThresholds.value,
    })
    
    originalSettings.value = {
      enabled: enabled.value,
      pointsPerDollar: pointsPerDollar.value,
      tierThresholds: { ...tierThresholds.value },
    }
    
    message.value = {
      type: 'success',
      text: 'Settings saved successfully',
    }
  } catch (error) {
    message.value = {
      type: 'error',
      text: 'Failed to save settings',
    }
  }

  setTimeout(() =&gt; {
    message.value = null
  }, 3000)
}
&lt;/script&gt;
