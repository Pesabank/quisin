import type { UserRole } from './auth'

declare module 'vue-router' {
  interface RouteMeta {
    requiresAuth?: boolean;
    roles?: UserRole[];
    layout?: string;
  }
} 