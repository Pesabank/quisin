<template>
  <div>
    <!-- Hero Section -->
    <section class="relative min-h-[80vh] bg-gradient-to-br from-gray-900 to-gray-800 pt-16 pb-20 px-4 sm:px-6 lg:pt-24 lg:pb-28 lg:px-8 overflow-hidden">
      <!-- Background Pattern -->
      <div class="absolute inset-0 opacity-10">
        <div class="absolute inset-0" style="background-image: url('data:image/svg+xml,%3Csvg width=\'60\' height=\'60\' viewBox=\'0 0 60 60\' xmlns=\'http://www.w3.org/2000/svg\'%3E%3Cg fill=\'none\' fill-rule=\'evenodd\'%3E%3Cg fill=\'%23ffffff\' fill-opacity=\'0.4\'%3E%3Cpath d=\'M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z\'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E')"></div>
      </div>
      
      <div class="relative max-w-7xl mx-auto">
        <div class="text-center">
          <h1 class="text-4xl tracking-tight font-extrabold text-white sm:text-5xl md:text-6xl">
            <span class="block mb-2">Welcome to</span>
            <span class="block text-[#FF6B00] font-serif italic">Quisin</span>
            <span class="block text-2xl sm:text-3xl md:text-4xl mt-4 font-light">Modern Restaurant Management System</span>
          </h1>
          <p class="mt-6 max-w-md mx-auto text-xl text-gray-300 sm:text-2xl md:mt-8 md:max-w-3xl">
            Elevate your dining experience with our comprehensive management solution.
          </p>
          <div class="mt-8 max-w-md mx-auto sm:flex sm:justify-center md:mt-12">
            <div class="rounded-md shadow">
              <NuxtLink
                to="/get-started"
                class="w-full flex items-center justify-center px-8 py-4 border border-transparent text-lg font-medium rounded-md text-white bg-[#FF6B00] hover:bg-[#e66000] transform hover:scale-105 transition-all duration-200 ease-in-out"
              >
                Get Started
              </NuxtLink>
            </div>
            <div class="mt-4 rounded-md shadow sm:mt-0 sm:ml-4">
              <NuxtLink
                to="/about"
                class="w-full flex items-center justify-center px-8 py-4 border-2 border-[#FF6B00] text-lg font-medium rounded-md text-[#FF6B00] bg-transparent hover:bg-[#FF6B00] hover:text-white transform hover:scale-105 transition-all duration-200 ease-in-out"
              >
                Learn More
              </NuxtLink>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="py-24 bg-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-16">
          <h2 class="text-3xl font-extrabold text-gray-900 sm:text-4xl">
            Popular Restaurants
          </h2>
          <p class="mt-4 text-xl text-gray-600">
            Discover the most loved restaurants in your area
          </p>
        </div>

        <!-- Popular Restaurants -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-20">
          <div v-for="restaurant in popularRestaurants" :key="restaurant.id" class="bg-white rounded-xl shadow-lg overflow-hidden hover:shadow-xl transition-all duration-300">
            <div class="relative">
              <img :src="restaurant.image" :alt="restaurant.name" class="w-full h-48 object-cover">
              <div class="absolute top-4 right-4 bg-white px-2 py-1 rounded-full text-sm font-medium text-[#FF6B00]">
                {{ restaurant.rating }} ★
              </div>
            </div>
            <div class="p-6">
              <h3 class="text-xl font-bold text-gray-900">{{ restaurant.name }}</h3>
              <p class="mt-2 text-gray-600">{{ restaurant.cuisine }}</p>
              <div class="mt-4 flex items-center justify-between">
                <span class="text-sm text-gray-500">{{ restaurant.location }}</span>
                <NuxtLink :to="'/restaurant/' + restaurant.id" class="text-[#FF6B00] font-medium hover:text-[#e66000]">
                  View Menu →
                </NuxtLink>
              </div>
            </div>
          </div>
            </div>

        <!-- Best Selling Items -->
        <div class="text-center mb-16">
          <h2 class="text-3xl font-extrabold text-gray-900 sm:text-4xl">
            Most Popular Dishes
          </h2>
          <p class="mt-4 text-xl text-gray-600">
            Trending dishes that everyone loves
            </p>
          </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          <div v-for="dish in popularDishes" :key="dish.id" class="group relative bg-white rounded-xl shadow-lg overflow-hidden hover:shadow-xl transition-all duration-300">
            <div class="relative">
              <img :src="dish.image" :alt="dish.name" class="w-full h-48 object-cover group-hover:scale-105 transition-transform duration-300">
              <div class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
            </div>
            <div class="p-4">
              <div class="flex justify-between items-start mb-2">
                <h3 class="text-lg font-semibold text-gray-900">{{ dish.name }}</h3>
                <span class="text-[#FF6B00] font-bold">${{ dish.price.toFixed(2) }}</span>
              </div>
              <p class="text-sm text-gray-600 mb-2">{{ dish.restaurant }}</p>
              <div class="flex items-center justify-between">
                <div class="flex items-center">
                  <span class="text-yellow-400">★</span>
                  <span class="ml-1 text-sm text-gray-600">{{ dish.rating }}</span>
            </div>
                <button class="px-4 py-2 bg-[#FF6B00] text-white text-sm rounded-full hover:bg-[#e66000] transition-colors duration-200">
                  Order Now
                </button>
          </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Call to Action Section -->
    <section class="bg-[#FF6B00] py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center">
          <h2 class="text-3xl font-extrabold text-white sm:text-4xl">
            Ready to Transform Your Restaurant?
          </h2>
          <p class="mt-4 text-xl text-white opacity-90">
            Join thousands of restaurants already using Quisin
          </p>
          <div class="mt-8">
            <NuxtLink
              to="/get-started"
              class="inline-flex items-center justify-center px-8 py-4 border border-transparent text-lg font-medium rounded-md text-[#FF6B00] bg-white hover:bg-gray-100 transform hover:scale-105 transition-all duration-200 ease-in-out"
            >
              Start Free Trial
            </NuxtLink>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'default',
  auth: false
})

const popularRestaurants = [
  {
    id: 1,
    name: "The Rustic Kitchen",
    cuisine: "Italian • Mediterranean",
    location: "Downtown",
    rating: 4.8,
    image: "https://placehold.co/800x600/FF6B00/FFFFFF.png?text=Rustic+Kitchen"
  },
  {
    id: 2,
    name: "Sakura Japanese",
    cuisine: "Japanese • Sushi",
    location: "Marina District",
    rating: 4.7,
    image: "https://placehold.co/800x600/FF6B00/FFFFFF.png?text=Sakura+Japanese"
  },
  {
    id: 3,
    name: "La Piazza",
    cuisine: "Italian • Pizza",
    location: "North Beach",
    rating: 4.9,
    image: "https://placehold.co/800x600/FF6B00/FFFFFF.png?text=La+Piazza"
  }
]

const popularDishes = [
  {
    id: 1,
    name: "Truffle Pasta",
    restaurant: "The Rustic Kitchen",
    price: 24.99,
    rating: 4.9,
    image: "https://placehold.co/800x600/FF6B00/FFFFFF.png?text=Truffle+Pasta"
  },
  {
    id: 2,
    name: "Dragon Roll",
    restaurant: "Sakura Japanese",
    price: 18.99,
    rating: 4.8,
    image: "https://placehold.co/800x600/FF6B00/FFFFFF.png?text=Dragon+Roll"
  },
  {
    id: 3,
    name: "Margherita Pizza",
    restaurant: "La Piazza",
    price: 19.99,
    rating: 4.7,
    image: "https://placehold.co/800x600/FF6B00/FFFFFF.png?text=Margherita+Pizza"
  },
  {
    id: 4,
    name: "Beef Wellington",
    restaurant: "The Rustic Kitchen",
    price: 39.99,
    rating: 4.9,
    image: "https://placehold.co/800x600/FF6B00/FFFFFF.png?text=Beef+Wellington"
  }
]
</script> 

<style scoped>
.bg-gradient-to-br {
  background-size: 400% 400%;
  animation: gradient 15s ease infinite;
}

@keyframes gradient {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}
</style> 