import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { PasswordResetService } from '../../../core/services/password-reset.service';
import { TranslocoModule } from '@jsverse/transloco';
import { PasswordResetRequest } from '../../../core/models/password-reset.model';

@Component({
  selector: 'app-password-reset-request',
  templateUrl: './password-reset-request.component.html',
  imports: [ReactiveFormsModule, TranslocoModule]
})
export class PasswordResetRequestComponent implements OnInit {
  resetForm: FormGroup;
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';

  private readonly formBuilder = inject(FormBuilder);
  private readonly passwordResetService = inject(PasswordResetService);
  private readonly router = inject(Router);

  constructor() {
    this.resetForm = this.formBuilder.group({
      username: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.resetForm.invalid) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    const request: PasswordResetRequest = this.resetForm.value;

    this.passwordResetService.requestPasswordReset(request)
      .subscribe({
        next: (response: { message: string }) => {
          this.isSubmitting = false;
          this.successMessage = response.message;
          this.resetForm.reset();
        },
        error: (error: HttpErrorResponse) => {
          this.isSubmitting = false;
          this.errorMessage = error.error?.message || 'An error occurred. Please try again.';
        }
      });
  }

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }
}
