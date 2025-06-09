import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';
import { NgClass } from '@angular/common';
import { TranslocoPipe } from '@jsverse/transloco';
import { AuthRequest } from '../../../core/models/auth.model';

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
  loginForm: FormGroup;

  private readonly formBuilder = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);

  constructor() {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
    this.isRegistered = this.route.snapshot.queryParams['registered'] === 'true';
  }

  get f() { 
    return this.loginForm.controls; 
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = '';

    const credentials: AuthRequest = {
      username: this.f['username'].value as string,
      password: this.f['password'].value as string
    };

    this.authService.login(credentials).subscribe({
      next: () => {
        this.router.navigate([this.returnUrl]);
      },
      error: (error: HttpErrorResponse) => {
        this.error = error.error?.message || 'Login failed';
        this.loading = false;
      }
    });
  }
}
