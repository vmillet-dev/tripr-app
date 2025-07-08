# Tripr App E2E Testing

[![Build Status](https://github.com/vmillet-dev/tripr-app/workflows/Build%20and%20Test/badge.svg)](https://github.com/vmillet-dev/tripr-app/actions)
[![Cypress](https://img.shields.io/badge/Cypress-13.15.2-green.svg)](https://www.cypress.io/)
[![Docker](https://img.shields.io/badge/Docker-Isolated%20Environment-blue.svg)](https://www.docker.com/)

End-to-end testing suite for the Tripr App using Cypress with isolated Docker environment and comprehensive test coverage.

## Table of Contents

- [Running Tests](#running-tests)
- [Troubleshooting](#troubleshooting)

## Running Tests

### Isolated Docker Environment

The recommended approach for running E2E tests is using the isolated Docker environment to ensure consistency and avoid conflicts with local development.

#### Prerequisites for Docker Environment

| Tool               | Version | Purpose                       | Verification             |
|--------------------|---------|-------------------------------|--------------------------|
| **Docker**         | 20+     | Container runtime             | `docker --version`       |
| **Docker Compose** | 2.0+    | Multi-container orchestration | `docker compose version` |
| **Node.js**        | 22+     | Test runner dependencies      | `node --version`         |

#### Running Tests in Docker

```bash
# Navigate to e2e directory
cd e2e

# Start isolated test environment
cypress:run:docker
```

### Local Development Environment

For rapid development and debugging, tests can be run against a local development environment.

```bash
# Navigate to e2e directory
cd e2e
cypress:run
```

#### Prerequisites for Local Development

```bash
# Ensure backend is running
cd backend
./gradlew bootRun

# Ensure frontend is running (in another terminal)
cd frontend
npm start

# Verify services are accessible
curl http://localhost:4200
```

#### Running Tests Locally

```bash
# Navigate to e2e directory
cd e2e

# Install dependencies
npm install

# Run all tests headlessly
npm run test
# or
npx cypress run

# Run tests in interactive mode
npm run test:open
# or
npx cypress open

# Run specific test file
npx cypress run --spec "cypress/e2e/auth.cy.ts"

# Run tests with specific browser
npx cypress run --browser chrome
npx cypress run --browser firefox
npx cypress run --browser edge
```

## Troubleshooting

### Common Test Issues

#### Cypress Installation Problems

**Problem**: Cypress fails to install or verify

**Solution**:
```bash
# Clear npm cache
npm cache clean --force

# Remove node_modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Verify Cypress installation
npx cypress verify

# Install Cypress binary manually if needed
npx cypress install
```

#### Test Flakiness Issues

**Problem**: Tests pass sometimes but fail other times

**Solution**:
```typescript
// Add proper waits instead of fixed delays
cy.get('[data-cy=submit-button]').should('be.enabled').click();

// Use retry logic for unstable elements
cy.get('[data-cy=dynamic-content]', { timeout: 10000 }).should('be.visible');

// Wait for network requests to complete
cy.intercept('POST', '/api/auth/login').as('loginRequest');
cy.get('[data-cy=login-button]').click();
cy.wait('@loginRequest');

// Use cy.session for authentication state
cy.session('user-session', () => {
  cy.login('user@example.com', 'password');
});
```

#### Browser Compatibility Issues

**Problem**: Tests fail in specific browsers

**Solution**:
```bash
# Test in different browsers
npx cypress run --browser chrome
npx cypress run --browser firefox
npx cypress run --browser edge

# Use browser-specific configurations
# cypress.config.ts
export default defineConfig({
  e2e: {
    setupNodeEvents(on, config) {
      if (config.browser?.name === 'firefox') {
        config.defaultCommandTimeout = 15000;
      }
      return config;
    }
  }
});
```

### Performance Issues

#### Slow Test Execution

**Problem**: Tests take too long to run

**Solution**:
```typescript
// Optimize selectors
cy.get('[data-cy=specific-element]'); // Good
cy.get('.complex-css-selector:nth-child(3)'); // Avoid

```

### Getting Help

**Testing Resources:**
- [Cypress Documentation](https://docs.cypress.io/)
- [Cypress Best Practices](https://docs.cypress.io/guides/references/best-practices)
- [Testing Library Cypress](https://testing-library.com/docs/cypress-testing-library/intro/)

**Community Support:**
- [Cypress Discord](https://discord.gg/cypress)
- [Cypress GitHub Discussions](https://github.com/cypress-io/cypress/discussions)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/cypress)

**Debugging Tools:**
- [Cypress Debug](https://docs.cypress.io/guides/guides/debugging)
- [Chrome DevTools](https://developer.chrome.com/docs/devtools/)
- [Cypress Dashboard](https://dashboard.cypress.io/)

---

**Built with ❤️ using Cypress and Docker.**
