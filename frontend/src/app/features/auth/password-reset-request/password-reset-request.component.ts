import {Component, inject, signal} from '@angular/core';
import {form, FormField} from '@angular/forms/signals';
import {Router} from '@angular/router';
import {TranslocoModule} from '@jsverse/transloco';
import {AuthService} from "../../../core/services/auth.service";
import {createAsyncAction} from "../../../core/utils/async-action.util";

@Component({
    selector: 'app-password-reset-request',
    templateUrl: './password-reset-request.component.html',
    imports: [FormField, TranslocoModule]
})
export class PasswordResetRequestComponent {
    private authService = inject(AuthService);
    private router = inject(Router);

    successMessage = signal<string | null>(null);

    resetModel = signal({
        username: ''
    });

    resetForm = form(this.resetModel);

    resetAction = createAsyncAction(
        (username: string) => this.authService.requestPasswordReset({username}),
        {
            onSuccess: (response) => {
                this.successMessage.set(response.message);
                this.resetModel.set({username: ''});
            }
        }
    );

    onSubmit(): void {
        if (!this.resetForm().valid()) {
            return;
        }

        this.successMessage.set(null);
        this.resetAction.execute(this.resetModel().username).subscribe({
            next: (res) => this.resetAction.handleSuccess(res),
            error: (err) => this.resetAction.handleError(err)
        });
    }

    navigateToLogin(): void {
        this.router.navigate(['/login']);
    }
}
