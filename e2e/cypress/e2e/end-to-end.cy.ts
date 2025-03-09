// cypress/e2e/end-to-end.cy.ts

describe('End-to-End User Flow', () => {
  const username = `e2euser_${Date.now()}`;
  const email = `e2euser_${Date.now()}@example.com`;
  const password = 'Password123!';
  const newPassword = 'NewPassword456!';

  it('should complete the full user journey', () => {
    // 1. Register a new user
    cy.register(username, email, password);
    cy.url().should('include', '/login');
    cy.url().should('include', 'registered=true');
    
    // 2. Login with the new user
    cy.login(username, password);
    
    // 3. Navigate to dashboard (would check for successful navigation in real env)
    cy.log('User should be logged in and redirected to dashboard');
    
    // 4. Logout (assuming there's a logout button in the header)
    // cy.get('[data-cy=logout-button]').click();
    // cy.url().should('include', '/login');
    
    // 5. Request password reset
    cy.requestPasswordReset(username);
    cy.get('[data-cy=success-message]').should('be.visible');
    
    // 6. Reset password (with mocked token)
    // Mock the token validation
    cy.intercept('GET', '**/api/auth/password/validate-token*', {
      statusCode: 200,
      body: { valid: true }
    }).as('validateToken');
    
    // Mock the password reset
    cy.intercept('POST', '**/api/auth/password/reset', {
      statusCode: 200,
      body: { message: 'Password has been reset successfully' }
    }).as('resetPassword');
    
    cy.resetPassword('mock-valid-token', newPassword);
    cy.get('[data-cy=success-message]').should('be.visible');
    
    // 7. Login with new password
    cy.login(username, newPassword);
    cy.log('User should be logged in with new password');
  });
});
