// cypress/e2e/login.cy.ts

describe('Login Flow', () => {
    const username = `testuser_login_${Date.now()}`;
    const email = `testuser_login_${Date.now()}@example.com`;
    const password = 'Password123!';
    const invalidUsername = 'nonexistentuser';
    const invalidPassword = 'wrongpassword';

    before(() => {
        // Register a user once for login tests
        cy.register(username, email, password);
    });

    it('should validate the login form', () => {
        cy.visit('/login');
        cy.get('[data-cy=username-input]').should('exist');
        cy.get('[data-cy=password-input]').should('exist');
        cy.get('[data-cy=login-button]').should('exist');

        // Test mandatory fields
        cy.get('[data-cy=login-button]').click();
        cy.get('.invalid-feedback').should('be.visible').and('contain', 'Ce champ est obligatoire');
    });

    it('should handle invalid login credentials', () => {
        cy.intercept('POST', '**/api/auth/login').as('loginRequest');

        cy.visit('/login');

        // Fill out the login form with invalid credentials
        cy.get('[data-cy=username-input]').type(invalidUsername);
        cy.get('[data-cy=password-input]').type(invalidPassword);

        // Submit the form
        cy.get('[data-cy=login-button]').click();

        // Wait for API call to complete
        cy.wait('@loginRequest');

        // Error message should be visible
        cy.get('.alert-danger').should('be.visible');
    });

    it('should login with valid credentials', () => {
        cy.visit('/login');

        // Fill out the login form with valid credentials
        cy.get('[data-cy=username-input]').type(username);
        cy.get('[data-cy=password-input]').type(password);

        // Intercept login
        cy.intercept('POST', '**/api/auth/login').as('loginRequest');

        // Submit the form
        cy.get('[data-cy=login-button]').click();

        // Wait for login
        cy.wait('@loginRequest').its('response.statusCode').should('eq', 200);

        // Check if we are redirected to dashboard
        cy.url().should('include', '/dashboard');
        cy.get('h1').should('be.visible');
    });

    it('should logout correctly', () => {
        // First login
        cy.login(username, password);
        cy.visit('/dashboard');

        // Then logout
        cy.get('[data-cy=logout-button]').click();

        // Should be back to home page
        cy.url().should('eq', Cypress.config().baseUrl + '/');

        // Try to go back to dashboard - should be redirected to login
        cy.visit('/dashboard');
        cy.url().should('include', '/login');
    });

    it('should not allow access to dashboard when not logged in', () => {
        cy.visit('/dashboard');
        cy.url().should('include', '/login');
    });

    it('should navigate to password reset request page from login', () => {
        cy.visit('/login');
        cy.get('[data-cy=forgot-password-link]').click();
        cy.url().should('include', '/password-reset-request');
    });
});
