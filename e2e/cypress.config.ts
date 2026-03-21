import {defineConfig} from "cypress";

export default defineConfig({
    e2e: {
        baseUrl: "http://localhost:8081",
        supportFile: "cypress/support/e2e.ts",
        specPattern: "cypress/e2e/**/*.cy.{js,jsx,ts,tsx}",
        pageLoadTimeout: 20000,
        defaultCommandTimeout: 20000,
        chromeWebSecurity: false,
        retries: {
            runMode: 1,
            openMode: 0,
        },
    },
});
