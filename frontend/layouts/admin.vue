<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Sidebar -->
    <div class="fixed inset-y-0 left-0 w-64 bg-white border-r border-gray-200 overflow-y-auto">
      <!-- Logo -->
      <div class="h-16 flex items-center px-6 border-b border-gray-200">
        <h1 class="text-xl font-bold text-[#FF6B00]">Quisin Admin</h1>
      </div>

      <!-- Navigation -->
      <nav class="mt-6 px-3">
        <div class="space-y-1">
          <NuxtLink
            v-for="item in navigationItems"
            :key="item.path"
            :to="item.path"
            :class="[
              'group flex items-center px-3 py-2 text-sm font-medium rounded-md transition-colors duration-200',
              $route.path.startsWith(item.path)
                ? 'bg-orange-50 text-[#FF6B00]'
                : 'text-gray-600 hover:bg-orange-50 hover:text-[#FF6B00]'
            ]"
          >
            <component
              :is="item.icon"
              :class="[
                'flex-shrink-0 -ml-1 mr-3 h-6 w-6',
                $route.path.startsWith(item.path)
                  ? 'text-[#FF6B00]'
                  : 'text-gray-400 group-hover:text-[#FF6B00]'
              ]"
            />
            {{ item.name }}
          </NuxtLink>
        </div>
      </nav>

      <!-- User Profile -->
      <div class="absolute bottom-0 w-full border-t border-gray-200">
        <div class="px-4 py-4">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="h-8 w-8 rounded-full bg-gray-200 flex items-center justify-center">
                <UserIcon class="h-5 w-5 text-gray-500" />
              </div>
            </div>
            <div class="ml-3">
              <p class="text-sm font-medium text-gray-700">Admin User</p>
              <p class="text-xs text-gray-500">admin@quisin.com</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="pl-64">
      <!-- Top Navigation -->
      <div class="sticky top-0 z-10 h-16 bg-white border-b border-gray-200 flex items-center justify-end px-6">
        <div class="flex items-center space-x-4">
          <button class="text-gray-400 hover:text-gray-500">
            <BellIcon class="h-6 w-6" />
          </button>
          <button class="text-gray-400 hover:text-gray-500">
            <Cog6ToothIcon class="h-6 w-6" />
          </button>
        </div>
      </div>

      <!-- Page Content -->
      <div class="p-6">
        <slot />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  HomeIcon,
  TableCellsIcon,
  QueueListIcon,
  UserGroupIcon,
  ChartBarIcon,
  BuildingOfficeIcon,
  UserIcon,
  BellIcon,
  Cog6ToothIcon,
  Square3Stack3DIcon,
  CalendarDaysIcon
} from '@heroicons/vue/24/outline'

const navigationItems = [
  { name: 'Dashboard', path: '/admin/dashboard', icon: HomeIcon },
  { name: 'Table Overview', path: '/admin/tables', icon: TableCellsIcon },
  { name: 'Menu & Inventory', path: '/admin/menu', icon: Square3Stack3DIcon },
  { name: 'Staff', path: '/admin/staff', icon: UserGroupIcon },
  { name: 'Chain', path: '/admin/chain', icon: BuildingOfficeIcon },
  { name: 'Reservations', path: '/admin/reservations', icon: CalendarDaysIcon },
  { name: 'Analytics', path: '/admin/analytics', icon: ChartBarIcon }
]
</script> 