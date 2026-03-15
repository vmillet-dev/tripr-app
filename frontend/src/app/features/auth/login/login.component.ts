import {Component, inject, OnInit, signal} from '@angular/core';
import {form, FormField, required} from '@angular/forms/signals';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';
import {TranslocoPipe} from '@jsverse/transloco';
import {createAsyncAction} from '../../../core/utils/async-action.util';
import {LoginCredentials} from '../../../core/models/auth.model';
import {FormInputComponent} from "../../../core/components/form-input/form-input.component";
import {FormSubmitDirective} from "../../../core/directives/form-submit.directive";

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    imports: [RouterLink, TranslocoPipe, FormField, FormInputComponent, FormSubmitDirective]
})
export class LoginComponent implements OnInit {
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private authService = inject(AuthService);

    isRegistered = signal<boolean>(false);
    private returnUrl: string = '/';

    loginModel = signal<LoginCredentials>({
        username: '',
        password: ''
    });

    loginForm = form(this.loginModel, (fields) => {
        required(fields.username);
        required(fields.password);
    });

    loginAction = createAsyncAction(
        (credentials: LoginCredentials) => this.authService.login(credentials),
        {
            onSuccess: () => this.router.navigate([this.returnUrl]),
            defaultErrorMessage: 'Login failed'
        }
    );

    ngOnInit(): void {
        // Get return URL from route parameters or default to '/dashboard'
        this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
        // Check if user was redirected after registration
        this.isRegistered.set(this.route.snapshot.queryParams['registered'] === 'true');
    }

    onSubmit(): void {
        if (!this.loginForm().valid()) {
            return;
        }

        this.loginAction.execute(this.loginModel()).subscribe({
            next: (res) => this.loginAction.handleSuccess(res),
            error: (err) => this.loginAction.handleError(err)
        });
    }
}
