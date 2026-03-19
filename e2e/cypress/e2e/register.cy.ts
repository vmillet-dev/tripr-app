// cypress/e2e/register.cy.ts

describe('Registration Flow', () => {
    const username = `testuser_${Date.now()}`;
    const email = `testuser_${Date.now()}@example.com`;
    const password = 'Password123!';

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

        // Test invalid email format
        cy.get('[data-cy=email-input]').type('invalid-email');
        cy.get('[data-cy=username-input]').click(); // trigger validation
        cy.get('.invalid-feedback').should('be.visible').and('contain', 'Veuillez entrer une adresse email valide');

        // Test password validation
        cy.get('[data-cy=username-input]').type(username);
        cy.get('[data-cy=email-input]').clear().type(email);
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
        // First, ensure the user exists (actually we reuse the one from the previous test if run sequentially,
        // but Cypress tests should ideally be independent. However, here we just use the same username/email)
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
});
