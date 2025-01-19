<template>
  <div class="min-h-screen bg-gray-100 py-6 flex flex-col justify-center sm:py-12">
    <div class="relative py-3 sm:max-w-xl sm:mx-auto">
      <div class="relative px-4 py-10 bg-white shadow-lg sm:rounded-3xl sm:p-20">
        <div class="max-w-md mx-auto">
          <div class="divide-y divide-gray-200">
            <div class="py-8 text-base leading-6 space-y-4 text-gray-700 sm:text-lg sm:leading-7">
              <h2 class="text-2xl font-bold mb-8">Supabase Auth Test</h2>
              
              <!-- Test Registration -->
              <div class="mb-8">
                <h3 class="text-xl font-semibold mb-4">Test Registration</h3>
                <input
                  v-model="registerEmail"
                  type="email"
                  placeholder="Email"
                  class="mb-2 w-full px-3 py-2 border rounded"
                />
                <input
                  v-model="registerPassword"
                  type="password"
                  placeholder="Password"
                  class="mb-2 w-full px-3 py-2 border rounded"
                />
                <button
                  @click="handleRegister"
                  :disabled="loading"
                  class="w-full bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
                >
                  {{ loading ? 'Registering...' : 'Register' }}
                </button>
              </div>

              <!-- Test Login -->
              <div class="mb-8">
                <h3 class="text-xl font-semibold mb-4">Test Login</h3>
                <input
                  v-model="loginEmail"
                  type="email"
                  placeholder="Email"
                  class="mb-2 w-full px-3 py-2 border rounded"
                />
                <input
                  v-model="loginPassword"
                  type="password"
                  placeholder="Password"
                  class="mb-2 w-full px-3 py-2 border rounded"
                />
                <button
                  @click="handleLogin"
                  :disabled="loading"
                  class="w-full bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
                >
                  {{ loading ? 'Logging in...' : 'Login' }}
                </button>
              </div>

              <!-- User Info -->
              <div v-if="user" class="mt-8 p-4 bg-gray-50 rounded">
                <h3 class="text-xl font-semibold mb-4">Current User</h3>
                <pre class="whitespace-pre-wrap">{{ JSON.stringify(user, null, 2) }}</pre>
                <button
                  @click="handleLogout"
                  class="mt-4 w-full bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
                >
                  Logout
                </button>
              </div>

              <!-- Error Display -->
              <div v-if="error" class="mt-4 p-4 bg-red-100 text-red-700 rounded">
                {{ error }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { signUp, signIn, signOut, user, loading, error } = useSupabaseAuth()

const registerEmail = ref('')
const registerPassword = ref('')
const loginEmail = ref('')
const loginPassword = ref('')

const handleRegister = async () => {
  await signUp(registerEmail.value, registerPassword.value)
  registerEmail.value = ''
  registerPassword.value = ''
}

const handleLogin = async () => {
  await signIn(loginEmail.value, loginPassword.value)
  loginEmail.value = ''
  loginPassword.value = ''
}

const handleLogout = async () => {
  await signOut()
}
</script> 