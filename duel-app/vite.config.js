import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'


console.log('Vite Proxy Config Applied')

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
})
