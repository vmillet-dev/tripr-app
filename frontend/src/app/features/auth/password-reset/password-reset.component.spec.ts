import {ComponentFixture, TestBed} from '@angular/core/testing';
import {PasswordResetComponent} from './password-reset.component';
import {ActivatedRoute, provideRouter, Router} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';
import {getTranslocoModule} from '../../../transloco/testing/transloco-testing.module';
import {of} from 'rxjs';
import {By} from '@angular/platform-browser';
import {vi} from 'vitest';
import {provideZonelessChangeDetection} from '@angular/core';

describe('PasswordResetComponent', () => {
    let component: PasswordResetComponent;
    let fixture: ComponentFixture<PasswordResetComponent>;
    let authService: any;
    let router: any;

    beforeEach(async () => {
        const authServiceMock = {
            validatePasswordResetToken: vi.fn(),
            resetPassword: vi.fn()
        };

        // Default mock behavior
        authServiceMock.validatePasswordResetToken.mockReturnValue(of({valid: true}));

        await TestBed.configureTestingModule({
            imports: [
                PasswordResetComponent,
                getTranslocoModule()
            ],
            providers: [
                provideZonelessChangeDetection(),
                {provide: AuthService, useValue: authServiceMock},
                provideRouter([]),
                {
                    provide: ActivatedRoute,
                    useValue: {
                        queryParams: of({token: 'valid-token'})
                    }
                }
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(PasswordResetComponent);
        component = fixture.componentInstance;
        authService = TestBed.inject(AuthService);
        router = TestBed.inject(Router);
        vi.spyOn(router, 'navigate');
        fixture.detectChanges();
    });

    afterEach(() => {
        vi.useRealTimers();
    });

    it('should create and validate token on init', () => {
        expect(component).toBeTruthy();
        expect(authService.validatePasswordResetToken).toHaveBeenCalledWith('valid-token');
        expect(component.isTokenValid()).toBe(true);
    });

    it('should show error if token is invalid', () => {
        authService.validatePasswordResetToken.mockReturnValue(of({valid: false}));

        // Re-create to trigger constructor with new mock behavior
        fixture = TestBed.createComponent(PasswordResetComponent);
        fixture.detectChanges();

        expect(fixture.componentInstance.isTokenValid()).toBe(false);
        const errorDiv = fixture.debugElement.query(By.css('[data-cy="token-invalid"]'));
        expect(errorDiv).toBeTruthy();
    });

    it('should show password mismatch error', async () => {
        const passwordInput = fixture.debugElement.query(By.css('[data-cy="new-password-input"] input')).nativeElement;
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
        const mismatchError = errors.find(e => e.nativeElement.textContent.includes('Passwords do not match'));
        expect(mismatchError).toBeTruthy();
    });

    it('should call authService.resetPassword on valid submission', async () => {
        vi.useFakeTimers();
        const passwordInput = fixture.debugElement.query(By.css('[data-cy="new-password-input"] input')).nativeElement;
        const confirmPasswordInput = fixture.debugElement.query(By.css('[data-cy="confirm-password-input"] input')).nativeElement;

        passwordInput.value = 'newpassword123';
        passwordInput.dispatchEvent(new Event('input'));
        confirmPasswordInput.value = 'newpassword123';
        confirmPasswordInput.dispatchEvent(new Event('input'));
        fixture.detectChanges();

        authService.resetPassword.mockReturnValue(of({message: 'Password reset successfully'}));

        const form = fixture.debugElement.query(By.css('form'));
        form.triggerEventHandler('submit', {
            preventDefault: () => {
            }
        });
        fixture.detectChanges();

        expect(authService.resetPassword).toHaveBeenCalledWith({
            token: 'valid-token',
            newPassword: 'newpassword123'
        });

        const successAlert = fixture.debugElement.query(By.css('[data-cy="success-message"]'));
        expect(successAlert).toBeTruthy();

        vi.advanceTimersByTime(3000); // Wait for the timeout in component
        expect(router.navigate).toHaveBeenCalledWith(['/login'], {queryParams: {resetSuccess: true}});
    });
});
