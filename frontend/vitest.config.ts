/// <reference types="vitest" />
import { defineConfig } from 'vite'
import Vue from '@vitejs/plugin-vue'
import { fileURLToPath } from 'url'
import { resolve } from 'path'

export default defineConfig({
  plugins: [Vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      '~': resolve(__dirname)
    }
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./test/setup.ts'],
    css: true,
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      exclude: [
        'node_modules/',
        'dist/',
        '.nuxt/',
        'coverage/',
        '**/*.d.ts',
        '**/*.test.ts',
        '**/*.config.ts'
      ]
    },
    include: ['src/**/*.{test,spec}.{js,mjs,cjs,ts,mts,cts,jsx,tsx}'],
    deps: {
      optimizer: {
        web: {
          include: ['@vue', '@vueuse', 'vue-demi']
        }
      }
    }
  }
})
