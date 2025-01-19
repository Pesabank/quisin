&lt;template&gt;
  &lt;button
    :class="[
      'inline-flex items-center px-4 py-2 border rounded-md shadow-sm text-sm font-medium focus:outline-none focus:ring-2 focus:ring-offset-2',
      variantClasses[variant],
      sizeClasses[size],
      { 'opacity-50 cursor-not-allowed': disabled }
    ]"
    :disabled="disabled || loading"
    @click="$emit('click')"
  &gt;
    &lt;slot name="icon-left"&gt;&lt;/slot&gt;
    &lt;span :class="{ 'opacity-0': loading }"&gt;
      &lt;slot&gt;&lt;/slot&gt;
    &lt;/span&gt;
    &lt;span v-if="loading" class="absolute"&gt;
      &lt;svg class="animate-spin h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"&gt;
        &lt;circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"&gt;&lt;/circle&gt;
        &lt;path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"&gt;&lt;/path&gt;
      &lt;/svg&gt;
    &lt;/span&gt;
    &lt;slot name="icon-right"&gt;&lt;/slot&gt;
  &lt;/button&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
interface Props {
  variant?: 'primary' | 'secondary' | 'danger' | 'success' | 'warning'
  size?: 'sm' | 'md' | 'lg'
  disabled?: boolean
  loading?: boolean
}

const props = withDefaults(defineProps&lt;Props&gt;(), {
  variant: 'primary',
  size: 'md',
  disabled: false,
  loading: false,
})

const variantClasses = {
  primary: 'bg-blue-600 text-white hover:bg-blue-700 focus:ring-blue-500',
  secondary: 'bg-gray-100 text-gray-700 hover:bg-gray-200 focus:ring-gray-500',
  danger: 'bg-red-600 text-white hover:bg-red-700 focus:ring-red-500',
  success: 'bg-green-600 text-white hover:bg-green-700 focus:ring-green-500',
  warning: 'bg-yellow-600 text-white hover:bg-yellow-700 focus:ring-yellow-500',
}

const sizeClasses = {
  sm: 'text-xs px-2.5 py-1.5',
  md: 'text-sm px-4 py-2',
  lg: 'text-base px-6 py-3',
}

defineEmits(['click'])
&lt;/script&gt;
