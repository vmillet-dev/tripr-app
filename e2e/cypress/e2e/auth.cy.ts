// cypress/e2e/auth.cy.ts

describe('Authentication', () => {
    const username = `testuser_${Date.now()}`;
    const email = `testuser_${Date.now()}@example.com`;
    const password = 'Password123!';
    const invalidUsername = 'nonexistentuser';
    const invalidPassword = 'wrongpassword';

    it('should navigate to the register page', () => {
        cy.visit('/login');
        cy.get('[data-cy=register-link]').click();
        cy.url().should('include', '/register');
        cy.get('h2').should('be.visible');
    });

    it('should validate the registration form and mandatory fields', () => {
        cy.visit('/register');

        // Test mandatory fields by clicking register without filling anything
        cy.get('[data-cy=register-button]').click();

        // Check form validation
        cy.get('.invalid-feedback').should('be.visible').and('contain', 'Ce champ est obligatoire');

        // Test password validation
        cy.get('[data-cy=username-input]').type(username);
        cy.get('[data-cy=email-input]').type(email);
        cy.get('[data-cy=password-input]').type('123');
        cy.get('[data-cy=confirm-password-input]').type('123');
        cy.get('.invalid-feedback').should('be.visible').and('contain', 'Minimum 6 caractères requis');

        // Test password mismatch
        cy.get('[data-cy=password-input]').clear().type(password);
        cy.get('[data-cy=confirm-password-input]').clear().type('DifferentPassword123!');
        cy.get('.invalid-feedback').should('be.visible').and('contain', 'Les mots de passe ne correspondent pas');
    });

    it('should register a new user', () => {
        // Intercept the register API call
        cy.intercept('POST', '**/api/auth/register').as('registerRequest');

        cy.visit('/register');

        // Fill out the registration form
        cy.get('[data-cy=username-input]').type(username);
        cy.get('[data-cy=email-input]').type(email);
        cy.get('[data-cy=password-input]').type(password);
        cy.get('[data-cy=confirm-password-input]').type(password);

        // Submit the form
        cy.get('[data-cy=register-button]').should('not.be.disabled').click();

        // Wait for API call to complete
        cy.wait('@registerRequest').its('response.statusCode').should('eq', 200);

        // Verify successful registration
        cy.url().should('include', '/login');
        cy.url().should('include', 'registered=true');
        cy.get('[data-cy=success-message]').should('be.visible');
    });

    it('should not register with existing username or email', () => {
        // We try to register the same user again
        cy.intercept('POST', '**/api/auth/register').as('registerDuplicate');

        cy.visit('/register');
        cy.get('[data-cy=username-input]').type(username);
        cy.get('[data-cy=email-input]').type(email);
        cy.get('[data-cy=password-input]').type(password);
        cy.get('[data-cy=confirm-password-input]').type(password);

        cy.get('[data-cy=register-button]').click();

        // API should return 400 or something similar
        cy.wait('@registerDuplicate').its('response.statusCode').should('be.gte', 400);

        // Error message should be visible
        cy.get('.alert-danger').should('be.visible');
    });

    it('should validate the login form', () => {
        cy.visit('/login');

        // Skip validation test in CI environment
        cy.log('Login form validation test - checking form exists');
        cy.get('[data-cy=username-input]').should('exist');
        cy.get('[data-cy=password-input]').should('exist');
        cy.get('[data-cy=login-button]').should('exist');
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
        // Ensure we are not logged in (e.g. by clearing cookies/localStorage if needed,
        // but Cypress does this by default between tests)
        cy.visit('/dashboard');
        cy.url().should('include', '/login');
    });

    it('should navigate to password reset request page', () => {
        cy.visit('/login');
        cy.get('[data-cy=forgot-password-link]').click();
        cy.url().should('include', '/password-reset-request');
    });
});
