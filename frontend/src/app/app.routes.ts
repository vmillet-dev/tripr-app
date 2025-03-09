import { Routes } from '@angular/router';




import { AuthGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent) },
  { path: 'login', loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent) },
  { path: 'password-reset-request', loadComponent: () => import('./features/auth/password-reset-request/password-reset-request.component').then(c => c.PasswordResetRequestComponent) },
  { path: 'password-reset', loadComponent: () => import('./features/auth/password-reset/password-reset.component').then(c => c.PasswordResetComponent) },
  { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent), canActivate: [AuthGuard] },
  { path: '**', redirectTo: '' }
];
