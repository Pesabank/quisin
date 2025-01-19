<template>
  <footer class="bg-[#14161F] text-white py-16">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Logo and Navigation -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 mb-12">
        <!-- Logo and About -->
        <div class="space-y-4">
          <div class="flex items-center">
            <div class="w-10 h-10 bg-[#FF6B00] rounded-lg flex items-center justify-center mr-3">
              <span class="text-xl font-bold">Q</span>
            </div>
            <span class="text-xl font-bold">Quisin</span>
          </div>
          <p class="text-gray-400 text-sm">Modern restaurant management system that helps you streamline operations and grow your business.</p>
        </div>

        <!-- Quick Links -->
        <div class="space-y-4">
          <h3 class="font-semibold text-lg">Quick Links</h3>
          <div class="flex flex-col space-y-2">
            <NuxtLink to="/about" class="text-gray-400 hover:text-white transition-colors">About Us</NuxtLink>
            <NuxtLink to="/contact" class="text-gray-400 hover:text-white transition-colors">Contact Us</NuxtLink>
            <NuxtLink to="/support" class="text-gray-400 hover:text-white transition-colors">Customer Support</NuxtLink>
          </div>
        </div>

        <!-- Legal -->
        <div class="space-y-4">
          <h3 class="font-semibold text-lg">Legal</h3>
          <div class="flex flex-col space-y-2">
            <NuxtLink to="/privacy" class="text-gray-400 hover:text-white transition-colors">Privacy Policy</NuxtLink>
            <NuxtLink to="/terms" class="text-gray-400 hover:text-white transition-colors">Terms of Service</NuxtLink>
            <NuxtLink to="/legal" class="text-gray-400 hover:text-white transition-colors">Legal Notice</NuxtLink>
          </div>
        </div>

        <!-- Newsletter -->
        <div class="space-y-4">
          <h3 class="font-semibold text-lg">Newsletter</h3>
          <p class="text-sm text-gray-400">Subscribe to our newsletter for updates and tips.</p>
          <div class="flex flex-col space-y-2">
            <input 
              type="email" 
              placeholder="Enter your email" 
              class="bg-gray-800 text-white px-4 py-2 rounded outline-none text-sm w-full focus:ring-1 focus:ring-[#FF6B00]"
            >
            <button class="bg-[#FF6B00] text-white px-4 py-2 rounded hover:bg-[#ff7b1a] transition-colors text-sm w-full">
              Subscribe
            </button>
          </div>
        </div>
      </div>

      <!-- Social Links -->
      <div class="border-t border-gray-800 pt-8">
        <div class="flex flex-col md:flex-row justify-between items-center space-y-4 md:space-y-0">
          <!-- Copyright -->
          <div class="text-sm text-gray-500">
            © {{ new Date().getFullYear() }} Quisin Inc. All rights reserved.
          </div>

          <!-- Social Icons -->
          <div class="flex space-x-6">
            <a href="#" class="text-gray-400 hover:text-white transition-colors">
              <span class="sr-only">Twitter</span>
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path d="M8.29 20.251c7.547 0 11.675-6.253 11.675-11.675 0-.178 0-.355-.012-.53A8.348 8.348 0 0022 5.92a8.19 8.19 0 01-2.357.646 4.118 4.118 0 001.804-2.27 8.224 8.224 0 01-2.605.996 4.107 4.107 0 00-6.993 3.743 11.65 11.65 0 01-8.457-4.287 4.106 4.106 0 001.27 5.477A4.072 4.072 0 012.8 9.713v.052a4.105 4.105 0 003.292 4.022 4.095 4.095 0 01-1.853.07 4.108 4.108 0 003.834 2.85A8.233 8.233 0 012 18.407a11.616 11.616 0 006.29 1.84"/>
              </svg>
            </a>
            <a href="#" class="text-gray-400 hover:text-white transition-colors">
              <span class="sr-only">Facebook</span>
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path d="M22 12c0-5.523-4.477-10-10-10S2 6.477 2 12c0 4.991 3.657 9.128 8.438 9.878v-6.987h-2.54V12h2.54V9.797c0-2.506 1.492-3.89 3.777-3.89 1.094 0 2.238.195 2.238.195v2.46h-1.26c-1.243 0-1.63.771-1.63 1.562V12h2.773l-.443 2.89h-2.33v6.988C18.343 21.128 22 16.991 22 12z"/>
              </svg>
            </a>
            <a href="#" class="text-gray-400 hover:text-white transition-colors">
              <span class="sr-only">Instagram</span>
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 2.163c3.204 0 3.584.012 4.85.07 3.252.148 4.771 1.691 4.919 4.919.058 1.265.069 1.645.069 4.849 0 3.205-.012 3.584-.069 4.849-.149 3.225-1.664 4.771-4.919 4.919-1.266.058-1.644.07-4.85.07-3.204 0-3.584-.012-4.849-.07-3.26-.149-4.771-1.699-4.919-4.92-.058-1.265-.07-1.644-.07-4.849 0-3.204.013-3.583.07-4.849.149-3.227 1.664-4.771 4.919-4.919 1.266-.057 1.645-.069 4.849-.069zM12 0C8.741 0 8.333.014 7.053.072 2.695.272.273 2.69.073 7.052.014 8.333 0 8.741 0 12c0 3.259.014 3.668.072 4.948.2 4.358 2.618 6.78 6.98 6.98C8.333 23.986 8.741 24 12 24c3.259 0 3.668-.014 4.948-.072 4.354-.2 6.782-2.618 6.979-6.98.059-1.28.073-1.689.073-4.948 0-3.259-.014-3.667-.072-4.947-.196-4.354-2.617-6.78-6.979-6.98C15.668.014 15.259 0 12 0zm0 5.838a6.162 6.162 0 100 12.324 6.162 6.162 0 000-12.324zM12 16a4 4 0 110-8 4 4 0 010 8zm6.406-11.845a1.44 1.44 0 100 2.881 1.44 1.44 0 000-2.881z"/>
              </svg>
            </a>
            <a href="#" class="text-gray-400 hover:text-white transition-colors">
              <span class="sr-only">LinkedIn</span>
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path d="M19 0h-14c-2.761 0-5 2.239-5 5v14c0 2.761 2.239 5 5 5h14c2.762 0 5-2.239 5-5v-14c0-2.761-2.238-5-5-5zm-11 19h-3v-11h3v11zm-1.5-12.268c-.966 0-1.75-.79-1.75-1.764s.784-1.764 1.75-1.764 1.75.79 1.75 1.764-.783 1.764-1.75 1.764zm13.5 12.268h-3v-5.604c0-3.368-4-3.113-4 0v5.604h-3v-11h3v1.765c1.396-2.586 7-2.777 7 2.476v6.759z"/>
              </svg>
            </a>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template> 