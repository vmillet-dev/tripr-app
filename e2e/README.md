# Tripr App E2E Testing

[![Build Status](https://github.com/vmillet-dev/tripr-app/workflows/Build%20and%20Test/badge.svg)](https://github.com/vmillet-dev/tripr-app/actions)
[![Cypress](https://img.shields.io/badge/Cypress-13.15.2-green.svg)](https://www.cypress.io/)
[![Docker](https://img.shields.io/badge/Docker-Isolated%20Environment-blue.svg)](https://www.docker.com/)

End-to-end testing suite for the Tripr App using Cypress with isolated Docker environment and comprehensive test coverage.

## Table of Contents

- [Testing Framework](#testing-framework)
- [Running Tests](#running-tests)
- [Test Setup](#test-setup)
- [Test Structure](#test-structure)
- [Page Object Model](#page-object-model)
- [Custom Commands](#custom-commands)
- [CI/CD Integration](#cicd-integration)
- [Test Data Management](#test-data-management)
- [Performance Testing](#performance-testing)
- [Troubleshooting](#troubleshooting)

## Testing Framework

### Framework
**Cypress 13.15.2** - Modern end-to-end testing framework with the following capabilities:
- **Real Browser Testing** - Tests run in actual Chrome, Firefox, and Edge browsers
- **Time Travel Debugging** - Step through test execution with snapshots
- **Automatic Waiting** - Built-in retry logic for dynamic content
- **Network Stubbing** - Mock API responses for isolated testing
- **Visual Testing** - Screenshot comparison and visual regression detection

### Test Organization
**Structured Test Architecture:**
- **Feature-Based Organization** - Tests grouped by application features
- **Shared Utilities** - Reusable functions and data generators
- **Environment Isolation** - Separate configurations for different environments
- **Parallel Execution** - Tests can run concurrently for faster feedback

### Page Object Model
**Implemented Page Object Pattern:**
- **Page Classes** - Encapsulate page-specific selectors and actions
- **Component Objects** - Reusable UI component interactions
- **Data Fixtures** - Centralized test data management
- **Action Abstractions** - High-level user action methods

## Running Tests

### Isolated Docker Environment

The recommended approach for running E2E tests is using the isolated Docker environment to ensure consistency and avoid conflicts with local development.

#### Prerequisites for Docker Environment

| Tool | Version | Purpose | Verification |
|------|---------|---------|--------------|
| **Docker** | 20+ | Container runtime | `docker --version` |
| **Docker Compose** | 2.0+ | Multi-container orchestration | `docker compose version` |
| **Node.js** | 22+ | Test runner dependencies | `node --version` |

#### Running Tests in Docker

```bash
# Navigate to e2e directory
cd e2e

# Start isolated test environment
docker compose -f docker-compose.test.yml up --build --abort-on-container-exit

# Run specific test suite in Docker
docker compose -f docker-compose.test.yml run --rm cypress-tests \
  npx cypress run --spec "cypress/e2e/auth.cy.ts"

# Clean up test environment
docker compose -f docker-compose.test.yml down -v
```

**Docker Test Environment Configuration:**

**File**: `e2e/docker-compose.test.yml`

```yaml
version: '3.8'

services:
  postgres-test:
    image: postgres:17.3
    environment:
      POSTGRES_DB: tripr_test
      POSTGRES_USER: test_user
      POSTGRES_PASSWORD: test_password
    ports:
      - "5434:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U test_user"]
      interval: 5s
      timeout: 5s
      retries: 5

  backend-test:
    build:
      context: ../
      dockerfile: devops/Dockerfile
    environment:
      SPRING_PROFILES_ACTIVE: test
      DB_HOST: postgres-test
      DB_PORT: 5432
      DB_NAME: tripr_test
      DB_USERNAME: test_user
      DB_PASSWORD: test_password
      JWT_SECRET: test-secret-key-for-e2e-testing-only
      BASE_URL: http://localhost:8081
    ports:
      - "8081:8081"
    depends_on:
      postgres-test:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
      interval: 10s
      timeout: 5s
      retries: 10

  frontend-test:
    build:
      context: ../
      dockerfile: devops/frontend.Dockerfile
    ports:
      - "4200:80"
    depends_on:
      - backend-test
    healthcheck:
      test: ["CMD", "wget", "--spider", "-q", "http://localhost:80/"]
      interval: 10s
      timeout: 5s
      retries: 5

  cypress-tests:
    build:
      context: .
      dockerfile: Dockerfile.cypress
    environment:
      CYPRESS_BASE_URL: http://frontend-test
      CYPRESS_API_URL: http://backend-test:8081
      CYPRESS_VIDEO: "true"
      CYPRESS_SCREENSHOTS: "true"
    volumes:
      - ./cypress/videos:/app/cypress/videos
      - ./cypress/screenshots:/app/cypress/screenshots
      - ./cypress/reports:/app/cypress/reports
    depends_on:
      frontend-test:
        condition: service_healthy
      backend-test:
        condition: service_healthy
    command: npx cypress run --browser chrome --headless
```

**Cypress Docker Image:**

**File**: `e2e/Dockerfile.cypress`

```dockerfile
FROM cypress/included:13.15.2

WORKDIR /app

# Copy package files
COPY package*.json ./
RUN npm ci --only=production

# Copy test files
COPY cypress/ cypress/
COPY cypress.config.ts ./

# Set up test reporting
RUN mkdir -p cypress/videos cypress/screenshots cypress/reports

# Health check for test readiness
HEALTHCHECK --interval=10s --timeout=5s --retries=3 \
  CMD npx cypress verify

CMD ["npx", "cypress", "run"]
```

### Local Development Environment

For rapid development and debugging, tests can be run against a local development environment.

#### Prerequisites for Local Development

```bash
# Ensure backend is running
cd backend
./gradlew bootRun

# Ensure frontend is running (in another terminal)
cd frontend
npm start

# Verify services are accessible
curl http://localhost:8081/actuator/health
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

#### Development Test Commands

```bash
# Run tests with live reload during development
npm run test:dev

# Run tests with debug output
DEBUG=cypress:* npm run test

# Run tests with custom configuration
npx cypress run --config baseUrl=http://localhost:3000

# Run tests with environment variables
CYPRESS_API_URL=http://localhost:8080 npm run test
```

### CI/CD Integration

Tests are automatically executed in the CI/CD pipeline on every pull request and merge to main branch.

#### GitHub Actions Integration

**File**: `.github/workflows/e2e-tests.yml`

```yaml
name: E2E Tests

on:
  pull_request:
    branches: [main, develop]
  push:
    branches: [main]

jobs:
  e2e-tests:
    runs-on: ubuntu-latest
    
    strategy:
      matrix:
        browser: [chrome, firefox, edge]
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
    
    - name: Set up Node.js
      uses: actions/setup-node@v4
      with:
        node-version: '22'
        cache: 'npm'
        cache-dependency-path: e2e/package-lock.json
    
    - name: Start test environment
      run: |
        cd e2e
        docker compose -f docker-compose.test.yml up -d
        
        # Wait for services to be ready
        timeout 300 bash -c 'until curl -f http://localhost:8081/actuator/health; do sleep 5; done'
        timeout 300 bash -c 'until curl -f http://localhost:4200; do sleep 5; done'
    
    - name: Install E2E dependencies
      run: |
        cd e2e
        npm ci
    
    - name: Run E2E tests
      run: |
        cd e2e
        npx cypress run --browser ${{ matrix.browser }} --record --key ${{ secrets.CYPRESS_RECORD_KEY }}
      env:
        CYPRESS_BASE_URL: http://localhost:4200
        CYPRESS_API_URL: http://localhost:8081
    
    - name: Upload test artifacts
      uses: actions/upload-artifact@v4
      if: failure()
      with:
        name: cypress-artifacts-${{ matrix.browser }}
        path: |
          e2e/cypress/videos/
          e2e/cypress/screenshots/
        retention-days: 7
    
    - name: Upload test results
      uses: actions/upload-artifact@v4
      if: always()
      with:
        name: cypress-results-${{ matrix.browser }}
        path: e2e/cypress/reports/
        retention-days: 30
    
    - name: Clean up test environment
      if: always()
      run: |
        cd e2e
        docker compose -f docker-compose.test.yml down -v
```

#### Pipeline Integration Commands

```bash
# Run E2E tests in CI mode
npm run test:ci

# Generate test reports for CI
npm run test:report

# Run tests with JUnit output
npx cypress run --reporter junit --reporter-options mochaFile=cypress/reports/results.xml

# Run tests with coverage collection
npx cypress run --env coverage=true
```

## Test Setup

### Prerequisites

#### Dependencies and Setup Requirements

**Core Dependencies:**
```json
{
  "devDependencies": {
    "cypress": "^13.15.2",
    "@cypress/code-coverage": "^3.12.0",
    "cypress-multi-reporters": "^1.6.4",
    "cypress-mochawesome-reporter": "^3.8.2",
    "cypress-real-events": "^1.12.0",
    "cypress-axe": "^1.5.0",
    "@testing-library/cypress": "^10.0.1"
  }
}
```

**Installation:**
```bash
cd e2e

# Install all dependencies
npm install

# Verify Cypress installation
npx cypress verify

# Open Cypress for first-time setup
npx cypress open
```

### Configuration

#### Environment-Specific Test Configurations

**Base Configuration** (`cypress.config.ts`):
```typescript
import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:4200',
    supportFile: 'cypress/support/e2e.ts',
    specPattern: 'cypress/e2e/**/*.cy.{js,jsx,ts,tsx}',
    viewportWidth: 1280,
    viewportHeight: 720,
    video: true,
    screenshotOnRunFailure: true,
    defaultCommandTimeout: 10000,
    requestTimeout: 10000,
    responseTimeout: 10000,
    pageLoadTimeout: 30000,
    
    env: {
      apiUrl: 'http://localhost:8081',
      coverage: false,
      hideXHRInCommandLog: true
    },
    
    setupNodeEvents(on, config) {
      // Code coverage plugin
      require('@cypress/code-coverage/task')(on, config);
      
      // Mochawesome reporter
      require('cypress-mochawesome-reporter/plugin')(on);
      
      // Custom tasks
      on('task', {
        log(message) {
          console.log(message);
          return null;
        },
        
        generateTestData() {
          return {
            email: `test-${Date.now()}@example.com`,
            password: 'TestPassword123!',
            firstName: 'Test',
            lastName: 'User'
          };
        }
      });
      
      return config;
    }
  },
  
  component: {
    devServer: {
      framework: 'angular',
      bundler: 'webpack'
    },
    specPattern: '**/*.component.cy.ts'
  }
});
```

**Environment Configurations:**

**Development** (`cypress.env.dev.json`):
```json
{
  "baseUrl": "http://localhost:4200",
  "apiUrl": "http://localhost:8081",
  "testUser": {
    "email": "dev-test@example.com",
    "password": "DevPassword123!"
  },
  "coverage": true,
  "retries": {
    "runMode": 2,
    "openMode": 0
  }
}
```

**Staging** (`cypress.env.staging.json`):
```json
{
  "baseUrl": "https://staging.tripr.example.com",
  "apiUrl": "https://staging-api.tripr.example.com",
  "testUser": {
    "email": "staging-test@example.com",
    "password": "StagingPassword123!"
  },
  "coverage": false,
  "retries": {
    "runMode": 3,
    "openMode": 1
  }
}
```

**Production** (`cypress.env.prod.json`):
```json
{
  "baseUrl": "https://tripr.example.com",
  "apiUrl": "https://api.tripr.example.com",
  "testUser": {
    "email": "prod-test@example.com",
    "password": "ProductionPassword123!"
  },
  "coverage": false,
  "retries": {
    "runMode": 5,
    "openMode": 0
  }
}
```

### Data Management

#### Test Data Setup and Cleanup

**Test Data Factory** (`cypress/support/data-factory.ts`):
```typescript
export class TestDataFactory {
  static generateUser(overrides: Partial<User> = {}): User {
    const timestamp = Date.now();
    return {
      email: `test-user-${timestamp}@example.com`,
      password: 'TestPassword123!',
      firstName: 'Test',
      lastName: 'User',
      ...overrides
    };
  }
  
  static generateUniqueEmail(): string {
    return `test-${Date.now()}-${Math.random().toString(36).substr(2, 9)}@example.com`;
  }
  
  static generateSecurePassword(): string {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*';
    let password = '';
    for (let i = 0; i < 12; i++) {
      password += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return password;
  }
}
```

**Database Seeding** (`cypress/support/database.ts`):
```typescript
export class DatabaseHelper {
  static seedTestData(): Cypress.Chainable<void> {
    return cy.task('db:seed', {
      users: [
        TestDataFactory.generateUser({ email: 'admin@test.com', role: 'ADMIN' }),
        TestDataFactory.generateUser({ email: 'user@test.com', role: 'USER' })
      ]
    });
  }
  
  static cleanupTestData(): Cypress.Chainable<void> {
    return cy.task('db:cleanup');
  }
  
  static resetDatabase(): Cypress.Chainable<void> {
    return cy.task('db:reset');
  }
}
```

**Test Hooks** (`cypress/support/hooks.ts`):
```typescript
beforeEach(() => {
  // Reset database state
  DatabaseHelper.resetDatabase();
  
  // Seed required test data
  DatabaseHelper.seedTestData();
  
  // Clear browser state
  cy.clearCookies();
  cy.clearLocalStorage();
  cy.window().then((win) => {
    win.sessionStorage.clear();
  });
});

afterEach(() => {
  // Cleanup test data
  DatabaseHelper.cleanupTestData();
  
  // Take screenshot on failure
  cy.screenshot({ capture: 'runner' });
});
```

## Test Structure

### Test Organization and Naming Conventions

**Directory Structure:**
```
cypress/
├── e2e/                          # End-to-end test specifications
│   ├── auth/                     # Authentication feature tests
│   │   ├── login.cy.ts           # Login functionality tests
│   │   ├── registration.cy.ts    # User registration tests
│   │   ├── password-reset.cy.ts  # Password reset workflow tests
│   │   └── logout.cy.ts          # Logout functionality tests
│   ├── dashboard/                # Dashboard feature tests
│   │   ├── user-profile.cy.ts    # User profile management tests
│   │   ├── settings.cy.ts        # User settings tests
│   │   └── navigation.cy.ts      # Dashboard navigation tests
│   ├── api/                      # API integration tests
│   │   ├── auth-api.cy.ts        # Authentication API tests
│   │   └── user-api.cy.ts        # User management API tests
│   └── accessibility/            # Accessibility tests
│       ├── a11y-auth.cy.ts       # Authentication accessibility
│       └── a11y-dashboard.cy.ts  # Dashboard accessibility
├── support/                      # Support files and utilities
│   ├── commands.ts               # Custom Cypress commands
│   ├── e2e.ts                    # Global configuration and imports
│   ├── page-objects/             # Page Object Model classes
│   │   ├── auth/                 # Authentication page objects
│   │   │   ├── login-page.ts     # Login page object
│   │   │   ├── register-page.ts  # Registration page object
│   │   │   └── reset-page.ts     # Password reset page object
│   │   ├── dashboard/            # Dashboard page objects
│   │   │   ├── dashboard-page.ts # Main dashboard page object
│   │   │   ├── profile-page.ts   # Profile page object
│   │   │   └── settings-page.ts  # Settings page object
│   │   └── common/               # Common page objects
│   │       ├── header.ts         # Header component object
│   │       ├── footer.ts         # Footer component object
│   │       └── navigation.ts     # Navigation component object
│   ├── fixtures/                 # Test data fixtures
│   │   ├── users.json            # User test data
│   │   ├── auth-responses.json   # Authentication API responses
│   │   └── error-messages.json   # Error message fixtures
│   └── utils/                    # Utility functions
│       ├── data-factory.ts       # Test data generation
│       ├── database.ts           # Database helper functions
│       ├── api-helpers.ts        # API testing utilities
│       └── accessibility.ts      # Accessibility testing utilities
├── downloads/                    # Downloaded files during tests
├── videos/                       # Test execution videos
├── screenshots/                  # Test failure screenshots
└── reports/                      # Test execution reports
```

### Shared Utilities and Fixtures

#### Custom Cypress Commands

**File**: `cypress/support/commands.ts`

```typescript
// Authentication Commands
Cypress.Commands.add('login', (email: string, password: string) => {
  cy.session([email, password], () => {
    cy.visit('/auth/login');
    cy.get('[data-cy=email-input]').type(email);
    cy.get('[data-cy=password-input]').type(password);
    cy.get('[data-cy=login-button]').click();
    cy.url().should('include', '/dashboard');
    cy.get('[data-cy=user-menu]').should('be.visible');
  });
});

Cypress.Commands.add('loginAsAdmin', () => {
  const adminUser = Cypress.env('adminUser');
  cy.login(adminUser.email, adminUser.password);
});

Cypress.Commands.add('loginAsUser', () => {
  const testUser = Cypress.env('testUser');
  cy.login(testUser.email, testUser.password);
});

Cypress.Commands.add('logout', () => {
  cy.get('[data-cy=user-menu]').click();
  cy.get('[data-cy=logout-button]').click();
  cy.url().should('eq', Cypress.config().baseUrl + '/');
});

// API Commands
Cypress.Commands.add('apiLogin', (email: string, password: string) => {
  return cy.request({
    method: 'POST',
    url: `${Cypress.env('apiUrl')}/api/auth/login`,
    body: { email, password }
  }).then((response) => {
    expect(response.status).to.eq(200);
    return response.body.accessToken;
  });
});

Cypress.Commands.add('apiRequest', (method: string, endpoint: string, body?: any) => {
  return cy.apiLogin(Cypress.env('testUser').email, Cypress.env('testUser').password)
    .then((token) => {
      return cy.request({
        method,
        url: `${Cypress.env('apiUrl')}${endpoint}`,
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body,
        failOnStatusCode: false
      });
    });
});

// UI Interaction Commands
Cypress.Commands.add('fillForm', (formData: Record<string, string>) => {
  Object.entries(formData).forEach(([field, value]) => {
    cy.get(`[data-cy=${field}-input]`).clear().type(value);
  });
});

Cypress.Commands.add('waitForSpinner', () => {
  cy.get('[data-cy=loading-spinner]').should('be.visible');
  cy.get('[data-cy=loading-spinner]').should('not.exist');
});

Cypress.Commands.add('checkAccessibility', () => {
  cy.injectAxe();
  cy.checkA11y(null, null, cy.terminalLog);
});

// Custom type definitions
declare global {
  namespace Cypress {
    interface Chainable {
      login(email: string, password: string): Chainable<void>;
      loginAsAdmin(): Chainable<void>;
      loginAsUser(): Chainable<void>;
      logout(): Chainable<void>;
      apiLogin(email: string, password: string): Chainable<string>;
      apiRequest(method: string, endpoint: string, body?: any): Chainable<Response>;
      fillForm(formData: Record<string, string>): Chainable<void>;
      waitForSpinner(): Chainable<void>;
      checkAccessibility(): Chainable<void>;
    }
  }
}
```

### Custom Commands and Helpers

#### Advanced Custom Commands

**File**: `cypress/support/advanced-commands.ts`

```typescript
// File Upload Command
Cypress.Commands.add('uploadFile', (selector: string, fileName: string, fileType: string = 'text/plain') => {
  cy.get(selector).then(subject => {
    cy.fixture(fileName, 'base64').then(content => {
      const el = subject[0] as HTMLInputElement;
      const blob = Cypress.Blob.base64StringToBlob(content, fileType);
      const file = new File([blob], fileName, { type: fileType });
      const dataTransfer = new DataTransfer();
      dataTransfer.items.add(file);
      el.files = dataTransfer.files;
      cy.wrap(subject).trigger('change', { force: true });
    });
  });
});

// Drag and Drop Command
Cypress.Commands.add('dragAndDrop', (sourceSelector: string, targetSelector: string) => {
  cy.get(sourceSelector).trigger('mousedown', { button: 0 });
  cy.get(targetSelector).trigger('mousemove').trigger('mouseup');
});

// Wait for Network Idle
Cypress.Commands.add('waitForNetworkIdle', (timeout: number = 5000) => {
  let requestCount = 0;
  
  cy.intercept('**', (req) => {
    requestCount++;
    req.continue((res) => {
      requestCount--;
    });
  });
  
  cy.waitUntil(() => requestCount === 0, { timeout });
});

// Visual Regression Testing
Cypress.Commands.add('compareSnapshot', (name: string) => {
  cy.screenshot(name);
  // Integration with visual testing service would go here
});

// Database Operations
Cypress.Commands.add('seedDatabase', (data: any) => {
  return cy.task('db:seed', data);
});

Cypress.Commands.add('queryDatabase', (query: string) => {
  return cy.task('db:query', query);
});
```

## Page Object Model

### Page Object Implementation

**Base Page Object** (`cypress/support/page-objects/base-page.ts`):
```typescript
export abstract class BasePage {
  protected url: string;
  
  constructor(url: string) {
    this.url = url;
  }
  
  visit(): void {
    cy.visit(this.url);
  }
  
  getTitle(): Cypress.Chainable<string> {
    return cy.title();
  }
  
  waitForPageLoad(): void {
    cy.get('[data-cy=page-loader]').should('not.exist');
  }
  
  checkAccessibility(): void {
    cy.checkAccessibility();
  }
  
  takeScreenshot(name?: string): void {
    cy.screenshot(name || this.constructor.name);
  }
}
```

**Login Page Object** (`cypress/support/page-objects/auth/login-page.ts`):
```typescript
import { BasePage } from '../base-page';

export class LoginPage extends BasePage {
  private selectors = {
    emailInput: '[data-cy=email-input]',
    passwordInput: '[data-cy=password-input]',
    loginButton: '[data-cy=login-button]',
    forgotPasswordLink: '[data-cy=forgot-password-link]',
    registerLink: '[data-cy=register-link]',
    errorMessage: '[data-cy=error-message]',
    loadingSpinner: '[data-cy=loading-spinner]'
  };
  
  constructor() {
    super('/auth/login');
  }
  
  enterEmail(email: string): LoginPage {
    cy.get(this.selectors.emailInput).clear().type(email);
    return this;
  }
  
  enterPassword(password: string): LoginPage {
    cy.get(this.selectors.passwordInput).clear().type(password);
    return this;
  }
  
  clickLogin(): void {
    cy.get(this.selectors.loginButton).click();
  }
  
  clickForgotPassword(): void {
    cy.get(this.selectors.forgotPasswordLink).click();
  }
  
  clickRegister(): void {
    cy.get(this.selectors.registerLink).click();
  }
  
  login(email: string, password: string): void {
    this.enterEmail(email)
        .enterPassword(password)
        .clickLogin();
  }
  
  verifyLoginSuccess(): void {
    cy.url().should('include', '/dashboard');
    cy.get('[data-cy=user-menu]').should('be.visible');
  }
  
  verifyLoginError(expectedMessage?: string): void {
    cy.get(this.selectors.errorMessage).should('be.visible');
    if (expectedMessage) {
      cy.get(this.selectors.errorMessage).should('contain.text', expectedMessage);
    }
  }
  
  verifyFormValidation(): void {
    cy.get(this.selectors.emailInput).should('have.class', 'is-invalid');
    cy.get(this.selectors.passwordInput).should('have.class', 'is-invalid');
  }
  
  waitForLoadingToComplete(): void {
    cy.get(this.selectors.loadingSpinner).should('not.exist');
  }
}
```

**Dashboard Page Object** (`cypress/support/page-objects/dashboard/dashboard-page.ts`):
```typescript
import { BasePage } from '../base-page';

export class DashboardPage extends BasePage {
  private selectors = {
    welcomeMessage: '[data-cy=welcome-message]',
    userMenu: '[data-cy=user-menu]',
    profileLink: '[data-cy=profile-link]',
    settingsLink: '[data-cy=settings-link]',
    logoutButton: '[data-cy=logout-button]',
    navigationMenu: '[data-cy=navigation-menu]',
    contentArea: '[data-cy=content-area]'
  };
  
  constructor() {
    super('/dashboard');
  }
  
  verifyWelcomeMessage(userName: string): void {
    cy.get(this.selectors.welcomeMessage)
      .should('be.visible')
      .and('contain.text', `Welcome, ${userName}`);
  }
  
  openUserMenu(): DashboardPage {
    cy.get(this.selectors.userMenu).click();
    return this;
  }
  
  navigateToProfile(): void {
    this.openUserMenu();
    cy.get(this.selectors.profileLink).click();
  }
  
  navigateToSettings(): void {
    this.openUserMenu();
    cy.get(this.selectors.settingsLink).click();
  }
  
  logout(): void {
    this.openUserMenu();
    cy.get(this.selectors.logoutButton).click();
  }
  
  verifyDashboardLoaded(): void {
    cy.get(this.selectors.contentArea).should('be.visible');
    cy.get(this.selectors.navigationMenu).should('be.visible');
  }
}
```

## Performance Testing

### Performance Monitoring

**Performance Test Example** (`cypress/e2e/performance/page-load.cy.ts`):
```typescript
describe('Performance Tests', () => {
  beforeEach(() => {
    // Enable performance monitoring
    cy.visit('/', {
      onBeforeLoad: (win) => {
        win.performance.mark('start');
      }
    });
  });
  
  it('should load the homepage within acceptable time', () => {
    cy.window().then((win) => {
      win.performance.mark('end');
      win.performance.measure('pageLoad', 'start', 'end');
      
      const measure = win.performance.getEntriesByName('pageLoad')[0];
      expect(measure.duration).to.be.lessThan(3000); // 3 seconds
    });
  });
  
  it('should have good Core Web Vitals', () => {
    cy.window().then((win) => {
      // Largest Contentful Paint (LCP)
      new PerformanceObserver((list) => {
        const entries = list.getEntries();
        const lastEntry = entries[entries.length - 1];
        expect(lastEntry.startTime).to.be.lessThan(2500); // 2.5 seconds
      }).observe({ entryTypes: ['largest-contentful-paint'] });
      
      // First Input Delay (FID) - simulated
      cy.get('button').first().click();
      cy.window().its('performance').invoke('now').should('be.lessThan', 100);
    });
  });
});
```

### Load Testing Integration

**Load Test Configuration** (`cypress/support/load-testing.ts`):
```typescript
export class LoadTestHelper {
  static simulateConcurrentUsers(userCount: number, testFunction: () => void): void {
    const promises = Array.from({ length: userCount }, (_, index) => {
      return new Promise((resolve) => {
        setTimeout(() => {
          testFunction();
          resolve(index);
        }, index * 100); // Stagger requests by 100ms
      });
    });
    
    Promise.all(promises);
  }
  
  static measureResponseTime(apiEndpoint: string): Cypress.Chainable<number> {
    const startTime = Date.now();
    
    return cy.request(apiEndpoint).then(() => {
      return Date.now() - startTime;
    });
  }
}
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

#### Docker Environment Issues

**Problem**: Docker containers fail to start or communicate

**Solution**:
```bash
# Check container logs
docker compose -f docker-compose.test.yml logs backend-test
docker compose -f docker-compose.test.yml logs frontend-test

# Verify network connectivity
docker compose -f docker-compose.test.yml exec cypress-tests ping backend-test
docker compose -f docker-compose.test.yml exec cypress-tests curl http://backend-test:8081/actuator/health

# Clean up and restart
docker compose -f docker-compose.test.yml down -v
docker system prune -f
docker compose -f docker-compose.test.yml up --build
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

// Use cy.session for authentication
cy.session('user', () => {
  cy.login('user@example.com', 'password');
});

// Parallelize tests
// cypress.config.ts
export default defineConfig({
  e2e: {
    experimentalRunAllSpecs: true
  }
});

// Run tests in parallel
npx cypress run --record --parallel --ci-build-id $CI_BUILD_ID
```

#### Memory Issues

**Problem**: Tests consume too much memory

**Solution**:
```bash
# Increase Node.js memory limit
export NODE_OPTIONS="--max-old-space-size=4096"

# Run tests in smaller batches
npx cypress run --spec "cypress/e2e/auth/*.cy.ts"
npx cypress run --spec "cypress/e2e/dashboard/*.cy.ts"

# Clean up between test runs
npx cypress run --config video=false,screenshotOnRunFailure=false
```

### CI/CD Integration Issues

#### GitHub Actions Failures

**Problem**: E2E tests fail in CI but pass locally

**Solution**:
```yaml
# .github/workflows/e2e-tests.yml
- name: Debug environment
  run: |
    echo "Node version: $(node --version)"
    echo "NPM version: $(npm --version)"
    echo "Cypress version: $(npx cypress --version)"
    docker ps
    curl -f http://localhost:8081/actuator/health || echo "Backend not ready"
    curl -f http://localhost:4200 || echo "Frontend not ready"

- name: Run E2E tests with debug
  run: |
    cd e2e
    DEBUG=cypress:* npx cypress run
  env:
    CYPRESS_BASE_URL: http://localhost:4200
    CYPRESS_API_URL: http://localhost:8081
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

**Built with ❤️ using Cypress and Docker. Happy testing! 🚀**
