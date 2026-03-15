import {TestBed} from '@angular/core/testing';
import {Router, RouterStateSnapshot} from '@angular/router';
import {AuthService} from '../services/auth.service';
import {AuthGuard} from './auth.guard';
import {Mock, vi} from 'vitest';

describe('AuthGuard', () => {
    let authService: AuthService;
    let router: Router;

    beforeEach(() => {
        const authServiceMock = {
            isAuthenticated: vi.fn()
        };

        const routerMock = {
            navigate: vi.fn().mockResolvedValue(true)
        };

        TestBed.configureTestingModule({
            providers: [
                {provide: AuthService, useValue: authServiceMock},
                {provide: Router, useValue: routerMock}
            ]
        });

        authService = TestBed.inject(AuthService);
        router = TestBed.inject(Router);
    });

    it('should return true if authenticated', async () => {
        (authService.isAuthenticated as Mock).mockReturnValue(true);

        const result = await TestBed.runInInjectionContext(() =>
            AuthGuard({} as any, {url: '/dashboard'} as RouterStateSnapshot)
        );

        expect(result).toBe(true);
        expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should navigate to login and return false if not authenticated', async () => {
        (authService.isAuthenticated as Mock).mockReturnValue(false);

        const result = await TestBed.runInInjectionContext(() =>
            AuthGuard({} as any, {url: '/dashboard'} as RouterStateSnapshot)
        );

        expect(result).toBe(false);
        expect(router.navigate).toHaveBeenCalledWith(['/login'], {queryParams: {returnUrl: '/dashboard'}});
    });
});
