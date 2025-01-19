<template>
  <div class="min-h-screen bg-gradient-to-b from-gray-50 to-white">
    <!-- Hero Section -->
    <div class="relative py-16 sm:py-24">
      <div class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center">
          <h2 class="text-base text-[#FF6B00] font-semibold tracking-wide uppercase">Contact Us</h2>
          <p class="mt-2 text-4xl font-extrabold text-gray-900 sm:text-5xl lg:text-6xl">
            Let's Start a Conversation
          </p>
          <p class="mt-6 max-w-2xl text-xl text-gray-500 mx-auto">
            Have questions about Quisin? We're here to help and would love to hear from you.
            Our team typically responds within 24 hours.
          </p>
        </div>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pb-24">
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 lg:gap-16">
        <!-- Contact Form -->
        <div class="relative">
          <div class="bg-white rounded-2xl shadow-xl p-8 lg:p-10">
            <form @submit.prevent="submitForm" class="space-y-8">
              <div>
                <label class="block text-lg font-medium text-gray-900 mb-3">Full Name</label>
                <input
                  type="text"
                  v-model="form.name"
                  class="block w-full px-6 py-4 text-lg rounded-xl border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring focus:ring-[#FF6B00] focus:ring-opacity-50 transition-shadow duration-200"
                  placeholder="John Doe"
                >
              </div>

              <div>
                <label class="block text-lg font-medium text-gray-900 mb-3">Email Address</label>
                <input
                  type="email"
                  v-model="form.email"
                  class="block w-full px-6 py-4 text-lg rounded-xl border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring focus:ring-[#FF6B00] focus:ring-opacity-50 transition-shadow duration-200"
                  placeholder="john@example.com"
                >
              </div>

              <div>
                <label class="block text-lg font-medium text-gray-900 mb-3">What can we help you with?</label>
                <select
                  v-model="form.subject"
                  class="block w-full px-6 py-4 text-lg rounded-xl border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring focus:ring-[#FF6B00] focus:ring-opacity-50 transition-shadow duration-200"
                >
                  <option value="">Select a topic</option>
                  <option value="general">General Inquiry</option>
                  <option value="support">Technical Support</option>
                  <option value="sales">Sales & Pricing</option>
                  <option value="partnership">Partnership Opportunities</option>
                  <option value="feedback">Product Feedback</option>
                </select>
              </div>

              <div>
                <label class="block text-lg font-medium text-gray-900 mb-3">Your Message</label>
                <textarea
                  v-model="form.message"
                  rows="6"
                  class="block w-full px-6 py-4 text-lg rounded-xl border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring focus:ring-[#FF6B00] focus:ring-opacity-50 transition-shadow duration-200"
                  placeholder="Tell us more about your needs..."
                ></textarea>
              </div>

              <div class="pt-4">
                <button
                  type="submit"
                  class="w-full flex justify-center items-center px-8 py-4 text-lg font-medium rounded-xl text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00] transform transition-all duration-200 hover:scale-[1.02] hover:shadow-lg"
                >
                  Send Message
                  <PaperAirplaneIcon class="ml-2 h-5 w-5 rotate-90" />
                </button>
              </div>
            </form>
          </div>
        </div>

        <!-- Contact Information -->
        <div class="lg:pl-8">
          <!-- Quick Contact Cards -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-6 mb-8">
            <div class="bg-white rounded-xl shadow-lg p-6 transform transition-all duration-200 hover:scale-[1.02] hover:shadow-xl">
              <PhoneIcon class="h-8 w-8 text-[#FF6B00] mb-4" />
              <h3 class="text-xl font-bold text-gray-900 mb-2">Phone Support</h3>
              <p class="text-gray-600 mb-4">Available Mon-Fri, 9am-6pm</p>
              <a href="tel:+15551234567" class="text-lg font-medium text-[#FF6B00] hover:text-[#e66000]">
                +1 (555) 123-4567
              </a>
            </div>
            
            <div class="bg-white rounded-xl shadow-lg p-6 transform transition-all duration-200 hover:scale-[1.02] hover:shadow-xl">
              <EnvelopeIcon class="h-8 w-8 text-[#FF6B00] mb-4" />
              <h3 class="text-xl font-bold text-gray-900 mb-2">Email Us</h3>
              <p class="text-gray-600 mb-4">We reply within 24 hours</p>
              <a href="mailto:support@quisin.com" class="text-lg font-medium text-[#FF6B00] hover:text-[#e66000]">
                support@quisin.com
              </a>
            </div>
          </div>

          <!-- Office Location -->
          <div class="bg-white rounded-xl shadow-lg p-8 mb-8 transform transition-all duration-200 hover:scale-[1.02] hover:shadow-xl">
            <div class="flex items-start space-x-4">
              <MapPinIcon class="h-8 w-8 text-[#FF6B00] flex-shrink-0" />
              <div>
                <h3 class="text-xl font-bold text-gray-900 mb-2">Visit Our Office</h3>
                <p class="text-gray-600 text-lg leading-relaxed">
                  123 Restaurant Street<br>
                  Foodie District<br>
                  City, State 12345
                </p>
              </div>
            </div>
          </div>

          <!-- Business Hours -->
          <div class="bg-white rounded-xl shadow-lg p-8 mb-8 transform transition-all duration-200 hover:scale-[1.02] hover:shadow-xl">
            <h3 class="text-xl font-bold text-gray-900 mb-4">Business Hours</h3>
            <div class="space-y-3">
              <div class="flex justify-between items-center">
                <span class="text-gray-600">Monday - Friday</span>
                <span class="text-gray-900 font-medium">9:00 AM - 6:00 PM</span>
              </div>
              <div class="flex justify-between items-center">
                <span class="text-gray-600">Saturday</span>
                <span class="text-gray-900 font-medium">10:00 AM - 4:00 PM</span>
              </div>
              <div class="flex justify-between items-center">
                <span class="text-gray-600">Sunday</span>
                <span class="text-gray-900 font-medium">Closed</span>
              </div>
            </div>
          </div>

          <!-- Social Links -->
          <div class="bg-white rounded-xl shadow-lg p-8 transform transition-all duration-200 hover:scale-[1.02] hover:shadow-xl">
            <h3 class="text-xl font-bold text-gray-900 mb-4">Connect With Us</h3>
            <div class="flex space-x-6">
              <a href="#" class="text-gray-400 hover:text-[#FF6B00] transform transition-all duration-200 hover:scale-110">
                <span class="sr-only">Facebook</span>
                <svg class="h-8 w-8" fill="currentColor" viewBox="0 0 24 24">
                  <path fill-rule="evenodd" d="M22 12c0-5.523-4.477-10-10-10S2 6.477 2 12c0 4.991 3.657 9.128 8.438 9.878v-6.987h-2.54V12h2.54V9.797c0-2.506 1.492-3.89 3.777-3.89 1.094 0 2.238.195 2.238.195v2.46h-1.26c-1.243 0-1.63.771-1.63 1.562V12h2.773l-.443 2.89h-2.33v6.988C18.343 21.128 22 16.991 22 12z" clip-rule="evenodd" />
                </svg>
              </a>
              <a href="#" class="text-gray-400 hover:text-[#FF6B00] transform transition-all duration-200 hover:scale-110">
                <span class="sr-only">Twitter</span>
                <svg class="h-8 w-8" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M8.29 20.251c7.547 0 11.675-6.253 11.675-11.675 0-.178 0-.355-.012-.53A8.348 8.348 0 0022 5.92a8.19 8.19 0 01-2.357.646 4.118 4.118 0 001.804-2.27 8.224 8.224 0 01-2.605.996 4.107 4.107 0 00-6.993 3.743 11.65 11.65 0 01-8.457-4.287 4.106 4.106 0 001.27 5.477A4.072 4.072 0 012.8 9.713v.052a4.105 4.105 0 003.292 4.022 4.095 4.095 0 01-1.853.07 4.108 4.108 0 003.834 2.85A8.233 8.233 0 012 18.407a11.616 11.616 0 006.29 1.84" />
                </svg>
              </a>
              <a href="#" class="text-gray-400 hover:text-[#FF6B00] transform transition-all duration-200 hover:scale-110">
                <span class="sr-only">LinkedIn</span>
                <svg class="h-8 w-8" fill="currentColor" viewBox="0 0 24 24">
                  <path fill-rule="evenodd" d="M19 0h-14c-2.761 0-5 2.239-5 5v14c0 2.761 2.239 5 5 5h14c2.762 0 5-2.239 5-5v-14c0-2.761-2.238-5-5-5zm-11 19h-3v-11h3v11zm-1.5-12.268c-.966 0-1.75-.79-1.75-1.764s.784-1.764 1.75-1.764 1.75.79 1.75 1.764-.783 1.764-1.75 1.764zm13.5 12.268h-3v-5.604c0-3.368-4-3.113-4 0v5.604h-3v-11h3v1.765c1.396-2.586 7-2.777 7 2.476v6.759z" clip-rule="evenodd" />
                </svg>
              </a>
              <a href="#" class="text-gray-400 hover:text-[#FF6B00] transform transition-all duration-200 hover:scale-110">
                <span class="sr-only">Instagram</span>
                <svg class="h-8 w-8" fill="currentColor" viewBox="0 0 24 24">
                  <path fill-rule="evenodd" d="M12.315 2c2.43 0 2.784.013 3.808.06 1.064.049 1.791.218 2.427.465a4.902 4.902 0 011.772 1.153 4.902 4.902 0 011.153 1.772c.247.636.416 1.363.465 2.427.048 1.067.06 1.407.06 4.123v.08c0 2.643-.012 2.987-.06 4.043-.049 1.064-.218 1.791-.465 2.427a4.902 4.902 0 01-1.153 1.772 4.902 4.902 0 01-1.772 1.153c-.636.247-1.363.416-2.427.465-1.067.048-1.407.06-4.123.06h-.08c-2.643 0-2.987-.012-4.043-.06-1.064-.049-1.791-.218-2.427-.465a4.902 4.902 0 01-1.772-1.153 4.902 4.902 0 01-1.153-1.772c-.247-.636-.416-1.363-.465-2.427-.047-1.024-.06-1.379-.06-3.808v-.63c0-2.43.013-2.784.06-3.808.049-1.064.218-1.791.465-2.427a4.902 4.902 0 011.153-1.772A4.902 4.902 0 015.45 2.525c.636-.247 1.363-.416 2.427-.465C8.901 2.013 9.256 2 11.685 2h.63zm-.081 1.802h-.468c-2.456 0-2.784.011-3.807.058-.975.045-1.504.207-1.857.344-.467.182-.8.398-1.15.748-.35.35-.566.683-.748 1.15-.137.353-.3.882-.344 1.857-.047 1.023-.058 1.351-.058 3.807v.468c0 2.456.011 2.784.058 3.807.045.975.207 1.504.344 1.857.182.466.399.8.748 1.15.35.35.683.566 1.15.748.353.137.882.3 1.857.344 1.054.048 1.37.058 4.041.058h.08c2.597 0 2.917-.01 3.96-.058.976-.045 1.505-.207 1.858-.344.466-.182.8-.398 1.15-.748.35-.35.566-.683.748-1.15.137-.353.3-.882.344-1.857.048-1.055.058-1.37.058-4.041v-.08c0-2.597-.01-2.917-.058-3.96-.045-.976-.207-1.505-.344-1.858a3.097 3.097 0 00-.748-1.15 3.098 3.098 0 00-1.15-.748c-.353-.137-.882-.3-1.857-.344-1.023-.047-1.351-.058-3.807-.058zM12 6.865a5.135 5.135 0 110 10.27 5.135 5.135 0 010-10.27zm0 1.802a3.333 3.333 0 100 6.666 3.333 3.333 0 000-6.666zm5.338-3.205a1.2 1.2 0 110 2.4 1.2 1.2 0 010-2.4z" clip-rule="evenodd" />
                </svg>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import {
  MapPinIcon,
  PhoneIcon,
  EnvelopeIcon,
  PaperAirplaneIcon
} from '@heroicons/vue/24/outline'

const form = ref({
  name: '',
  email: '',
  subject: '',
  message: ''
})

const submitForm = () => {
  // TODO: Implement form submission
  console.log('Form submitted:', form.value)
}
</script> 