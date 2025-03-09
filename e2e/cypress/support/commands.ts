// cypress/support/commands.ts

declare global {
  namespace Cypress {
    interface Chainable {
      register(username: string, email: string, password: string): Chainable<void>;
      login(username: string, password: string): Chainable<void>;
      requestPasswordReset(username: string): Chainable<void>;
      resetPassword(token: string, newPassword: string): Chainable<void>;
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

Cypress.Commands.add('requestPasswordReset', (username) => {
  cy.visit('/password-reset-request');
  cy.get('[data-cy=username-input]').type(username);
  
  // Intercept the password reset request API call
  cy.intercept('POST', '/api/auth/password/reset-request').as('resetRequest');
  cy.get('[data-cy=reset-request-button]').click();
  cy.wait('@resetRequest');
});

Cypress.Commands.add('resetPassword', (token, newPassword) => {
  cy.visit(`/password-reset?token=${token}`);
  
  // Intercept the token validation API call
  cy.intercept('GET', '/api/auth/password/validate-token*').as('validateToken');
  cy.wait('@validateToken');
  
  // Only proceed if token is valid
  cy.get('body').then(($body) => {
    if ($body.find('[data-cy=new-password-input]').length > 0) {
      cy.get('[data-cy=new-password-input]').type(newPassword);
      cy.get('[data-cy=confirm-password-input]').type(newPassword);
      
      // Intercept the password reset API call
      cy.intercept('POST', '/api/auth/password/reset').as('resetPassword');
      cy.get('[data-cy=reset-password-button]').click();
      cy.wait('@resetPassword');
    }
  });
});

export {};
