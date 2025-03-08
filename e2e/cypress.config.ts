import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:4200',
    supportFile: 'cypress/support/e2e.ts',
    specPattern: 'cypress/e2e/**/*.cy.{js,jsx,ts,tsx}',
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
    // Increase timeouts for CI environment
    pageLoadTimeout: 30000,
    defaultCommandTimeout: 20000,
    // Disable web security to allow cross-origin requests
    chromeWebSecurity: false,
    // Add retries for more stability in CI
    retries: {
      runMode: 2,
      openMode: 0
    }
  },
  env: {
    apiUrl: 'http://localhost:8081/api',
  },
});
