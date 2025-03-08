import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:4200',
    supportFile: 'cypress/support/e2e.ts',
    specPattern: 'cypress/e2e/**/*.cy.{js,jsx,ts,tsx}',
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
    // Increase timeout for page loads and assertions
    pageLoadTimeout: 10000,
    defaultCommandTimeout: 10000,
    // Disable web security to allow cross-origin requests
    chromeWebSecurity: false
  },
  env: {
    apiUrl: 'http://localhost:8081/api',
  },
});
