<template>
  <form @submit.prevent="handleSubmit" class="space-y-6">
    <div>
      <label for="email" class="block text-sm font-medium text-gray-700">
        Email address
      </label>
      <input
        id="email"
        v-model="email"
        type="email"
        required
        class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
      />
    </div>

    <div>
      <label for="password" class="block text-sm font-medium text-gray-700">
        Password
      </label>
      <input
        id="password"
        v-model="password"
        type="password"
        required
        class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
      />
    </div>

    <div v-if="error" class="text-red-600 text-sm">
      {{ error }}
    </div>

    <button
      type="submit"
      :disabled="loading"
      class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
    >
      <span v-if="!loading">Sign in</span>
      <span v-else>Loading...</span>
    </button>
  </form>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

const email = ref('');
const password = ref('');
const error = ref('');
const loading = ref(false);

async function handleSubmit() {
  loading.value = true;
  error.value = '';

  try {
    await authStore.login({
      email: email.value,
      password: password.value,
    });

    // Redirect based on user role
    const role = authStore.userRole;
    switch (role) {
      case 'SUPER_ADMIN':
        router.push('/admin/dashboard');
        break;
      case 'RESTAURANT_ADMIN':
        router.push('/restaurant/dashboard');
        break;
      case 'WAITER':
        router.push('/waiter/dashboard');
        break;
      case 'KITCHEN_STAFF':
        router.push('/kitchen/dashboard');
        break;
      default:
        router.push('/dashboard');
    }
  } catch (err) {
    error.value = 'Invalid email or password';
  } finally {
    loading.value = false;
  }
}
</script> 