import {ComponentFixture, TestBed} from '@angular/core/testing';
import {PasswordResetRequestComponent} from './password-reset-request.component';
import {provideRouter, Router} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';
import {getTranslocoModule} from '../../../transloco/testing/transloco-testing.module';
import {of, throwError} from 'rxjs';
import {By} from '@angular/platform-browser';
import {HttpErrorResponse} from '@angular/common/http';
import {Mock, vi} from 'vitest';
import {provideZonelessChangeDetection} from '@angular/core';

describe('PasswordResetRequestComponent', () => {
    let component: PasswordResetRequestComponent;
    let fixture: ComponentFixture<PasswordResetRequestComponent>;
    let authService: AuthService;
    let router: Router;

    beforeEach(async () => {
        const authServiceMock = {
            requestPasswordReset: vi.fn()
        };

        await TestBed.configureTestingModule({
            imports: [
                PasswordResetRequestComponent,
                getTranslocoModule()
            ],
            providers: [
                provideZonelessChangeDetection(),
                {provide: AuthService, useValue: authServiceMock},
                provideRouter([])
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(PasswordResetRequestComponent);
        component = fixture.componentInstance;
        authService = TestBed.inject(AuthService);
        router = TestBed.inject(Router);
        vi.spyOn(router, 'navigate');
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should show error when username is empty and submitted', async () => {
        const form = fixture.debugElement.query(By.css('form'));
        form.triggerEventHandler('submit', {
            preventDefault: () => {
            }
        });
        fixture.detectChanges();

        expect(authService.requestPasswordReset).not.toHaveBeenCalled();
        const errors = fixture.debugElement.queryAll(By.css('.invalid-feedback'));
        expect(errors.length).toBeGreaterThan(0);
    });

    it('should call authService.requestPasswordReset and show success message', async () => {
        const usernameInput = fixture.debugElement.query(By.css('[data-cy="username-input"] input')).nativeElement;
        usernameInput.value = 'testuser';
        usernameInput.dispatchEvent(new Event('input'));
        fixture.detectChanges();

        const successMsg = 'Reset link sent to your email';
        (authService.requestPasswordReset as Mock).mockReturnValue(of({message: successMsg}));

        const form = fixture.debugElement.query(By.css('form'));
        form.triggerEventHandler('submit', {
            preventDefault: () => {
            }
        });
        fixture.detectChanges();

        expect(authService.requestPasswordReset).toHaveBeenCalledWith({username: 'testuser'});

        const successAlert = fixture.debugElement.query(By.css('[data-cy="success-message"]'));
        expect(successAlert).toBeTruthy();
        expect(successAlert.nativeElement.textContent).toContain(successMsg);

        const inputAfterSuccess = fixture.debugElement.query(By.css('[data-cy="username-input"]'));
        expect(inputAfterSuccess).toBeFalsy();
    });

    it('should show error message when request fails', async () => {
        const usernameInput = fixture.debugElement.query(By.css('[data-cy="username-input"] input')).nativeElement;
        usernameInput.value = 'unknownuser';
        usernameInput.dispatchEvent(new Event('input'));
        fixture.detectChanges();

        const errorResponse = new HttpErrorResponse({
            error: {error: 'USER_NOT_FOUND'},
            status: 404
        });
        (authService.requestPasswordReset as Mock).mockReturnValue(throwError(() => errorResponse));

        const form = fixture.debugElement.query(By.css('form'));
        form.triggerEventHandler('submit', {
            preventDefault: () => {
            }
        });
        fixture.detectChanges();

        const alert = fixture.debugElement.query(By.css('.alert-danger'));
        expect(alert).toBeTruthy();
        expect(alert.nativeElement.textContent).toContain('errors.USER_NOT_FOUND');
    });

    it('should navigate to login when back button clicked', async () => {
        const backBtn = fixture.debugElement.query(By.css('[data-cy="back-to-login"]'));
        backBtn.triggerEventHandler('click', null);
        expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });
});
