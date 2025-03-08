// cypress/e2e/auth.cy.ts

describe('Authentication', () => {
  const username = `testuser_${Date.now()}`;
  const email = `testuser_${Date.now()}@example.com`;
  const password = 'Password123!';

  it('should register a new user', () => {
    // Intercept the register API call before visiting the page
    cy.intercept('POST', '**/api/auth/register').as('registerRequest');
    
    cy.visit('/register');
    
    // Fill out the registration form
    cy.get('[data-cy=username-input]').type(username);
    cy.get('[data-cy=email-input]').type(email);
    cy.get('[data-cy=password-input]').type(password);
    cy.get('[data-cy=confirm-password-input]').type(password);
    
    // Submit the form
    cy.get('[data-cy=register-button]').click();
    
    // Wait for API call to complete
    cy.wait('@registerRequest').its('response.statusCode').should('eq', 200);
    
    // Verify successful registration (redirected to login page with query parameter)
    cy.url().should('include', '/login');
    cy.url().should('include', 'registered=true');
    cy.get('[data-cy=success-message]').should('be.visible');
  });

  it('should login with newly created credentials', () => {
    // Intercept the login API call before visiting the page
    cy.intercept('POST', '**/api/auth/login').as('loginRequest');
    
    cy.visit('/login');
    
    // Fill out the login form
    cy.get('[data-cy=username-input]').type(username);
    cy.get('[data-cy=password-input]').type(password);
    
    // Submit the form
    cy.get('[data-cy=login-button]').click();
    
    // Wait for API call to complete
    cy.wait('@loginRequest').its('response.statusCode').should('eq', 200);
    
    // Verify successful login (redirected to dashboard)
    cy.url().should('include', '/dashboard');
    
    // Verify user is authenticated
    cy.get('[data-cy=user-menu]').should('contain', username);
  });
});
