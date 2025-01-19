<template>
  <div class="min-h-screen bg-gray-50 py-6">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <h1 class="text-2xl font-semibold text-gray-900">Dashboard</h1>

      <!-- Stats Overview -->
      <div class="mt-6 grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        <!-- Total Reservations -->
        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <CalendarIcon class="h-6 w-6 text-blue-500" />
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Total Reservations</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">156</div>
                    <div class="ml-2 flex items-baseline text-sm font-semibold text-green-600">
                      <ArrowUpIcon class="self-center flex-shrink-0 h-5 w-5 text-green-500" />
                      <span class="ml-1">+12% vs last month</span>
                    </div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <!-- Active Tables -->
        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <TableCellsIcon class="h-6 w-6 text-orange-500" />
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Active Tables</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">23/30</div>
                    <div class="ml-2 flex items-baseline text-sm font-semibold text-green-600">
                      <ArrowUpIcon class="self-center flex-shrink-0 h-5 w-5 text-green-500" />
                      <span class="ml-1">+8% vs last month</span>
                    </div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <!-- Today's Revenue -->
        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <CurrencyDollarIcon class="h-6 w-6 text-green-500" />
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Today's Revenue</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">$2,435</div>
                    <div class="ml-2 flex items-baseline text-sm font-semibold text-green-600">
                      <ArrowUpIcon class="self-center flex-shrink-0 h-5 w-5 text-green-500" />
                      <span class="ml-1">+23.1% vs last month</span>
                    </div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <!-- Staff on Duty -->
        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <UsersIcon class="h-6 w-6 text-purple-500" />
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Staff on Duty</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">12</div>
                    <div class="ml-2 flex items-baseline text-sm font-semibold text-red-600">
                      <ArrowDownIcon class="self-center flex-shrink-0 h-5 w-5 text-red-500" />
                      <span class="ml-1">-2 vs last month</span>
                    </div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Recent Activity and Quick Actions -->
      <div class="mt-8 grid grid-cols-1 gap-6 lg:grid-cols-2">
        <!-- Recent Activity -->
        <div class="bg-white shadow rounded-lg">
          <div class="px-6 py-5 border-b border-gray-200">
            <h3 class="text-lg font-medium leading-6 text-gray-900">Recent Activity</h3>
          </div>
          <div class="px-6 py-5">
            <ul class="space-y-6">
              <li class="flex items-center space-x-4">
                <div class="flex-shrink-0">
                  <div class="h-8 w-8 rounded-full bg-blue-100 flex items-center justify-center">
                    <CalendarIcon class="h-5 w-5 text-blue-600" />
                  </div>
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-900">John Smith made a reservation for 4 people</p>
                  <p class="text-sm text-gray-500">5 minutes ago</p>
                </div>
              </li>
              <li class="flex items-center space-x-4">
                <div class="flex-shrink-0">
                  <div class="h-8 w-8 rounded-full bg-green-100 flex items-center justify-center">
                    <ClipboardDocumentListIcon class="h-5 w-5 text-green-600" />
                  </div>
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-900">Table 7 placed a new order</p>
                  <p class="text-sm text-gray-500">10 minutes ago</p>
                </div>
              </li>
              <li class="flex items-center space-x-4">
                <div class="flex-shrink-0">
                  <div class="h-8 w-8 rounded-full bg-gray-100 flex items-center justify-center">
                    <UserIcon class="h-5 w-5 text-gray-600" />
                  </div>
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-900">Sarah Johnson started their shift</p>
                  <p class="text-sm text-gray-500">30 minutes ago</p>
                </div>
              </li>
              <li class="flex items-center space-x-4">
                <div class="flex-shrink-0">
                  <div class="h-8 w-8 rounded-full bg-blue-100 flex items-center justify-center">
                    <ClockIcon class="h-5 w-5 text-blue-600" />
                  </div>
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-900">Mike Wilson modified their reservation</p>
                  <p class="text-sm text-gray-500">1 hour ago</p>
                </div>
              </li>
            </ul>
          </div>
        </div>

        <!-- Quick Actions -->
        <div class="bg-white shadow rounded-lg">
          <div class="px-6 py-5 border-b border-gray-200">
            <h3 class="text-lg font-medium leading-6 text-gray-900">Quick Actions</h3>
          </div>
          <div class="p-6 grid grid-cols-2 gap-4">
            <NuxtLink to="/admin/tables" class="flex items-center p-4 bg-gray-50 rounded-lg hover:bg-gray-100">
              <PlusIcon class="h-6 w-6 text-gray-600" />
              <span class="ml-3 text-sm font-medium text-gray-900">New Reservation</span>
            </NuxtLink>
            <NuxtLink to="/admin/menu" class="flex items-center p-4 bg-gray-50 rounded-lg hover:bg-gray-100">
              <Bars3Icon class="h-6 w-6 text-gray-600" />
              <span class="ml-3 text-sm font-medium text-gray-900">Add Menu Item</span>
            </NuxtLink>
            <NuxtLink to="/admin/tables" class="flex items-center p-4 bg-gray-50 rounded-lg hover:bg-gray-100">
              <QueueListIcon class="h-6 w-6 text-gray-600" />
              <span class="ml-3 text-sm font-medium text-gray-900">View Orders</span>
            </NuxtLink>
            <NuxtLink to="/admin/staff" class="flex items-center p-4 bg-gray-50 rounded-lg hover:bg-gray-100">
              <UsersIcon class="h-6 w-6 text-gray-600" />
              <span class="ml-3 text-sm font-medium text-gray-900">Staff Schedule</span>
            </NuxtLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'admin'
})

import {
  CalendarIcon,
  TableCellsIcon,
  CurrencyDollarIcon,
  UsersIcon,
  ArrowUpIcon,
  ArrowDownIcon,
  ClipboardDocumentListIcon,
  UserIcon,
  ClockIcon,
  PlusIcon,
  Bars3Icon,
  QueueListIcon
} from '@heroicons/vue/24/outline'
</script>
