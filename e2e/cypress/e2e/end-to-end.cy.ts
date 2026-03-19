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
        cy.url().should('include', '/dashboard');
        cy.get('h1').should('be.visible');

        // 3. Logout
        cy.get('[data-cy=logout-button]').click();
        cy.url().should('eq', Cypress.config().baseUrl + '/');

        // 4. Request password reset
        cy.deleteAllEmails();
        cy.requestPasswordReset(username);
        cy.get('[data-cy=success-message]').should('be.visible');

        // 5. Extract token from Mailpit and reset password
        cy.getLastEmail(email).then((response) => {
            const body = response.body.HTML || response.body.Text;
            const match = body.match(/token=([a-zA-Z0-9\-_.]+)/);
            expect(match, 'Reset token should be in the email').to.not.be.null;
            const token = match[1];

            // Perform the actual reset
            cy.visit(`/password-reset?token=${token}`);

            // Wait for token validation
            cy.intercept('GET', '**/api/auth/password/validate-token*').as('validateToken');
            cy.wait('@validateToken').its('response.statusCode').should('eq', 200);

            cy.get('[data-cy=new-password-input]').type(newPassword);
            cy.get('[data-cy=confirm-password-input]').type(newPassword);

            cy.intercept('POST', '**/api/auth/password/reset').as('resetPassword');
            cy.get('[data-cy=reset-password-button]').click();
            cy.wait('@resetPassword').its('response.statusCode').should('eq', 200);

            // 6. Login with NEW password
            cy.login(username, newPassword);
            cy.url().should('include', '/dashboard');
            cy.get('h1').should('be.visible');

            // 7. Final Logout
            cy.get('[data-cy=logout-button]').click();
            cy.url().should('eq', Cypress.config().baseUrl + '/');
        });
    });
});
