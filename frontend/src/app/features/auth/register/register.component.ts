import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NgClass } from '@angular/common';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  imports: [ReactiveFormsModule, NgClass, RouterLink, TranslocoPipe]
})
export class RegisterComponent {
  loading = false;
  error = '';

  private formBuilder = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthService);

  registerForm = this.formBuilder.group({
    username: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', Validators.required]
  }, {
    validators: this.passwordMatchValidator
  });



  get formControls() { return this.registerForm.controls; }

  passwordMatchValidator(formGroup: FormGroup) {
    const password = formGroup.get('password')?.value;
    const confirmPassword = formGroup.get('confirmPassword')?.value;

    if (password !== confirmPassword) {
      formGroup.get('confirmPassword')?.setErrors({ passwordMismatch: true });
    } else {
      formGroup.get('confirmPassword')?.setErrors(null);
    }

    return null;
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = '';

    this.authService.register({
      username: this.formControls['username'].value as string,
      password: this.formControls['password'].value as string,
      email: this.formControls['email'].value as string
    }).subscribe({
      next: () => {
        this.router.navigate(['/login'], { queryParams: { registered: true } });
      },
      error: error => {
        this.error = error.error?.message || 'Registration failed';
        this.loading = false;
      }
    });
  }
}
