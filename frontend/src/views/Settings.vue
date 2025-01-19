&lt;template&gt;
  &lt;div class="min-h-full"&gt;
    &lt;header class="bg-white shadow"&gt;
      &lt;div class="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8"&gt;
        &lt;h1 class="text-3xl font-bold leading-tight tracking-tight text-gray-900"&gt;Settings&lt;/h1&gt;
      &lt;/div&gt;
    &lt;/header&gt;

    &lt;main&gt;
      &lt;div class="mx-auto max-w-7xl py-6 sm:px-6 lg:px-8"&gt;
        &lt;div class="space-y-6"&gt;
          &lt;LoyaltySettings v-if="isAdmin" /&gt;
          &lt;!-- Add other settings components here --&gt;
        &lt;/div&gt;
      &lt;/div&gt;
    &lt;/main&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { useAuthStore } from '@/store/modules/auth'
import LoyaltySettings from '@/components/settings/LoyaltySettings.vue'

const authStore = useAuthStore()
const isAdmin = computed(() =&gt; authStore.isAdmin)
&lt;/script&gt;
