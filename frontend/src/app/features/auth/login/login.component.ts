import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NgIf, NgClass } from '@angular/common';
import { TranslocoModule } from '@ngneat/transloco';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
  imports: [ReactiveFormsModule, NgIf, NgClass, RouterLink, TranslocoModule]
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  loading = false;
  error = '';
  returnUrl = '/';

  private formBuilder = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);

  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });

    // Get return URL from route parameters or default to '/dashboard'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
  }

  get f() { return this.loginForm.controls; }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = '';

    this.authService.login({
      username: this.f['username'].value,
      password: this.f['password'].value
    }).subscribe({
      next: () => {
        this.router.navigate([this.returnUrl]);
      },
      error: error => {
        this.error = error.error?.message || 'Login failed';
        this.loading = false;
      }
    });
  }
}
