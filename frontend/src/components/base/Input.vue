&lt;template&gt;
  &lt;div&gt;
    &lt;label v-if="label" :for="id" class="block text-sm font-medium text-gray-700 mb-1"&gt;
      {{ label }}
      &lt;span v-if="required" class="text-red-500"&gt;*&lt;/span&gt;
    &lt;/label&gt;
    &lt;div class="relative"&gt;
      &lt;input
        :id="id"
        :type="type"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :required="required"
        class="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm disabled:bg-gray-100 disabled:cursor-not-allowed"
        :class="{ 'border-red-300': error }"
        @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
      &gt;
      &lt;div v-if="error" class="mt-1 text-sm text-red-600"&gt;{{ error }}&lt;/div&gt;
    &lt;/div&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
interface Props {
  modelValue: string
  label?: string
  type?: string
  placeholder?: string
  disabled?: boolean
  required?: boolean
  error?: string
  id?: string
}

withDefaults(defineProps&lt;Props&gt;(), {
  type: 'text',
  placeholder: '',
  disabled: false,
  required: false,
  id: () => `input-${Math.random().toString(36).substr(2, 9)}`,
})

defineEmits(['update:modelValue'])
&lt;/script&gt;
