import {Component, inject, signal} from '@angular/core';
import {form, FormField, validate} from '@angular/forms/signals';
import {ActivatedRoute, Router} from '@angular/router';
import {toSignal} from '@angular/core/rxjs-interop';
import {TranslocoModule} from '@jsverse/transloco';
import {map} from 'rxjs';
import {AuthService} from "../../../core/services/auth.service";
import {createAsyncAction} from "../../../core/utils/async-action.util";

@Component({
    selector: 'app-password-reset',
    templateUrl: './password-reset.component.html',
    standalone: true,
    imports: [FormField, TranslocoModule]
})
export class PasswordResetComponent {
    private authService = inject(AuthService);
    private route = inject(ActivatedRoute);
    private router = inject(Router);

    isTokenValid = signal(false);
    successMessage = signal<string | null>(null);

    private token$ = this.route.queryParams.pipe(map(params => params['token'] || ''));
    token = toSignal(this.token$, {initialValue: ''});

    validateAction = createAsyncAction(
        (token: string) => this.authService.validatePasswordResetToken(token),
        {
            onSuccess: (response) => {
                this.isTokenValid.set(response.valid);
            },
            onError: () => {
                this.isTokenValid.set(false);
            },
            defaultErrorMessage: 'Invalid or missing token. Please request a new password reset link.'
        }
    );

    resetModel = signal({
        newPassword: '',
        confirmPassword: ''
    });

    resetForm = form(this.resetModel, (fields) => {
        validate(fields, (ctx) => {
            const {newPassword, confirmPassword} = ctx.value();
            return newPassword === confirmPassword ? null : [{kind: 'passwordMismatch'}];
        });
    });

    resetAction = createAsyncAction(
        (data: any) => this.authService.resetPassword(data),
        {
            onSuccess: (response) => {
                this.successMessage.set(response.message);
                setTimeout(() => {
                    this.router.navigate(['/login'], {queryParams: {resetSuccess: true}});
                }, 3000);
            }
        }
    );

    constructor() {
        const currentToken = this.token();
        if (currentToken) {
            this.validateAction.execute(currentToken).subscribe({
                next: (res) => this.validateAction.handleSuccess(res),
                error: (err) => this.validateAction.handleError(err)
            });
        } else {
            this.validateAction.handleError(new Error('Missing token'));
        }
    }

    onSubmit(): void {
        if (!this.resetForm().valid()) return;

        this.successMessage.set('');

        const data = {
            token: this.token(),
            newPassword: this.resetModel().newPassword
        };

        this.resetAction.execute(data).subscribe({
            next: (res) => this.resetAction.handleSuccess(res),
            error: (err) => this.resetAction.handleError(err)
        });
    }

    navigateToLogin(): void {
        this.router.navigate(['/login']);
    }
}
