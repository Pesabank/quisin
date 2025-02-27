&lt;template&gt;
  &lt;div
    aria-live="assertive"
    class="fixed inset-0 flex items-end px-4 py-6 pointer-events-none sm:p-6 sm:items-start z-50"
  &gt;
    &lt;div class="w-full flex flex-col items-center space-y-4 sm:items-end"&gt;
      &lt;TransitionGroup
        enter-active-class="transform ease-out duration-300 transition"
        enter-from-class="translate-y-2 opacity-0 sm:translate-y-0 sm:translate-x-2"
        enter-to-class="translate-y-0 opacity-100 sm:translate-x-0"
        leave-active-class="transition ease-in duration-100"
        leave-from-class="opacity-100"
        leave-to-class="opacity-0"
      &gt;
        &lt;div
          v-for="notification in notifications"
          :key="notification.id"
          class="max-w-sm w-full bg-white shadow-lg rounded-lg pointer-events-auto ring-1 ring-black ring-opacity-5 overflow-hidden"
        &gt;
          &lt;div class="p-4"&gt;
            &lt;div class="flex items-start"&gt;
              &lt;div class="flex-shrink-0"&gt;
                &lt;!-- Success Icon --&gt;
                &lt;svg
                  v-if="notification.type === 'success'"
                  class="h-6 w-6 text-green-400"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                &gt;
                  &lt;path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                  /&gt;
                &lt;/svg&gt;
                &lt;!-- Error Icon --&gt;
                &lt;svg
                  v-else-if="notification.type === 'error'"
                  class="h-6 w-6 text-red-400"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                &gt;
                  &lt;path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                  /&gt;
                &lt;/svg&gt;
                &lt;!-- Warning Icon --&gt;
                &lt;svg
                  v-else-if="notification.type === 'warning'"
                  class="h-6 w-6 text-yellow-400"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                &gt;
                  &lt;path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                  /&gt;
                &lt;/svg&gt;
                &lt;!-- Info Icon --&gt;
                &lt;svg
                  v-else
                  class="h-6 w-6 text-blue-400"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                &gt;
                  &lt;path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                  /&gt;
                &lt;/svg&gt;
              &lt;/div&gt;
              &lt;div class="ml-3 w-0 flex-1 pt-0.5"&gt;
                &lt;p v-if="notification.title" class="text-sm font-medium text-gray-900"&gt;
                  {{ notification.title }}
                &lt;/p&gt;
                &lt;p class="text-sm text-gray-500"&gt;{{ notification.message }}&lt;/p&gt;
              &lt;/div&gt;
              &lt;div class="ml-4 flex-shrink-0 flex"&gt;
                &lt;button
                  type="button"
                  class="bg-white rounded-md inline-flex text-gray-400 hover:text-gray-500 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                  @click="closeNotification(notification.id)"
                &gt;
                  &lt;span class="sr-only"&gt;Close&lt;/span&gt;
                  &lt;svg
                    class="h-5 w-5"
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 0 20 20"
                    fill="currentColor"
                  &gt;
                    &lt;path
                      fill-rule="evenodd"
                      d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                      clip-rule="evenodd"
                    /&gt;
                  &lt;/svg&gt;
                &lt;/button&gt;
              &lt;/div&gt;
            &lt;/div&gt;
          &lt;/div&gt;
        &lt;/div&gt;
      &lt;/TransitionGroup&gt;
    &lt;/div&gt;
  &lt;/div&gt;
&lt;/template&gt;

&lt;script setup lang="ts"&gt;
import { storeToRefs } from 'pinia'
import { useNotificationStore } from '~/stores/notification'

const notificationStore = useNotificationStore()
const { notifications } = storeToRefs(notificationStore)

const closeNotification = (id: string) => {
  notificationStore.remove(id)
}
&lt;/script&gt;
