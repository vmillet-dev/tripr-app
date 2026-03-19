import {ComponentFixture, TestBed} from '@angular/core/testing';
import {LoginComponent} from './login.component';
import {ActivatedRoute, provideRouter, Router} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';
import {getTranslocoModule} from '../../../transloco/testing/transloco-testing.module';
import {of, throwError} from 'rxjs';
import {By} from '@angular/platform-browser';
import {HttpErrorResponse} from '@angular/common/http';
import {Mock, vi} from 'vitest';
import {provideZonelessChangeDetection} from '@angular/core';

describe('LoginComponent', () => {
    let component: LoginComponent;
    let fixture: ComponentFixture<LoginComponent>;
    let authService: AuthService;
    let router: Router;

    beforeEach(async () => {
        const authServiceMock = {
            login: vi.fn()
        };

        await TestBed.configureTestingModule({
            imports: [
                LoginComponent,
                getTranslocoModule()
            ],
            providers: [
                provideZonelessChangeDetection(),
                {provide: AuthService, useValue: authServiceMock},
                provideRouter([]),
                {
                    provide: ActivatedRoute,
                    useValue: {
                        snapshot: {
                            queryParams: {returnUrl: '/dashboard'}
                        }
                    }
                }
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(LoginComponent);
        component = fixture.componentInstance;
        authService = TestBed.inject(AuthService);
        router = TestBed.inject(Router);
        vi.spyOn(router, 'navigate');
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize with returnUrl from queryParams', () => {
        expect(component['returnUrl']).toBe('/dashboard');
    });

    it('should show success message if registered query param is true', async () => {
        // Re-create component with registered=true
        TestBed.resetTestingModule();
        TestBed.configureTestingModule({
            imports: [LoginComponent, getTranslocoModule()],
            providers: [
                provideZonelessChangeDetection(),
                {provide: AuthService, useValue: {login: vi.fn()}},
                provideRouter([]),
                {
                    provide: ActivatedRoute,
                    useValue: {
                        snapshot: {
                            queryParams: {registered: 'true'}
                        }
                    }
                }
            ]
        });
        fixture = TestBed.createComponent(LoginComponent);
        fixture.detectChanges();

        const successAlert = fixture.debugElement.query(By.css('[data-cy="success-message"]'));
        expect(successAlert).toBeTruthy();
        expect(successAlert.nativeElement.textContent).toContain('Registration completed successfully');
    });

    it('should show error when fields are empty and submitted', async () => {
        const form = fixture.debugElement.query(By.css('form'));
        form.triggerEventHandler('submit', {
            preventDefault: () => {
            }
        });
        fixture.detectChanges();

        expect(authService.login).not.toHaveBeenCalled();

        const errors = fixture.debugElement.queryAll(By.css('.invalid-feedback'));
        expect(errors.length).toBeGreaterThan(0);
    });

    it('should call authService.login when form is valid and submitted', async () => {
        const usernameInput = fixture.debugElement.query(By.css('[data-cy="username-input"]')).nativeElement;
        const passwordInput = fixture.debugElement.query(By.css('[data-cy="password-input"]')).nativeElement;

        usernameInput.value = 'testuser';
        usernameInput.dispatchEvent(new Event('input'));
        passwordInput.value = 'mypassword';
        passwordInput.dispatchEvent(new Event('input'));

        fixture.detectChanges();

        (authService.login as Mock).mockReturnValue(of({accessToken: 'fake-token'}));

        const form = fixture.debugElement.query(By.css('form'));
        form.triggerEventHandler('submit', {
            preventDefault: () => {
            }
        });

        fixture.detectChanges();

        expect(authService.login).toHaveBeenCalledWith({
            username: 'testuser',
            password: 'mypassword'
        });
        expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
    });

    it('should show error message when login fails', async () => {
        const usernameInput = fixture.debugElement.query(By.css('[data-cy="username-input"]')).nativeElement;
        const passwordInput = fixture.debugElement.query(By.css('[data-cy="password-input"]')).nativeElement;

        usernameInput.value = 'testuser';
        usernameInput.dispatchEvent(new Event('input'));
        passwordInput.value = 'wrongpassword';
        passwordInput.dispatchEvent(new Event('input'));

        fixture.detectChanges();

        const errorResponse = new HttpErrorResponse({
            error: {error: 'INVALID_CREDENTIALS'},
            status: 401
        });
        (authService.login as Mock).mockReturnValue(throwError(() => errorResponse));

        const form = fixture.debugElement.query(By.css('form'));
        form.triggerEventHandler('submit', {
            preventDefault: () => {
            }
        });

        fixture.detectChanges();

        const alert = fixture.debugElement.query(By.css('.alert-danger'));
        expect(alert).toBeTruthy();
        expect(alert.nativeElement.textContent).toContain('errors.INVALID_CREDENTIALS');
    });

    it('should disable submit button when loading', async () => {
        component.loginAction.loading.set(true);
        fixture.detectChanges();

        const submitBtn = fixture.debugElement.query(By.css('[data-cy="login-button"]')).nativeElement;
        expect(submitBtn.disabled).toBe(true);
        expect(fixture.debugElement.query(By.css('.spinner-border'))).toBeTruthy();
    });
});
