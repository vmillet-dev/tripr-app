import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PasswordResetService } from '../../../core/services/password-reset.service';

@Component({
  selector: 'app-password-reset-request',
  templateUrl: './password-reset-request.component.html',
  styleUrls: ['./password-reset-request.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class PasswordResetRequestComponent implements OnInit {
  resetForm!: FormGroup;
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private formBuilder: FormBuilder,
    private passwordResetService: PasswordResetService,
    private router: Router
  ) {}

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

    this.passwordResetService.requestPasswordReset(this.resetForm.value)
      .subscribe({
        next: (response) => {
          this.isSubmitting = false;
          this.successMessage = response.message;
          this.resetForm.reset();
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
