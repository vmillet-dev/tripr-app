import {Component, inject, signal} from '@angular/core';
import {email, form, FormField, minLength, required, validate} from '@angular/forms/signals';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';
import {TranslocoPipe} from '@jsverse/transloco';
import {createAsyncAction} from '../../../core/utils/async-action.util';
import {RegisterData} from '../../../core/models/auth.model';
import {FormInputComponent} from "../../../core/components/form-input/form-input.component";

interface RegisterModel extends RegisterData {
    confirmPassword: string;
}

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    imports: [RouterLink, TranslocoPipe, FormField, FormInputComponent]
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
        required(fields.username);

        required(fields.email);
        email(fields.email);

        required(fields.password);
        minLength(fields.password, 6);

        required(fields.confirmPassword);

        validate(fields.confirmPassword, ({value, valueOf}) => {
            return value() === valueOf(fields.password) ? null : {kind: 'passwordMismatch'};
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
