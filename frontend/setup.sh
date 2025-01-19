#!/bin/bash

# Nuxt.js Project Setup Script for Quisin

# Ensure we're using the latest Node.js
echo "Checking Node.js version..."
node --version

# Create Nuxt.js project with TypeScript
echo "Creating Nuxt.js project..."
npx nuxi init quisin-frontend

# Navigate to project directory
cd quisin-frontend

# Install core dependencies
echo "Installing dependencies..."
npm install

# Install additional development dependencies
npm install -D \
  @types/node \
  @typescript-eslint/eslint-plugin \
  @typescript-eslint/parser \
  eslint \
  eslint-config-prettier \
  eslint-plugin-nuxt \
  eslint-plugin-vue \
  prettier \
  tailwindcss \
  postcss \
  autoprefixer \
  pinia \
  vitest \
  @pinia/nuxt \
  @vueuse/nuxt

# Setup Tailwind CSS
npx tailwindcss init

# Configure TypeScript
cat > tsconfig.json << EOL
{
  "extends": "./.nuxt/tsconfig.json",
  "compilerOptions": {
    "strict": true,
    "esModuleInterop": true,
    "skipLibCheck": true,
    "forceConsistentCasingInFileNames": true
  }
}
EOL

# Create ESLint configuration
cat > .eslintrc.js << EOL
module.exports = {
  root: true,
  env: {
    browser: true,
    node: true
  },
  extends: [
    '@nuxtjs/eslint-config-typescript',
    'plugin:nuxt/recommended',
    'prettier'
  ],
  rules: {
    'vue/multi-word-component-names': 'off'
  }
}
EOL

# Create Prettier configuration
cat > .prettierrc << EOL
{
  "semi": false,
  "singleQuote": true,
  "trailingComma": "none"
}
EOL

# Update package.json scripts
npm pkg set scripts.lint="eslint ."
npm pkg set scripts.format="prettier --write ."
npm pkg set scripts.test="vitest"

# Initialize git
git init
git add .
git commit -m "Initial commit: Nuxt.js project setup"

echo "Quisin frontend project setup complete!"
echo "Next steps:"
echo "1. cd quisin-frontend"
echo "2. npm run dev"
