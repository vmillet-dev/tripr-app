# Tripr App E2E Testing

End-to-end testing for the Tripr application using Cypress.

## Overview

This directory contains end-to-end tests that verify the integration between the Angular frontend and Spring Boot backend. The tests use Cypress to simulate user interactions and verify application behavior.

## Test Structure

```
cypress/
├── e2e/                  # Test specifications
│   ├── auth.cy.ts        # Authentication tests
│   ├── end-to-end.cy.ts  # Full user flow tests
│   └── password-reset.cy.ts # Password reset workflow tests
└── support/              # Support files
    ├── commands.ts       # Custom Cypress commands
    └── e2e.ts            # Global configuration
```

## Key Test Scenarios

- **Authentication**: Registration, login, and validation
- **Password Reset**: Request password reset, validate token, and reset password
- **End-to-End Flows**: Complete user journeys from registration to using core features

## Running Tests

### Prerequisites

- Node.js 18+
- npm 9+
- Backend and frontend running

### Installation

```bash
npm install
```

### Running All Tests

To run all tests headlessly:

```bash
npm run cypress:run
```

### Running Tests in Interactive Mode

To open the Cypress Test Runner:

```bash
npm run cypress:open
```

### Running Specific Tests

To run a specific test file:

```bash
npx cypress run --spec "cypress/e2e/auth.cy.ts"
```

## Automated Test Execution

The repository includes a script to start both the backend and frontend, run the tests, and then shut down the services:

```bash
./start-app.sh
```

This script:
1. Starts the backend with the dev profile
2. Waits for the backend to be ready
3. Starts the frontend
4. Waits for the frontend to be ready
5. Runs the Cypress tests
6. Shuts down both services

## Best Practices

- Use `data-cy` attributes for element selection
- Create custom commands for common operations
- Keep tests independent and idempotent
- Use dynamic data generation to avoid test conflicts
- Add proper waiting mechanisms for asynchronous operations
