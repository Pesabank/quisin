<template>
  <div class="min-h-screen bg-white">
    <!-- Header -->
    <div class="border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center py-4">
          <h1 class="text-2xl font-bold text-gray-900">Menu</h1>
          <div class="flex items-center space-x-4">
            <div class="bg-orange-100 px-4 py-2 rounded-full">
              <span class="text-[#FF6B00] font-medium">Table T123</span>
            </div>
            <button
              @click="cartOpen = true"
              class="relative inline-flex items-center px-4 py-2 text-sm font-medium text-white bg-[#FF6B00] rounded-md hover:bg-[#e66000]"
            >
              <ShoppingCartIcon class="h-5 w-5 mr-2" />
              Cart ({{ cartItemsCount }})
            </button>
          </div>
        </div>

        <!-- Category Tabs -->
        <div class="mt-2 -mb-px flex space-x-8 overflow-x-auto">
          <button
            v-for="category in categories"
            :key="category.id"
            @click="selectedCategory = category.id"
            :class="[
              'whitespace-nowrap pb-4 px-1 border-b-2 font-medium text-sm',
              selectedCategory === category.id
                ? 'border-[#FF6B00] text-[#FF6B00]'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            ]"
          >
            {{ category.name }}
          </button>
        </div>
      </div>
    </div>

    <!-- Menu Items -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="item in filteredItems"
          :key="item.id"
          class="bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow duration-200"
        >
          <img
            :src="item.image"
            :alt="item.name"
            class="w-full h-48 object-cover rounded-t-lg"
          />
          <div class="p-4">
            <div class="flex justify-between items-start">
              <div>
                <h3 class="text-lg font-medium text-gray-900">{{ item.name }}</h3>
                <p class="mt-1 text-sm text-gray-500 line-clamp-2">{{ item.description }}</p>
              </div>
              <p class="text-lg font-medium text-gray-900">Ksh {{ item.price.toFixed(2) }}</p>
            </div>
            <div class="mt-4">
              <button
                @click="addToCart(item)"
                class="w-full flex items-center justify-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000]"
              >
                Add to Cart
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Cart Slide Over -->
    <TransitionRoot as="template" :show="cartOpen">
      <Dialog as="div" class="relative z-10" @close="cartOpen = false">
        <TransitionChild
          as="template"
          enter="ease-in-out duration-500"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="ease-in-out duration-500"
          leave-from="opacity-100"
          leave-to="opacity-0"
        >
          <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" />
        </TransitionChild>

        <div class="fixed inset-0 overflow-hidden">
          <div class="absolute inset-0 overflow-hidden">
            <div class="pointer-events-none fixed inset-y-0 right-0 flex max-w-full pl-10">
              <TransitionChild
                as="template"
                enter="transform transition ease-in-out duration-500 sm:duration-700"
                enter-from="translate-x-full"
                enter-to="translate-x-0"
                leave="transform transition ease-in-out duration-500 sm:duration-700"
                leave-from="translate-x-0"
                leave-to="translate-x-full"
              >
                <DialogPanel class="pointer-events-auto w-screen max-w-md">
                  <div class="flex h-full flex-col overflow-y-scroll bg-white shadow-xl">
                    <div class="flex-1 overflow-y-auto px-4 py-6 sm:px-6">
                      <div class="flex items-start justify-between">
                        <DialogTitle class="text-lg font-medium text-gray-900">Shopping Cart</DialogTitle>
                        <div class="ml-3 flex h-7 items-center">
                          <button
                            type="button"
                            class="relative -m-2 p-2 text-gray-400 hover:text-gray-500"
                            @click="cartOpen = false"
                          >
                            <span class="absolute -inset-0.5" />
                            <span class="sr-only">Close panel</span>
                            <XMarkIcon class="h-6 w-6" aria-hidden="true" />
                          </button>
                        </div>
                      </div>

                      <div class="mt-8">
                        <div class="flow-root">
                          <ul role="list" class="-my-6 divide-y divide-gray-200">
                            <li v-for="item in cartItems" :key="item.id" class="flex py-6">
                              <div class="h-24 w-24 flex-shrink-0 overflow-hidden rounded-md border border-gray-200">
                                <img
                                  :src="item.image"
                                  :alt="item.name"
                                  class="h-full w-full object-cover object-center"
                                />
                              </div>

                              <div class="ml-4 flex flex-1 flex-col">
                                <div>
                                  <div class="flex justify-between text-base font-medium text-gray-900">
                                    <h3>{{ item.name }}</h3>
                                    <p class="ml-4">Ksh {{ (item.price * item.quantity).toFixed(2) }}</p>
                                  </div>
                                  <div class="mt-2">
                                    <textarea
                                      v-model="item.comments"
                                      rows="2"
                                      class="block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-sm"
                                      placeholder="Add special instructions..."
                                    ></textarea>
                                  </div>
                                </div>
                                <div class="flex flex-1 items-end justify-between text-sm">
                                  <div class="flex items-center space-x-2">
                                    <button
                                      @click="decrementQuantity(item)"
                                      class="p-1 rounded-full text-gray-400 hover:text-gray-500"
                                    >
                                      <MinusCircleIcon class="h-5 w-5" />
                                    </button>
                                    <span class="text-gray-500">{{ item.quantity }}</span>
                                    <button
                                      @click="incrementQuantity(item)"
                                      class="p-1 rounded-full text-gray-400 hover:text-gray-500"
                                    >
                                      <PlusCircleIcon class="h-5 w-5" />
                                    </button>
                                  </div>

                                  <button
                                    @click="removeFromCart(item)"
                                    type="button"
                                    class="font-medium text-[#FF6B00] hover:text-[#e66000]"
                                  >
                                    Remove
                                  </button>
                                </div>
                              </div>
                            </li>
                          </ul>
                        </div>
                      </div>
                    </div>

                    <div class="border-t border-gray-200 px-4 py-6 sm:px-6">
                      <div class="flex justify-between text-base font-medium text-gray-900">
                        <p>Subtotal</p>
                        <p>Ksh {{ cartSubtotal.toFixed(2) }}</p>
                      </div>
                      <div class="mt-2 flex justify-between text-sm text-gray-500">
                        <p>Tax (10%)</p>
                        <p>Ksh {{ cartTax.toFixed(2) }}</p>
                      </div>
                      <div class="mt-2 flex justify-between text-lg font-medium text-gray-900">
                        <p>Total</p>
                        <p>Ksh {{ cartTotal.toFixed(2) }}</p>
                      </div>
                      <div class="mt-6">
                        <button
                          @click="proceedToCheckout"
                          :disabled="cartItems.length === 0"
                          :class="[
                            cartItems.length === 0
                              ? 'bg-gray-300 cursor-not-allowed'
                              : 'bg-[#FF6B00] hover:bg-[#e66000]',
                            'w-full flex items-center justify-center rounded-md border border-transparent px-6 py-3 text-base font-medium text-white shadow-sm focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]'
                          ]"
                        >
                          Proceed to Checkout
                        </button>
                      </div>
                      <div class="mt-6 flex justify-center text-center text-sm text-gray-500">
                        <p>
                          or
                          <button
                            type="button"
                            class="font-medium text-[#FF6B00] hover:text-[#e66000]"
                            @click="cartOpen = false"
                          >
                            Continue Shopping
                            <span aria-hidden="true"> →</span>
                          </button>
                        </p>
                      </div>
                    </div>
                  </div>
                </DialogPanel>
              </TransitionChild>
            </div>
          </div>
        </div>
      </Dialog>
    </TransitionRoot>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import {
  ShoppingCartIcon,
  XMarkIcon,
  MinusCircleIcon,
  PlusCircleIcon
} from '@heroicons/vue/24/outline'
import { useRouter } from 'vue-router'

interface MenuItem {
  id: string
  name: string
  description: string
  price: number
  image: string
  categoryId: string
}

interface CartItem extends MenuItem {
  quantity: number
  comments?: string
}

interface Category {
  id: string
  name: string
}

// State
const selectedCategory = ref('main-course')
const cartOpen = ref(false)
const cartItems = ref<CartItem[]>([])
const isGroupOrder = ref(false)
const router = useRouter()

// Mock data
const categories: Category[] = [
  { id: 'main-course', name: 'Main Course' },
  { id: 'drinks', name: 'Drinks' },
  { id: 'desserts', name: 'Desserts' },
  { id: 'specials', name: 'Specials' },
  { id: 'starters', name: 'Starters' }
]

const menuItems: MenuItem[] = [
  {
    id: '1',
    name: 'Pancake Stack',
    description: 'Golden pancakes served with fresh berries, maple syrup, and a dollop of whipped cream.',
    price: 600.00,
    image: 'https://images.unsplash.com/photo-1567620832903-9fc6debc209f',
    categoryId: 'main-course'
  },
  {
    id: '2',
    name: 'Spring Mix Salad',
    description: 'A fresh blend of baby greens, crisp lettuce, and seasonal vegetables, served with your choice of dressing.',
    price: 400.00,
    image: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd',
    categoryId: 'main-course'
  },
  {
    id: '3',
    name: 'Spaghetti Carbonara',
    description: 'Classic Italian pasta tossed in a creamy sauce made with eggs, Parmesan, crispy pancetta, and black pepper.',
    price: 1300.00,
    image: 'https://images.unsplash.com/photo-1612874742237-6526221588e3',
    categoryId: 'main-course'
  }
]

// Computed
const filteredItems = computed(() => {
  return menuItems.filter(item => item.categoryId === selectedCategory.value)
})

const cartItemsCount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

const cartSubtotal = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0)
})

const cartTax = computed(() => cartSubtotal.value * 0.1)
const cartTotal = computed(() => cartSubtotal.value + cartTax.value)

// Methods
const addToCart = (item: MenuItem) => {
  const existingItem = cartItems.value.find(i => i.id === item.id)
  if (existingItem) {
    existingItem.quantity++
  } else {
    cartItems.value.push({ ...item, quantity: 1 })
  }
  cartOpen.value = true
}

const incrementQuantity = (item: CartItem) => {
  item.quantity++
}

const decrementQuantity = (item: CartItem) => {
  if (item.quantity > 1) {
    item.quantity--
  } else {
    removeFromCart(item)
  }
}

const removeFromCart = (item: CartItem) => {
  const index = cartItems.value.findIndex(i => i.id === item.id)
  if (index !== -1) {
    cartItems.value.splice(index, 1)
  }
}

const proceedToCheckout = () => {
  // Store cart items in localStorage
  localStorage.setItem('cartItems', JSON.stringify(cartItems.value))
  
  if (isGroupOrder.value) {
    // For group orders, store as member's items
    const memberData = {
      id: localStorage.getItem('memberId') || Date.now().toString(),
      name: localStorage.getItem('guestName') || 'Guest',
      status: 'Member',
      items: cartItems.value
    }
    localStorage.setItem('memberOrderData', JSON.stringify(memberData))
    router.push('/customer/group-order-summary')
  } else {
    router.push('/customer/order-summary')
  }
}

// Add watchers to persist cart items
watch(cartItems, (newItems) => {
  localStorage.setItem('cartItems', JSON.stringify(newItems))
}, { deep: true })
</script>

<style scoped>
.aspect-w-4 {
  position: relative;
  padding-bottom: 75%;
}

.aspect-w-4 > * {
  position: absolute;
  height: 100%;
  width: 100%;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
}
</style> 