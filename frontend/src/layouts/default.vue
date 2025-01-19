<template>
  <div class="min-h-screen bg-[#14161F]">
    <Navbar />
    <main class="pt-24">
      <slot />
    </main>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import Navbar from '../components/layout/Navbar.vue'
import Footer from '../components/layout/Footer.vue'
</script>

<style>
:root {
  --navbar-height: 5rem;
}

main {
  min-height: calc(100vh - var(--navbar-height));
}
</style> 