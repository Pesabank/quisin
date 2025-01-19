import { useAuthStore } from '@/stores/auth';
import { storeToRefs } from 'pinia';

interface ApiOptions extends Omit<RequestInit, 'headers'> {
  requiresAuth?: boolean;
  headers?: Record<string, string>;
}

export function useApi() {
  const authStore = useAuthStore();
  const { accessToken } = storeToRefs(authStore);

  async function fetchWithAuth(url: string, options: ApiOptions = {}) {
    const { requiresAuth = true, headers = {}, ...rest } = options;
    const requestHeaders: Record<string, string> = {
      'Content-Type': 'application/json',
      ...headers
    };

    if (requiresAuth && accessToken.value) {
      requestHeaders.Authorization = `Bearer ${accessToken.value}`;
    }

    try {
      const response = await fetch(url, {
        headers: requestHeaders,
        ...rest,
      });

      // Handle 401 Unauthorized
      if (response.status === 401) {
        try {
          await authStore.refreshAccessToken();
          // Retry the request with new token
          requestHeaders.Authorization = `Bearer ${accessToken.value}`;
          return fetch(url, { 
            headers: requestHeaders,
            ...rest 
          });
        } catch (error) {
          authStore.logout();
          throw new Error('Session expired');
        }
      }

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return response;
    } catch (error) {
      console.error('API request failed:', error);
      throw error;
    }
  }

  return {
    fetchWithAuth,
  };
} 