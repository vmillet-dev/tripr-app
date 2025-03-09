import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PasswordResetService } from '../../../core/services/password-reset.service';

@Component({
  selector: 'app-password-reset',
  templateUrl: './password-reset.component.html',
  styleUrls: ['./password-reset.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class PasswordResetComponent implements OnInit {
  resetForm!: FormGroup;
  token: string = '';
  isSubmitting = false;
  isValidatingToken = true;
  isTokenValid = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private formBuilder: FormBuilder,
    private passwordResetService: PasswordResetService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.resetForm = this.formBuilder.group({
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    }, { 
      validators: this.passwordMatchValidator 
    });

    this.route.queryParams.subscribe(params => {
      if (params['token']) {
        this.token = params['token'];
        this.validateToken();
      } else {
        this.isValidatingToken = false;
        this.isTokenValid = false;
        this.errorMessage = 'Invalid or missing token. Please request a new password reset link.';
      }
    });
  }

  validateToken(): void {
    this.passwordResetService.validateToken(this.token)
      .subscribe({
        next: (response) => {
          this.isValidatingToken = false;
          this.isTokenValid = response.valid;
          if (!response.valid) {
            this.errorMessage = 'This password reset link has expired or is invalid. Please request a new one.';
          }
        },
        error: (error) => {
          this.isValidatingToken = false;
          this.isTokenValid = false;
          this.errorMessage = 'Failed to validate token. Please request a new password reset link.';
        }
      });
  }

  passwordMatchValidator(group: FormGroup): { [key: string]: boolean } | null {
    const password = group.get('newPassword')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    
    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  onSubmit(): void {
    if (this.resetForm.invalid) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.passwordResetService.resetPassword({
      token: this.token,
      newPassword: this.resetForm.get('newPassword')?.value
    })
      .subscribe({
        next: (response) => {
          this.isSubmitting = false;
          this.successMessage = response.message;
          setTimeout(() => {
            this.router.navigate(['/login'], { queryParams: { resetSuccess: true } });
          }, 3000);
        },
        error: (error) => {
          this.isSubmitting = false;
          this.errorMessage = error.error?.message || 'An error occurred. Please try again.';
        }
      });
  }

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }
}
