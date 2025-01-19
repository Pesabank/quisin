&lt;template&gt;
  &lt;button
    @click="toggleSound"
    class="inline-flex items-center px-3 py-2 border border-gray-300 shadow-sm text-sm leading-4 font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
    :title="enabled ? 'Mute Notifications' : 'Unmute Notifications'"
  &gt;
    &lt;VolumeUpIcon v-if="enabled" class="h-5 w-5" /&gt;
    &lt;VolumeOffIcon v-else class="h-5 w-5" /&gt;
  &lt;/button&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, onMounted } from 'vue'
import { VolumeUpIcon, VolumeOffIcon } from '@heroicons/vue/solid'
import { soundService } from '@/services/sound'

const enabled = ref(true)

const toggleSound = () => {
  enabled.value = !enabled.value
  soundService.toggleSound(enabled.value)
}

onMounted(() => {
  enabled.value = soundService.isEnabled()
})
&lt;/script&gt;
