import {Component, inject, signal} from '@angular/core';
import {form, FormField, minLength, required, validate} from '@angular/forms/signals';
import {ActivatedRoute, Router} from '@angular/router';
import {toSignal} from '@angular/core/rxjs-interop';
import {TranslocoPipe} from '@jsverse/transloco';
import {map} from 'rxjs';
import {AuthService} from "../../../core/services/auth.service";
import {createAsyncAction} from "../../../core/utils/async-action.util";
import {FormInputComponent} from "../../../core/components/form-input/form-input.component";

@Component({
    selector: 'app-password-reset',
    templateUrl: './password-reset.component.html',
    standalone: true,
    imports: [FormField, TranslocoPipe, FormInputComponent]
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
        required(fields.newPassword);
        minLength(fields.newPassword, 8);
        required(fields.confirmPassword);

        validate(fields.confirmPassword, ({value, valueOf}) => {
            return value() === valueOf(fields.newPassword) ? null : {kind: 'passwordMismatch'};
        });
    });

    submitted = signal(false);

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
        this.submitted.set(true);

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
