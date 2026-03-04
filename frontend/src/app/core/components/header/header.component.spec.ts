import {TestBed} from '@angular/core/testing';
import {HeaderComponent} from './header.component';
import {AuthService} from '../../services/auth.service';
import {of} from 'rxjs';
import {provideRouter, Router} from '@angular/router';
import {getTranslocoModule} from '../../../transloco/testing/transloco-testing.module';
import {vi, describe, it, expect, beforeEach} from 'vitest';

describe('HeaderComponent', () => {
    let authService: any;
    let router: Router;

    beforeEach(async () => {
        authService = {
            currentUser$: of(null),
            logout: vi.fn().mockReturnValue(of({}))
        };

        await TestBed.configureTestingModule({
            imports: [HeaderComponent, getTranslocoModule()],
            providers: [
                provideRouter([]),
                {provide: AuthService, useValue: authService}
            ]
        }).compileComponents();

        router = TestBed.inject(Router);
        vi.spyOn(router, 'navigate');
    });

    it('should create', () => {
        const fixture = TestBed.createComponent(HeaderComponent);
        const component = fixture.componentInstance;
        expect(component).toBeTruthy();
    });

    it('should show login/register when not authenticated', () => {
        const fixture = TestBed.createComponent(HeaderComponent);
        fixture.detectChanges();
        expect(fixture.componentInstance.isAuthenticated()).toBe(false);
    });

    it('should show username when authenticated', () => {
        // Change the observable before creating the component
        authService.currentUser$ = of({username: 'testuser', roles: []});
        const fixture = TestBed.createComponent(HeaderComponent);
        fixture.detectChanges();
        expect(fixture.componentInstance.isAuthenticated()).toBe(true);
        expect(fixture.componentInstance.username()).toBe('testuser');
    });

    it('should call logout', () => {
        const fixture = TestBed.createComponent(HeaderComponent);
        fixture.componentInstance.logout();
        expect(authService.logout).toHaveBeenCalled();
        expect(router.navigate).toHaveBeenCalledWith(['/']);
    });
});
