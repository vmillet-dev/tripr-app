import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HeaderComponent} from './header.component';
import {AuthService} from '../../services/auth.service';
import {provideRouter, Router} from '@angular/router';
import {TranslocoService} from '@jsverse/transloco';
import {getTranslocoModule} from '../../../transloco/testing/transloco-testing.module';
import {BehaviorSubject, of} from 'rxjs';
import {By} from '@angular/platform-browser';
import {vi} from 'vitest';
import {provideZonelessChangeDetection} from '@angular/core';

describe('HeaderComponent', () => {
    let component: HeaderComponent;
    let fixture: ComponentFixture<HeaderComponent>;
    let authService: AuthService;
    let translocoService: TranslocoService;
    let router: Router;
    let currentUserSubject: BehaviorSubject<any>;

    beforeEach(async () => {
        currentUserSubject = new BehaviorSubject<any>(null);

        const authServiceMock = {
            currentUser$: currentUserSubject.asObservable(),
            logout: vi.fn().mockReturnValue(of({message: 'OK'}))
        };

        await TestBed.configureTestingModule({
            imports: [
                HeaderComponent,
                getTranslocoModule()
            ],
            providers: [
                provideZonelessChangeDetection(),
                {provide: AuthService, useValue: authServiceMock},
                provideRouter([])
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(HeaderComponent);
        component = fixture.componentInstance;
        authService = TestBed.inject(AuthService);
        translocoService = TestBed.inject(TranslocoService);
        router = TestBed.inject(Router);
        vi.spyOn(router, 'navigate');
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should show login and register links when not authenticated', () => {
        currentUserSubject.next(null);
        fixture.detectChanges();

        const loginLink = fixture.debugElement.query(By.css('a[routerLink="/login"]'));
        const registerLink = fixture.debugElement.query(By.css('a[routerLink="/register"]'));
        const logoutBtn = fixture.debugElement.query(By.css('button[data-cy="logout-button"]'));

        expect(loginLink).toBeTruthy();
        expect(registerLink).toBeTruthy();
        expect(logoutBtn).toBeFalsy();
    });

    it.skip('should show username and logout button when authenticated', async () => {
        currentUserSubject.next({username: 'testuser', roles: []});
        fixture.detectChanges();
        await fixture.whenStable();
        fixture.detectChanges();

        const logoutBtn = fixture.debugElement.query(By.css('[data-cy="logout-button"]'));
        const usernameSpan = fixture.debugElement.query(By.css('[data-cy="username-display"]'));

        expect(logoutBtn).toBeTruthy();
        expect(usernameSpan.nativeElement.textContent).toContain('testuser');
    });

    it.skip('should call logout and navigate to home on logout click', async () => {
        currentUserSubject.next({username: 'testuser', roles: []});
        fixture.detectChanges();
        await fixture.whenStable();
        fixture.detectChanges();

        const logoutBtn = fixture.debugElement.query(By.css('[data-cy="logout-button"]'));
        logoutBtn.triggerEventHandler('click', null);

        expect(authService.logout).toHaveBeenCalled();
        expect(router.navigate).toHaveBeenCalledWith(['/']);
    });

    describe('Language switcher', () => {
        it('should change language when changeLanguage is called', () => {
            const spy = vi.spyOn(translocoService, 'setActiveLang');
            component.changeLanguage('fr');
            expect(spy).toHaveBeenCalledWith('fr');
        });

        it('should return correct flag and current language', () => {
            vi.spyOn(translocoService, 'getActiveLang').mockReturnValue('en');
            expect(component.currentLanguage()).toBe('en');
            expect(component.currentLanguageFlag()).toBe('🇬🇧');

            vi.spyOn(translocoService, 'getActiveLang').mockReturnValue('fr');
            expect(component.currentLanguage()).toBe('fr');
            expect(component.currentLanguageFlag()).toBe('🇫🇷');
        });
    });
});
