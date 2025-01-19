module.exports = {
  root: true,
  env: {
    browser: true,
    node: true,
    'vue/setup-compiler-macros': true
  },
  extends: [
    '@nuxt/eslint-config',
    'plugin:vue/vue3-recommended',
    'plugin:@typescript-eslint/recommended',
    'prettier'
  ],
  parser: 'vue-eslint-parser',
  parserOptions: {
    parser: '@typescript-eslint/parser',
    ecmaVersion: 2021,
    sourceType: 'module'
  },
  plugins: [
    'vue',
    '@typescript-eslint'
  ],
  rules: {
    // Vue-specific rules
    'vue/multi-word-component-names': 'off',
    'vue/no-multiple-template-root': 'off',
    'vue/component-name-in-template-casing': ['error', 'PascalCase'],
    
    // TypeScript rules
    '@typescript-eslint/no-explicit-any': 'warn',
    '@typescript-eslint/explicit-module-boundary-types': 'off',
    '@typescript-eslint/ban-ts-comment': 'warn',
    
    // General rules
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    
    // Accessibility rules
    'vuejs-accessibility/label-has-for': ['error', {
      components: ['Label'],
      required: {
        some: ['nesting', 'id']
      }
    }]
  },
  settings: {
    'import/resolver': {
      typescript: {
        project: './tsconfig.json'
      }
    }
  }
}
