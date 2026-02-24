import {Component, inject, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {toSignal} from '@angular/core/rxjs-interop';
import {TranslocoModule} from '@jsverse/transloco';
import {map} from 'rxjs';
import {AuthService} from "../../../core/services/auth.service";

@Component({
    selector: 'app-password-reset',
    templateUrl: './password-reset.component.html',
    styleUrls: ['./password-reset.component.scss'],
    standalone: true,
    imports: [ReactiveFormsModule, TranslocoModule]
})
export class PasswordResetComponent {
    private fb = inject(FormBuilder);
    private authService = inject(AuthService);
    private route = inject(ActivatedRoute);
    private router = inject(Router);

    isSubmitting = signal(false);
    isValidatingToken = signal(true);
    isTokenValid = signal(false);
    successMessage = signal<string | null>(null);
    errorMessage = signal<string | null>(null);

    private token$ = this.route.queryParams.pipe(map(params => params['token'] || ''));
    token = toSignal(this.token$, {initialValue: ''});

    resetForm = this.fb.group({
        newPassword: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', [Validators.required]]
    }, {
        validators: this.passwordMatchValidator
    });

    constructor() {
        const currentToken = this.token();
        if (currentToken) {
            this.validateToken(currentToken);
        } else {
            this.isValidatingToken.set(false);
            this.isTokenValid.set(false);
            this.errorMessage.set('Invalid or missing token. Please request a new password reset link.');
        }
    }

    validateToken(token: string): void {
        this.authService.validatePasswordResetToken(token).subscribe({
            next: (response) => {
                this.isValidatingToken.set(false);
                this.isTokenValid.set(response.valid);
                if (!response.valid) {
                    this.errorMessage.set('This password reset link has expired or is invalid. Please request a new one.');
                }
            },
            error: () => {
                this.isValidatingToken.set(false);
                this.isTokenValid.set(false);
                this.errorMessage.set('Failed to validate token. Please request a new password reset link.');
            }
        });
    }

    passwordMatchValidator(group: FormGroup) {
        const password = group.get('newPassword')?.value;
        const confirmPassword = group.get('confirmPassword')?.value;
        return password === confirmPassword ? null : {passwordMismatch: true};
    }

    onSubmit(): void {
        if (this.resetForm.invalid) return;

        this.isSubmitting.set(true);
        this.errorMessage.set('');
        this.successMessage.set('');

        this.authService.resetPassword({
            token: this.token(),
            newPassword: this.resetForm.get('newPassword')?.value
        }).subscribe({
            next: (response) => {
                this.isSubmitting.set(false);
                this.successMessage.set(response.message);
                setTimeout(() => {
                    this.router.navigate(['/login'], {queryParams: {resetSuccess: true}});
                }, 3000);
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
