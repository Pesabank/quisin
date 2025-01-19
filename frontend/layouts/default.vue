<template>
  <div class="min-h-screen bg-gray-100">
    <PublicNavbar v-if="!isAuthenticated" />
    <TheNavbar v-else />
    <main>
      <slot />
    </main>
    <TheFooter />
  </div>
</template>

<script setup lang="ts">
import { useAuthState } from '~/composables/useAuthState'

const { isAuthenticated } = useAuthState()
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
}

/* Custom scrollbar */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #FF6B00;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #e66000;
}
</style>
