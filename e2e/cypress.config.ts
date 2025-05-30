import {defineConfig} from "cypress";

export default defineConfig({
    e2e: {
        baseUrl: "http://localhost:8080",
        supportFile: "cypress/support/e2e.ts",
        specPattern: "cypress/e2e/**/*.cy.{js,jsx,ts,tsx}",
        pageLoadTimeout: 30000,
        defaultCommandTimeout: 20000,
        chromeWebSecurity: false,
        retries: {
            runMode: 5,
            openMode: 0,
        },
    },
});
