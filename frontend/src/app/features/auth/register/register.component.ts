import {Component, inject, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';
import {NgClass} from '@angular/common';
import {TranslocoPipe} from '@jsverse/transloco';

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    imports: [ReactiveFormsModule, NgClass, RouterLink, TranslocoPipe]
})
export class RegisterComponent {
    private formBuilder = inject(FormBuilder);
    private router = inject(Router);
    private authService = inject(AuthService);

    error = signal<string | null>(null);
    loading = signal<boolean>(false);

    registerForm = this.formBuilder.group({
        username: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', Validators.required]
    }, {
        validators: this.passwordMatchValidator
    });

    get f() {
        return this.registerForm.controls;
    }

    passwordMatchValidator(formGroup: FormGroup) {
        const password = formGroup.get('password')?.value;
        const confirmPassword = formGroup.get('confirmPassword')?.value;

        if (password !== confirmPassword) {
            formGroup.get('confirmPassword')?.setErrors({passwordMismatch: true});
        } else {
            formGroup.get('confirmPassword')?.setErrors(null);
        }

        return null;
    }

    onSubmit(): void {
        if (this.registerForm.invalid) {
            return;
        }

        this.loading.set(true);
        this.error.set(null);

        this.authService.register({
            username: this.f['username'].value as string,
            password: this.f['password'].value as string,
            email: this.f['email'].value as string
        }).subscribe({
            next: () => {
                this.router.navigate(['/login'], {queryParams: {registered: true}});
            },
            error: error => {
                this.error.set(error.error?.message || 'Registration failed');
                this.loading.set(false);
            }
        });
    }
}
