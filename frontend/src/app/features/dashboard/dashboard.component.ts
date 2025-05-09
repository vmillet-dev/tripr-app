import {Component, inject, OnInit} from '@angular/core';
import {AuthService} from '../../core/services/auth.service';
import {TranslocoPipe} from '@jsverse/transloco';

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    imports: [TranslocoPipe]
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
