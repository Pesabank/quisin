&lt;template&gt;
  &lt;div class="relative" @keydown.escape="isOpen = false"&gt;
    &lt;div @click="toggle"&gt;
      &lt;slot name="trigger" /&gt;
    &lt;/div&gt;

    &lt;transition
      enter-active-class="transition ease-out duration-100"
      enter-from-class="transform opacity-0 scale-95"
      enter-to-class="transform opacity-100 scale-100"
      leave-active-class="transition ease-in duration-75"
      leave-from-class="transform opacity-100 scale-100"
      leave-to-class="transform opacity-0 scale-95"
    &gt;
      &lt;div
        v-show="isOpen"
        class="absolute right-0 z-10 mt-2 w-48 origin-top-right rounded-md bg-white py-1 shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none"
        role="menu"
        aria-orientation="vertical"
        aria-labelledby="user-menu-button"
        tabindex="-1"
        @click="isOpen = false"
      &gt;
        &lt;slot name="content" /&gt;
      &lt;/div&gt;
    &lt;/transition&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, onMounted, onUnmounted } from 'vue'

const isOpen = ref(false)

const toggle = () => {
  isOpen.value = !isOpen.value
}

// Close dropdown when clicking outside
const handleClickOutside = (event: MouseEvent) => {
  const dropdown = event.target as HTMLElement
  if (!dropdown.closest('.relative')) {
    isOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
&lt;/script&gt;
