import {ComponentFixture, TestBed} from '@angular/core/testing';
import {RegisterComponent} from './register.component';
import {provideRouter, Router} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';
import {getTranslocoModule} from '../../../transloco/testing/transloco-testing.module';
import {of, throwError} from 'rxjs';
import {By} from '@angular/platform-browser';
import {HttpErrorResponse} from '@angular/common/http';
import {vi} from 'vitest';
import {provideZonelessChangeDetection} from '@angular/core';

describe('RegisterComponent', () => {
    let component: RegisterComponent;
    let fixture: ComponentFixture<RegisterComponent>;
    let authService: any;
    let router: any;

    beforeEach(async () => {
        const authServiceMock = {
            register: vi.fn()
        };

        await TestBed.configureTestingModule({
            imports: [
                RegisterComponent,
                getTranslocoModule()
            ],
            providers: [
                provideZonelessChangeDetection(),
                {provide: AuthService, useValue: authServiceMock},
                provideRouter([])
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(RegisterComponent);
        component = fixture.componentInstance;
        authService = TestBed.inject(AuthService);
        router = TestBed.inject(Router);
        vi.spyOn(router, 'navigate');
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should show errors when form is invalid and submitted', async () => {
        const form = fixture.debugElement.query(By.css('form'));
        form.triggerEventHandler('submit', {
            preventDefault: () => {
            }
        });
        fixture.detectChanges();

        expect(authService.register).not.toHaveBeenCalled();
        const errors = fixture.debugElement.queryAll(By.css('.invalid-feedback'));
        expect(errors.length).toBeGreaterThan(0);
    });

    it('should show password mismatch error', async () => {
        const passwordInput = fixture.debugElement.query(By.css('[data-cy="password-input"] input')).nativeElement;
        const confirmPasswordInput = fixture.debugElement.query(By.css('[data-cy="confirm-password-input"] input')).nativeElement;

        passwordInput.value = 'password123';
        passwordInput.dispatchEvent(new Event('input'));
        confirmPasswordInput.value = 'different';
        confirmPasswordInput.dispatchEvent(new Event('input'));

        fixture.detectChanges();

        const form = fixture.debugElement.query(By.css('form'));
        form.triggerEventHandler('submit', {
            preventDefault: () => {
            }
        });
        fixture.detectChanges();

        const errors = fixture.debugElement.queryAll(By.css('.invalid-feedback'));
        const confirmPasswordError = errors.find(e => e.nativeElement.textContent.includes('Passwords do not match'));
        expect(confirmPasswordError).toBeTruthy();
    });

    it('should call authService.register when form is valid', async () => {
        const usernameInput = fixture.debugElement.query(By.css('[data-cy="username-input"] input')).nativeElement;
        const emailInput = fixture.debugElement.query(By.css('[data-cy="email-input"] input')).nativeElement;
        const passwordInput = fixture.debugElement.query(By.css('[data-cy="password-input"] input')).nativeElement;
        const confirmPasswordInput = fixture.debugElement.query(By.css('[data-cy="confirm-password-input"] input')).nativeElement;

        usernameInput.value = 'testuser';
        usernameInput.dispatchEvent(new Event('input'));
        emailInput.value = 'test@example.com';
        emailInput.dispatchEvent(new Event('input'));
        passwordInput.value = 'password123';
        passwordInput.dispatchEvent(new Event('input'));
        confirmPasswordInput.value = 'password123';
        confirmPasswordInput.dispatchEvent(new Event('input'));

        fixture.detectChanges();

        authService.register.mockReturnValue(of({message: 'User registered'}));

        const form = fixture.debugElement.query(By.css('form'));
        form.triggerEventHandler('submit', {
            preventDefault: () => {
            }
        });

        fixture.detectChanges();

        expect(authService.register).toHaveBeenCalledWith({
            username: 'testuser',
            email: 'test@example.com',
            password: 'password123'
        });
        expect(router.navigate).toHaveBeenCalledWith(['/login'], {queryParams: {registered: true}});
    });

    it('should show error message when registration fails', async () => {
        const usernameInput = fixture.debugElement.query(By.css('[data-cy="username-input"] input')).nativeElement;
        const emailInput = fixture.debugElement.query(By.css('[data-cy="email-input"] input')).nativeElement;
        const passwordInput = fixture.debugElement.query(By.css('[data-cy="password-input"] input')).nativeElement;
        const confirmPasswordInput = fixture.debugElement.query(By.css('[data-cy="confirm-password-input"] input')).nativeElement;

        usernameInput.value = 'testuser';
        usernameInput.dispatchEvent(new Event('input'));
        emailInput.value = 'test@example.com';
        emailInput.dispatchEvent(new Event('input'));
        passwordInput.value = 'password123';
        passwordInput.dispatchEvent(new Event('input'));
        confirmPasswordInput.value = 'password123';
        confirmPasswordInput.dispatchEvent(new Event('input'));

        fixture.detectChanges();

        const errorResponse = new HttpErrorResponse({
            error: {error: 'USERNAME_TAKEN'},
            status: 400
        });
        authService.register.mockReturnValue(throwError(() => errorResponse));

        const form = fixture.debugElement.query(By.css('form'));
        form.triggerEventHandler('submit', {
            preventDefault: () => {
            }
        });

        fixture.detectChanges();

        const alert = fixture.debugElement.query(By.css('.alert-danger'));
        expect(alert).toBeTruthy();
        expect(alert.nativeElement.textContent).toContain('errors.USERNAME_TAKEN');
    });
});
