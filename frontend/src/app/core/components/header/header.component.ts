import {Component, computed, inject} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {toSignal} from "@angular/core/rxjs-interop";
import {TranslocoPipe, TranslocoService} from "@jsverse/transloco";


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
    private readonly translocoService = inject(TranslocoService);
    private readonly authService = inject(AuthService);
    private readonly router = inject(Router);

    changeLanguage(lang: 'fr' | 'en'): void {
        this.translocoService.setActiveLang(lang);
    }

    currentLanguage(): 'fr' | 'en' {
        return this.translocoService.getActiveLang() === 'en' ? 'en' : 'fr';
    }

    currentLanguageFlag(): string {
        return this.currentLanguage() === 'fr' ? '🇫🇷' : '🇬🇧';
    }

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
