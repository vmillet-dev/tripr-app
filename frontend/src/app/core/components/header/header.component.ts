import {Component, computed, inject} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {toSignal} from "@angular/core/rxjs-interop";
import {TranslocoPipe} from "@jsverse/transloco";


@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    standalone: true,
    imports: [
        TranslocoPipe,
        RouterLinkActive,
        RouterLink
    ]
})
export class HeaderComponent {

    private authService = inject(AuthService);
    private router = inject(Router);

    currentUser = toSignal(this.authService.currentUser$, {
        initialValue: null
    });

    isAuthenticated = computed(() => !!this.currentUser());

    username = computed(() => this.currentUser()?.username ?? null);

    logout(): void {
        this.authService.logout().subscribe(() => {
            this.router.navigate(['/']);
        });
    }
}
