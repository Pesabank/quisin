<template>
  <TransitionRoot appear :show="show" as="template">
    <Dialog as="div" class="relative z-10" @close="$emit('close')">
      <TransitionChild
        as="template"
        enter="duration-300 ease-out"
        enter-from="opacity-0"
        enter-to="opacity-100"
        leave="duration-200 ease-in"
        leave-from="opacity-100"
        leave-to="opacity-0"
      >
        <div class="fixed inset-0 bg-black bg-opacity-25" />
      </TransitionChild>

      <div class="fixed inset-0 overflow-y-auto">
        <div class="flex min-h-full items-center justify-center p-4">
          <TransitionChild
            as="template"
            enter="duration-300 ease-out"
            enter-from="opacity-0 scale-95"
            enter-to="opacity-100 scale-100"
            leave="duration-200 ease-in"
            leave-from="opacity-100 scale-100"
            leave-to="opacity-0 scale-95"
          >
            <DialogPanel class="w-full max-w-md transform overflow-hidden rounded-lg bg-white p-8 text-left align-middle shadow-xl transition-all">
              <div class="flex justify-between items-center mb-6">
                <DialogTitle as="h3" class="text-2xl font-semibold text-gray-900">
                  Update Inventory
                </DialogTitle>
                <button @click="$emit('close')" class="text-gray-400 hover:text-gray-500">
                  <XMarkIcon class="h-6 w-6" />
                </button>
              </div>

              <form @submit.prevent="$emit('save', form)">
                <div class="space-y-6">
                  <div>
                    <label class="block text-xl font-medium text-gray-700 mb-2">Current Stock</label>
                    <input
                      type="number"
                      v-model="form.newStock"
                      class="mt-2 block w-full h-24 rounded-lg border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-3xl px-6"
                      placeholder="Enter new stock level"
                      required
                    />
                  </div>

                  <div>
                    <label class="block text-xl font-medium text-gray-700 mb-2">Reason for Change</label>
                    <select
                      v-model="form.reason"
                      class="mt-2 block w-full h-24 rounded-lg border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-3xl px-6"
                    >
                      <option value="restock">Restock</option>
                      <option value="adjustment">Inventory Adjustment</option>
                      <option value="waste">Waste/Loss</option>
                      <option value="other">Other</option>
                    </select>
                  </div>

                  <div>
                    <label class="block text-xl font-medium text-gray-700 mb-2">Notes</label>
                    <textarea
                      v-model="form.notes"
                      rows="4"
                      class="mt-2 block w-full rounded-lg border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-3xl px-6 py-4"
                      placeholder="Optional notes about this inventory update"
                    />
                  </div>
                </div>

                <div class="mt-8 flex justify-end space-x-4">
                  <button
                    type="button"
                    class="inline-flex justify-center rounded-lg border border-gray-300 bg-white px-8 py-4 text-lg font-medium text-gray-700 hover:bg-gray-50"
                    @click="$emit('close')"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    class="inline-flex justify-center rounded-lg border border-transparent bg-[#FF6B00] px-8 py-4 text-lg font-medium text-white hover:bg-[#e66000]"
                  >
                    Update Stock
                  </button>
                </div>
              </form>
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import { XMarkIcon } from '@heroicons/vue/24/outline'

const props = defineProps<{
  show: boolean
  initialStock: number
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'save', form: { newStock: number; reason: string; notes: string }): void
}>()

const form = ref({
  newStock: props.initialStock,
  reason: 'restock',
  notes: ''
})
</script> 