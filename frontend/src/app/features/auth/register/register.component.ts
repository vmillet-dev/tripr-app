import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';
import { NgClass } from '@angular/common';
import { TranslocoPipe } from '@jsverse/transloco';
import { RegisterRequest } from '../../../core/models/auth.model';
import { passwordMatchValidator } from '../../../shared/validators/password-match.validator';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  imports: [ReactiveFormsModule, NgClass, RouterLink, TranslocoPipe]
})
export class RegisterComponent implements OnInit {
  loading = false;
  error = '';
  registerForm: FormGroup;

  private readonly formBuilder = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);

  constructor() {
    this.registerForm = this.formBuilder.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, {
      validators: passwordMatchValidator()
    });
  }

  ngOnInit(): void {}

  get f() { 
    return this.registerForm.controls; 
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = '';

    const registerData: RegisterRequest = {
      username: this.f['username'].value as string,
      password: this.f['password'].value as string,
      email: this.f['email'].value as string
    };

    this.authService.register(registerData).subscribe({
      next: () => {
        this.router.navigate(['/login'], { queryParams: { registered: true } });
      },
      error: (error: HttpErrorResponse) => {
        this.error = error.error?.message || 'Registration failed';
        this.loading = false;
      }
    });
  }
}
