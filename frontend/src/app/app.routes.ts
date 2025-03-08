import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { AuthGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { 
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  { 
    path: 'dashboard', 
    loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [AuthGuard] 
  },
  { path: '**', redirectTo: '' }
];
