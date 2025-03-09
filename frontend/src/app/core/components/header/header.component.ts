import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';

import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
  standalone: true,
  imports: [RouterLink, RouterLinkActive, TranslocoPipe]
})
export class HeaderComponent implements OnInit {
  isAuthenticated = false;
  username: string | null = null;

  private authService = inject(AuthService);
  private router = inject(Router);

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.isAuthenticated = !!user;
      this.username = user?.username || null;
    });
  }

  logout(): void {
    this.authService.logout().subscribe(() => {
      this.router.navigate(['/']);
    });
  }
}
