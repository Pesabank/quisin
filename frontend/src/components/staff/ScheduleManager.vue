&lt;template&gt;
  &lt;div class="bg-white shadow sm:rounded-lg"&gt;
    &lt;div class="px-4 py-5 sm:p-6"&gt;
      &lt;div class="sm:flex sm:items-center"&gt;
        &lt;div class="sm:flex-auto"&gt;
          &lt;h3 class="text-lg font-medium leading-6 text-gray-900"&gt;Staff Schedule&lt;/h3&gt;
          &lt;p class="mt-2 text-sm text-gray-500"&gt;Manage staff shifts and availability&lt;/p&gt;
        &lt;/div&gt;
        &lt;div class="mt-4 sm:mt-0 sm:ml-16 sm:flex-none"&gt;
          &lt;button
            @click="openShiftModal"
            class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700"
          &gt;
            Add Shift
          &lt;/button&gt;
        &lt;/div&gt;
      &lt;/div&gt;

      &lt;!-- Week Navigation --&gt;
      &lt;div class="mt-6 flex items-center justify-between"&gt;
        &lt;button
          @click="previousWeek"
          class="inline-flex items-center px-3 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
        &gt;
          Previous Week
        &lt;/button&gt;
        &lt;span class="text-sm font-medium text-gray-900"&gt;
          {{ formatDateRange(weekStart, weekEnd) }}
        &lt;/span&gt;
        &lt;button
          @click="nextWeek"
          class="inline-flex items-center px-3 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
        &gt;
          Next Week
        &lt;/button&gt;
      &lt;/div&gt;

      &lt;!-- Schedule Grid --&gt;
      &lt;div class="mt-6 overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg"&gt;
        &lt;table class="min-w-full divide-y divide-gray-300"&gt;
          &lt;thead class="bg-gray-50"&gt;
            &lt;tr&gt;
              &lt;th class="py-3.5 pl-4 pr-3 text-left text-sm font-semibold text-gray-900"&gt;Staff Member&lt;/th&gt;
              &lt;template v-for="day in weekDays" :key="day.date"&gt;
                &lt;th class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900"&gt;
                  {{ formatDay(day.date) }}
                &lt;/th&gt;
              &lt;/template&gt;
            &lt;/tr&gt;
          &lt;/thead&gt;
          &lt;tbody class="divide-y divide-gray-200 bg-white"&gt;
            &lt;tr v-for="staff in staffMembers" :key="staff.id"&gt;
              &lt;td class="whitespace-nowrap py-4 pl-4 pr-3 text-sm font-medium text-gray-900"&gt;
                {{ staff.name }}
              &lt;/td&gt;
              &lt;template v-for="day in weekDays" :key="`${staff.id}-${day.date}`"&gt;
                &lt;td
                  class="px-3 py-4 text-sm text-gray-500"
                  @click="openShiftModal(staff, day.date)"
                &gt;
                  &lt;template v-if="getShift(staff.id, day.date)"&gt;
                    &lt;div class="flex flex-col space-y-1"&gt;
                      &lt;span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                        :class="getShiftStatusClass(getShift(staff.id, day.date))"&gt;
                        {{ formatShiftTime(getShift(staff.id, day.date)) }}
                      &lt;/span&gt;
                      &lt;span class="text-xs text-gray-500"&gt;
                        {{ getShift(staff.id, day.date).role }}
                      &lt;/span&gt;
                    &lt;/div&gt;
                  &lt;/template&gt;
                &lt;/td&gt;
              &lt;/template&gt;
            &lt;/tr&gt;
          &lt;/tbody&gt;
        &lt;/table&gt;
      &lt;/div&gt;
    &lt;/div&gt;

    &lt;!-- Shift Modal --&gt;
    &lt;TransitionRoot appear :show="isModalOpen" as="template"&gt;
      &lt;Dialog as="div" @close="closeShiftModal" class="relative z-10"&gt;
        &lt;TransitionChild
          as="template"
          enter="duration-300 ease-out"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="duration-200 ease-in"
          leave-from="opacity-100"
          leave-to="opacity-0"
        &gt;
          &lt;div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" /&gt;
        &lt;/TransitionChild&gt;

        &lt;div class="fixed inset-0 z-10 overflow-y-auto"&gt;
          &lt;div class="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0"&gt;
            &lt;TransitionChild
              as="template"
              enter="duration-300 ease-out"
              enter-from="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
              enter-to="opacity-100 translate-y-0 sm:scale-100"
              leave="duration-200 ease-in"
              leave-from="opacity-100 translate-y-0 sm:scale-100"
              leave-to="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
            &gt;
              &lt;DialogPanel class="relative transform overflow-hidden rounded-lg bg-white px-4 pt-5 pb-4 text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-lg sm:p-6"&gt;
                &lt;form @submit.prevent="saveShift"&gt;
                  &lt;div&gt;
                    &lt;div class="mt-3 sm:mt-5"&gt;
                      &lt;DialogTitle as="h3" class="text-lg font-medium leading-6 text-gray-900"&gt;
                        {{ editingShift ? 'Edit Shift' : 'Add New Shift' }}
                      &lt;/DialogTitle&gt;
                      
                      &lt;div class="mt-6 grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-6"&gt;
                        &lt;div class="sm:col-span-3"&gt;
                          &lt;label class="block text-sm font-medium text-gray-700"&gt;Start Time&lt;/label&gt;
                          &lt;input
                            type="time"
                            v-model="shiftForm.startTime"
                            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
                          &gt;
                        &lt;/div&gt;

                        &lt;div class="sm:col-span-3"&gt;
                          &lt;label class="block text-sm font-medium text-gray-700"&gt;End Time&lt;/label&gt;
                          &lt;input
                            type="time"
                            v-model="shiftForm.endTime"
                            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
                          &gt;
                        &lt;/div&gt;

                        &lt;div class="sm:col-span-6"&gt;
                          &lt;label class="block text-sm font-medium text-gray-700"&gt;Role&lt;/label&gt;
                          &lt;select
                            v-model="shiftForm.role"
                            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
                          &gt;
                            &lt;option value="WAITER"&gt;Waiter&lt;/option&gt;
                            &lt;option value="CHEF"&gt;Chef&lt;/option&gt;
                            &lt;option value="HOST"&gt;Host&lt;/option&gt;
                            &lt;option value="BARTENDER"&gt;Bartender&lt;/option&gt;
                          &lt;/select&gt;
                        &lt;/div&gt;

                        &lt;div class="sm:col-span-6"&gt;
                          &lt;label class="block text-sm font-medium text-gray-700"&gt;Notes&lt;/label&gt;
                          &lt;textarea
                            v-model="shiftForm.notes"
                            rows="3"
                            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
                          &gt;&lt;/textarea&gt;
                        &lt;/div&gt;
                      &lt;/div&gt;
                    &lt;/div&gt;
                  &lt;/div&gt;

                  &lt;div class="mt-5 sm:mt-6 sm:grid sm:grid-cols-2 sm:gap-3 sm:grid-flow-row-dense"&gt;
                    &lt;button
                      type="submit"
                      class="inline-flex w-full justify-center rounded-md border border-transparent bg-blue-600 px-4 py-2 text-base font-medium text-white shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:col-start-2 sm:text-sm"
                    &gt;
                      Save
                    &lt;/button&gt;
                    &lt;button
                      type="button"
                      @click="closeShiftModal"
                      class="mt-3 inline-flex w-full justify-center rounded-md border border-gray-300 bg-white px-4 py-2 text-base font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:col-start-1 sm:mt-0 sm:text-sm"
                    &gt;
                      Cancel
                    &lt;/button&gt;
                  &lt;/div&gt;
                &lt;/form&gt;
              &lt;/DialogPanel&gt;
            &lt;/TransitionChild&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/Dialog&gt;
    &lt;/TransitionRoot&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { ref, computed } from 'vue'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import { format, addDays, startOfWeek, endOfWeek, eachDayOfInterval } from 'date-fns'
import { useSocket } from '@/composables/useSocket'

interface Staff {
  id: string
  name: string
  role: string
}

interface Shift {
  id: string
  staffId: string
  date: string
  startTime: string
  endTime: string
  role: string
  notes?: string
}

const socket = useSocket()

const props = defineProps&lt;{
  staffMembers: Staff[]
  shifts: Shift[]
}&gt;()

const weekStart = ref(startOfWeek(new Date()))
const weekEnd = computed(() =&gt; endOfWeek(weekStart.value))

const weekDays = computed(() =&gt; {
  return eachDayOfInterval({
    start: weekStart.value,
    end: weekEnd.value,
  }).map(date =&gt; ({
    date: format(date, 'yyyy-MM-dd'),
    dayName: format(date, 'EEEE'),
  }))
})

const isModalOpen = ref(false)
const editingShift = ref&lt;Shift | null&gt;(null)
const selectedStaff = ref&lt;Staff | null&gt;(null)
const selectedDate = ref&lt;string | null&gt;(null)

const shiftForm = ref({
  startTime: '',
  endTime: '',
  role: 'WAITER',
  notes: '',
})

const formatDateRange = (start: Date, end: Date) =&gt; {
  return `${format(start, 'MMM d')} - ${format(end, 'MMM d, yyyy')}`
}

const formatDay = (date: string) =&gt; {
  return format(new Date(date), 'EEE, MMM d')
}

const formatShiftTime = (shift: Shift) =&gt; {
  return `${shift.startTime} - ${shift.endTime}`
}

const getShiftStatusClass = (shift: Shift) =&gt; {
  const now = new Date()
  const [hours, minutes] = shift.startTime.split(':').map(Number)
  const shiftStart = new Date(shift.date)
  shiftStart.setHours(hours, minutes)

  if (now &lt; shiftStart) {
    return 'bg-yellow-100 text-yellow-800'
  }
  return 'bg-green-100 text-green-800'
}

const getShift = (staffId: string, date: string) =&gt; {
  return props.shifts.find(shift =&gt; 
    shift.staffId === staffId && shift.date === date
  )
}

const openShiftModal = (staff?: Staff, date?: string) =&gt; {
  if (staff && date) {
    selectedStaff.value = staff
    selectedDate.value = date
    const existingShift = getShift(staff.id, date)
    
    if (existingShift) {
      editingShift.value = existingShift
      shiftForm.value = {
        startTime: existingShift.startTime,
        endTime: existingShift.endTime,
        role: existingShift.role,
        notes: existingShift.notes || '',
      }
    } else {
      editingShift.value = null
      shiftForm.value = {
        startTime: '09:00',
        endTime: '17:00',
        role: staff.role,
        notes: '',
      }
    }
    
    isModalOpen.value = true
  }
}

const closeShiftModal = () =&gt; {
  isModalOpen.value = false
  editingShift.value = null
  selectedStaff.value = null
  selectedDate.value = null
  shiftForm.value = {
    startTime: '',
    endTime: '',
    role: 'WAITER',
    notes: '',
  }
}

const saveShift = () =&gt; {
  if (!selectedStaff.value || !selectedDate.value) return

  const shiftData = {
    id: editingShift.value?.id || `new-${Date.now()}`,
    staffId: selectedStaff.value.id,
    date: selectedDate.value,
    ...shiftForm.value,
  }

  socket.emit('schedule:update', {
    scheduleId: shiftData.id,
    updates: shiftData,
  })

  closeShiftModal()
}

const previousWeek = () =&gt; {
  weekStart.value = addDays(weekStart.value, -7)
}

const nextWeek = () =&gt; {
  weekStart.value = addDays(weekStart.value, 7)
}

// Listen for schedule updates
socket.on('schedule:updated', (data) =&gt; {
  const index = props.shifts.findIndex(shift =&gt; shift.id === data.scheduleId)
  if (index !== -1) {
    props.shifts[index] = { ...props.shifts[index], ...data }
  } else {
    props.shifts.push(data)
  }
})
&lt;/script&gt;
