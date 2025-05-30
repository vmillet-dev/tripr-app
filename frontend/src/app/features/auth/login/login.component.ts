import {Component, inject, OnInit} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';
import {NgClass} from '@angular/common';
import {TranslocoPipe} from '@jsverse/transloco';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    imports: [ReactiveFormsModule, NgClass, RouterLink, TranslocoPipe]
})
export class LoginComponent implements OnInit {
  loading = false;
  error = '';
  returnUrl = '/';
  isRegistered = false;

  private formBuilder = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);

  loginForm = this.formBuilder.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  ngOnInit(): void {
    // Get return URL from route parameters or default to '/dashboard'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
    // Check if user was redirected after registration
    this.isRegistered = this.route.snapshot.queryParams['registered'] === 'true';
  }

  get f() { return this.loginForm.controls; }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = '';

    this.authService.login({
      username: this.f['username'].value as string,
      password: this.f['password'].value as string
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
