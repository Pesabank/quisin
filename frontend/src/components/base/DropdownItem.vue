&lt;template&gt;
  &lt;button
    type="button"
    class="block w-full px-4 py-2 text-sm text-left text-gray-700 hover:bg-gray-100"
    role="menuitem"
    tabindex="-1"
    @click="$emit('click')"
  &gt;
    &lt;slot /&gt;
  &lt;/button&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
defineEmits(['click'])
&lt;/script&gt;
