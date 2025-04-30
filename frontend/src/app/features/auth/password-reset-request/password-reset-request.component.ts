import { Component, OnInit, inject, ChangeDetectionStrategy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { PasswordResetService } from '../../../core/services/password-reset.service';
import { PasswordResetRequest } from '../../../core/models/password-reset.model';

@Component({
  selector: 'app-password-reset-request',
  templateUrl: './password-reset-request.component.html',
  styleUrls: ['./password-reset-request.component.scss'],
  standalone: true,
  imports: [ReactiveFormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PasswordResetRequestComponent implements OnInit {
  resetForm!: FormGroup;
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';

  private formBuilder = inject(FormBuilder);
  private passwordResetService = inject(PasswordResetService);
  private router = inject(Router);

  ngOnInit(): void {
    this.resetForm = this.formBuilder.group({
      username: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.resetForm.invalid) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.passwordResetService.requestPasswordReset(this.resetForm.value as PasswordResetRequest)
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
