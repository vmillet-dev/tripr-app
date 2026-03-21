// cypress/e2e/password-reset.cy.ts

describe('Password Reset Workflow', () => {
    const username = `testuser_${Date.now()}`;
    const email = `testuser_${Date.now()}@example.com`;
    const password = 'Password123!';
    const newPassword = 'NewPassword456!';

    before(() => {
        // Register a new user first
        cy.register(username, email, password);
    });

    it('should navigate to password reset request page from login', () => {
        cy.visit('/login');
        cy.get('[data-cy=forgot-password-link]').click();
        cy.url().should('include', '/password-reset-request');
    });

    it('should show validation error for empty username in reset request', () => {
        cy.visit('/password-reset-request');
        cy.get('[data-cy=reset-request-button]').click();
        cy.get('.invalid-feedback').should('be.visible')
    });

    it('should validate password reset form fields', () => {
        // Use a dummy token to show the form
        cy.intercept('GET', '**/api/auth/password/validate-token*', {
            statusCode: 200,
            body: {valid: true}
        }).as('validateTokenMock');

        cy.visit('/password-reset?token=dummy-token');
        cy.wait('@validateTokenMock');

        // Test mandatory fields
        cy.get('[data-cy=reset-password-button]').click();
        cy.get('.invalid-feedback').should('be.visible');

        // Test password length
        cy.get('[data-cy=new-password-input]').type('123');
        cy.get('[data-cy=confirm-password-input]').type('123');
        cy.get('.invalid-feedback').should('be.visible');

        // Test password mismatch
        cy.get('[data-cy=new-password-input]').clear().type('Password123!');
        cy.get('[data-cy=confirm-password-input]').clear().type('DifferentPassword123!');
        cy.get('.invalid-feedback').should('be.visible');
    });

    it('should complete full password reset flow via Mailpit', () => {
        // 1. Clear previous emails and request a reset
        cy.deleteAllEmails();
        cy.visit('/password-reset-request');
        cy.get('[data-cy=username-input]').type(username);

        cy.intercept('POST', '**/api/auth/password/reset-request').as('resetRequest');
        cy.get('[data-cy=reset-request-button]').click();

        // The API returns 200 even if user doesn't exist for security reasons
        cy.wait('@resetRequest').its('response.statusCode').should('eq', 200);
        cy.get('[data-cy=success-message]').should('be.visible');

        // 2. Fetch email and extract token
        cy.getLastEmail(email).then((response) => {
            const body = response.body.HTML || response.body.Text;
            const tokenMatch = body.match(/token=([a-zA-Z0-9\-_.]+)/);
            expect(tokenMatch, 'Token should be present in the email').to.not.be.null;
            const token = tokenMatch[1];

            cy.log(`Extracted token: ${token}`);

            // 3. Navigate to reset page with token
            cy.visit(`/password-reset?token=${token}`);

            // Wait for token validation
            cy.intercept('GET', '**/api/auth/password/validate-token*').as('validateToken');
            cy.wait('@validateToken').its('response.statusCode').should('eq', 200);

            // 4. Fill in new password
            cy.get('[data-cy=new-password-input]').type(newPassword);
            cy.get('[data-cy=confirm-password-input]').type(newPassword);

            cy.intercept('POST', '**/api/auth/password/reset').as('resetPassword');
            cy.get('[data-cy=reset-password-button]').click();

            cy.wait('@resetPassword').its('response.statusCode').should('eq', 200);

            // 5. Should be redirected to login and show success message
            cy.url().should('include', '/login');
            cy.get('[data-cy=success-message]').should('be.visible');

            // 6. Verify login with NEW password
            cy.login(username, newPassword);
            cy.url().should('include', '/dashboard');
        });
    });

    it('should handle invalid or expired token', () => {
        // The API returns 200 with isValid: false
        cy.visit('/password-reset?token=invalid-token-123');

        cy.intercept('GET', '**/api/auth/password/validate-token*').as('validateToken');
        cy.wait('@validateToken').its('response.body.valid').should('be.false');

        cy.get('[data-cy=token-invalid]').should('be.visible');
        cy.get('[data-cy=back-to-login]').click();
        cy.url().should('include', '/password-reset-request');
    });
});
