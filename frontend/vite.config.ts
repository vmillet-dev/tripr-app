/// <reference types="vitest/config" />
import {defineConfig} from 'vite';
import angular from '@analogjs/vite-plugin-angular';

export default defineConfig({
    plugins: [angular()],
    server: {
        port: 4200,
        open: true,
        proxy: {
            '/api': {
                target: 'http://localhost:8081',
                secure: false,
                changeOrigin: true
            }
        }
    },
    build: {
        outDir: 'dist',
        target: 'es2022',
    },
    resolve: {
        mainFields: ['module'],
    },
    publicDir: 'src/assets',
    test: {
        globals: true,
        environment: 'jsdom',
        setupFiles: ['src/test-setup.ts'],
        include: ['src/**/*.{test,spec}.{js,mjs,cjs,ts,mts,cts,jsx,tsx}'],
        reporters: ['default'],
        coverage: {
            include: ['packages/**/src/**.{js,jsx,ts,tsx}'],
        }
    }
});
