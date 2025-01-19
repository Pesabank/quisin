<template>
  <div class="max-w-md mx-auto p-4">
    <h2 class="text-2xl font-bold mb-6">Choose Payment Method</h2>
    
    <div class="space-y-4">
      <!-- Credit/Debit Card -->
      <button
        @click="selectPaymentMethod('card')"
        class="w-full p-4 border rounded-lg flex items-center justify-between hover:bg-gray-50"
      >
        <div class="flex items-center">
          <span class="text-lg">Credit/Debit Card</span>
        </div>
        <Icon name="heroicons:credit-card" class="h-6 w-6" />
      </button>

      <!-- Mobile Money -->
      <button
        @click="selectPaymentMethod('mobile_money')"
        class="w-full p-4 border rounded-lg flex items-center justify-between hover:bg-gray-50"
      >
        <div class="flex items-center">
          <span class="text-lg">Mobile Money</span>
        </div>
        <Icon name="heroicons:device-phone-mobile" class="h-6 w-6" />
      </button>

      <!-- Cash -->
      <button
        @click="selectPaymentMethod('cash')"
        class="w-full p-4 border rounded-lg flex items-center justify-between hover:bg-gray-50"
      >
        <div class="flex items-center">
          <span class="text-lg">Cash</span>
        </div>
        <Icon name="heroicons:banknotes" class="h-6 w-6" />
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
type PaymentMethod = 'card' | 'mobile_money' | 'cash'

const emit = defineEmits<{
  (e: 'select', method: PaymentMethod): void
}>()

function selectPaymentMethod(method: PaymentMethod) {
  emit('select', method)
}
</script> 