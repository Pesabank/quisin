<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
</template>

<script setup lang="ts">
definePageMeta({
  middleware: 'auth'
})
</script>

<style>
html {
  scroll-behavior: smooth;
}

body {
  @apply bg-[#14161F] text-white;
}
</style>
