import {Component, inject} from '@angular/core';
import {TranslocoPipe} from '@jsverse/transloco';
import {toSignal} from "@angular/core/rxjs-interop";
import {AuthService} from "../../core/services/auth.service";
import {map} from "rxjs";

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    imports: [TranslocoPipe]
})
export class DashboardComponent {
    private authService = inject(AuthService);

    username = toSignal(
        this.authService.currentUser$.pipe(map(user => user?.username || null)),
        {initialValue: null}
    );
}

