<template>
  <div class="min-h-screen bg-[#14161F]">
    <!-- Hero Section -->
    <div class="relative bg-gray-900 py-16">
      <div class="absolute inset-0">
        <div class="absolute inset-0 bg-gradient-to-br from-[#FF6B00] to-gray-900 opacity-80"></div>
      </div>
      <div class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h1 class="text-4xl font-extrabold tracking-tight text-white sm:text-5xl lg:text-6xl">Contact Us</h1>
        <p class="mt-6 max-w-2xl mx-auto text-xl text-gray-200">
          Get in touch with us for any inquiries or feedback
        </p>
      </div>
    </div>

    <!-- Contact Form Section -->
    <div class="py-16">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="bg-[#1A1C25] rounded-lg shadow-xl p-8 border border-gray-800">
          <form class="space-y-8">
            <!-- Name and Email -->
            <div class="grid grid-cols-1 gap-8">
              <div>
                <label for="name" class="block text-lg font-medium text-gray-200 mb-2">
                  Full Name
                </label>
                <input
                  type="text"
                  id="name"
                  name="name"
                  class="block w-full px-4 py-3 text-lg bg-gray-800 border border-gray-700 rounded-md shadow-sm text-white placeholder-gray-400 focus:ring-[#FF6B00] focus:border-[#FF6B00]"
                  placeholder="Enter your full name"
                />
              </div>
              <div>
                <label for="email" class="block text-lg font-medium text-gray-200 mb-2">
                  Email Address
                </label>
                <input
                  type="email"
                  id="email"
                  name="email"
                  class="block w-full px-4 py-3 text-lg bg-gray-800 border border-gray-700 rounded-md shadow-sm text-white placeholder-gray-400 focus:ring-[#FF6B00] focus:border-[#FF6B00]"
                  placeholder="Enter your email address"
                />
              </div>
            </div>

            <!-- Subject -->
            <div>
              <label for="subject" class="block text-lg font-medium text-gray-200 mb-2">
                Subject
              </label>
              <input
                type="text"
                id="subject"
                name="subject"
                class="block w-full px-4 py-3 text-lg bg-gray-800 border border-gray-700 rounded-md shadow-sm text-white placeholder-gray-400 focus:ring-[#FF6B00] focus:border-[#FF6B00]"
                placeholder="What is your message about?"
              />
            </div>

            <!-- Message -->
            <div>
              <label for="message" class="block text-lg font-medium text-gray-200 mb-2">
                Message
              </label>
              <textarea
                id="message"
                name="message"
                rows="6"
                class="block w-full px-4 py-3 text-lg bg-gray-800 border border-gray-700 rounded-md shadow-sm text-white placeholder-gray-400 focus:ring-[#FF6B00] focus:border-[#FF6B00]"
                placeholder="Type your message here..."
              ></textarea>
            </div>

            <!-- Submit Button -->
            <div>
              <button
                type="submit"
                class="w-full flex justify-center py-4 px-6 border border-transparent rounded-md shadow-sm text-lg font-medium text-white bg-[#FF6B00] hover:bg-[#ff7b1a] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00] transition-colors duration-200"
              >
                Send Message
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'default',
  middleware: false
})
</script>

<style scoped>
input::placeholder,
textarea::placeholder {
  color: #9CA3AF;
}

input:-webkit-autofill,
input:-webkit-autofill:hover,
input:-webkit-autofill:focus,
textarea:-webkit-autofill,
textarea:-webkit-autofill:hover,
textarea:-webkit-autofill:focus {
  -webkit-text-fill-color: white;
  -webkit-box-shadow: 0 0 0px 1000px #1F2937 inset;
  transition: background-color 5000s ease-in-out 0s;
}
</style> 