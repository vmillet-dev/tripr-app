import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { TranslocoPipe } from '@jsverse/transloco';
import { User } from '../../models/auth.model';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  imports: [RouterLink, RouterLinkActive, TranslocoPipe]
})
export class HeaderComponent implements OnInit, OnDestroy {
  isAuthenticated = false;
  username: string | null = null;

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private subscription = new Subscription();

  ngOnInit(): void {
    this.subscription.add(
      this.authService.currentUser$.subscribe((user: User | null) => {
        this.isAuthenticated = !!user;
        this.username = user?.username || null;
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (error) => {
        console.error('Logout failed:', error);
        this.router.navigate(['/']);
      }
    });
  }
}
