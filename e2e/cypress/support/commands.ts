// cypress/support/commands.ts

declare global {
  namespace Cypress {
    interface Chainable {
      register(username: string, email: string, password: string): Chainable<void>;
      login(username: string, password: string): Chainable<void>;
    }
  }
}

Cypress.Commands.add('register', (username, email, password) => {
  cy.visit('/register');
  cy.get('[data-cy=username-input]').type(username);
  cy.get('[data-cy=email-input]').type(email);
  cy.get('[data-cy=password-input]').type(password);
  cy.get('[data-cy=confirm-password-input]').type(password);
  
  // Intercept the register API call
  cy.intercept('POST', '/api/auth/register').as('registerRequest');
  cy.get('[data-cy=register-button]').click();
  cy.wait('@registerRequest');
});

Cypress.Commands.add('login', (username, password) => {
  cy.visit('/login');
  cy.get('[data-cy=username-input]').type(username);
  cy.get('[data-cy=password-input]').type(password);
  
  // Intercept the login API call
  cy.intercept('POST', '/api/auth/login').as('loginRequest');
  cy.get('[data-cy=login-button]').click();
  cy.wait('@loginRequest');
});

export {};
