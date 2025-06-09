import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { TranslocoPipe } from '@jsverse/transloco';
import { User } from '../../core/models/auth.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  imports: [TranslocoPipe]
})
export class DashboardComponent implements OnInit, OnDestroy {
  username: string | null = null;
  
  private readonly authService = inject(AuthService);
  private subscription = new Subscription();

  ngOnInit(): void {
    this.subscription.add(
      this.authService.currentUser$.subscribe((user: User | null) => {
        this.username = user?.username || null;
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
