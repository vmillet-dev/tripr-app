import { Component, OnInit, inject } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { TranslocoPipe } from '@jsverse/transloco';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  standalone: true,
  imports: [TranslocoPipe, CommonModule]
})
export class DashboardComponent implements OnInit {
  username: string | null = null;
  private authService = inject(AuthService);

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.username = user?.username || null;
    });
  }
}
