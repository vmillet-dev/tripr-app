import {Component, inject, signal} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {TranslocoModule} from '@jsverse/transloco';
import {AuthService} from "../../../core/services/auth.service";

@Component({
    selector: 'app-password-reset-request',
    templateUrl: './password-reset-request.component.html',
    imports: [ReactiveFormsModule, TranslocoModule]
})
export class PasswordResetRequestComponent {
    private formBuilder = inject(FormBuilder);
    private authService = inject(AuthService);
    private router = inject(Router);

    isSubmitting = signal<boolean>(false);
    successMessage = signal<string | null>(null);
    errorMessage = signal<string | null>(null);

    resetForm = this.formBuilder.group({
        username: ['', [Validators.required]]
    });

    onSubmit(): void {
        if (this.resetForm.invalid) {
            return;
        }

        this.isSubmitting.set(true);
        this.errorMessage.set(null);
        this.successMessage.set(null);

        this.authService.requestPasswordReset(this.resetForm.value as any)
            .subscribe({
                next: (response) => {
                    this.isSubmitting.set(false);
                    this.successMessage.set(response.message);
                    this.resetForm.reset();
                },
                error: (error) => {
                    this.isSubmitting.set(false);
                    this.errorMessage.set(error.error?.message || 'An error occurred. Please try again.');
                }
            });
    }

    navigateToLogin(): void {
        this.router.navigate(['/login']);
    }
}
