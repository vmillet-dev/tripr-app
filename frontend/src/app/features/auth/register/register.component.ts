import {Component, inject, signal} from '@angular/core';
import {form, FormField, validate} from '@angular/forms/signals';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';
import {NgClass} from '@angular/common';
import {TranslocoPipe} from '@jsverse/transloco';
import {createAsyncAction} from '../../../core/utils/async-action.util';
import {RegisterData} from '../../../core/models/auth.model';

interface RegisterModel extends RegisterData {
    confirmPassword: string;
}

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    imports: [NgClass, RouterLink, TranslocoPipe, FormField]
})
export class RegisterComponent {
    private router = inject(Router);
    private authService = inject(AuthService);

    registerModel = signal<RegisterModel>({
        username: '',
        password: '',
        email: '',
        confirmPassword: ''
    });

    registerForm = form(this.registerModel, (fields) => {
        validate(fields, (ctx) => {
            const {password, confirmPassword} = ctx.value();
            return password === confirmPassword ? null : [{kind: 'passwordMismatch'}];
        });
    });

    registerAction = createAsyncAction(
        (data: RegisterData) => this.authService.register(data),
        {
            onSuccess: () => this.router.navigate(['/login'], {queryParams: {registered: true}}),
            defaultErrorMessage: 'Registration failed'
        }
    );

    onSubmit(): void {
        if (!this.registerForm().valid()) {
            return;
        }

        const {confirmPassword, ...data} = this.registerModel();

        this.registerAction.execute(data).subscribe({
            next: (res) => this.registerAction.handleSuccess(res),
            error: (err) => this.registerAction.handleError(err)
        });
    }
}
