import {TestBed} from '@angular/core/testing';
import {LoginComponent} from './login.component';
import {AuthService} from '../../../core/services/auth.service';
import {of, throwError} from 'rxjs';
import {provideRouter, Router, ActivatedRoute} from '@angular/router';
import {ReactiveFormsModule} from '@angular/forms';
import {getTranslocoModule} from '../../../transloco/testing/transloco-testing.module';
import {vi, describe, it, expect, beforeEach} from 'vitest';

describe('LoginComponent', () => {
    let authService: any;
    let router: Router;
    let route: any;

    beforeEach(async () => {
        authService = {
            login: vi.fn()
        };
        route = {
            snapshot: {
                queryParams: {}
            }
        };

        await TestBed.configureTestingModule({
            imports: [LoginComponent, ReactiveFormsModule, getTranslocoModule()],
            providers: [
                provideRouter([]),
                {provide: AuthService, useValue: authService},
                {provide: ActivatedRoute, useValue: route}
            ]
        }).compileComponents();

        router = TestBed.inject(Router);
        vi.spyOn(router, 'navigate');
    });

    it('should create', () => {
        const fixture = TestBed.createComponent(LoginComponent);
        const component = fixture.componentInstance;
        expect(component).toBeTruthy();
    });

    it('should submit login form and navigate to dashboard', () => {
        const fixture = TestBed.createComponent(LoginComponent);
        const component = fixture.componentInstance;
        fixture.detectChanges(); // triggers ngOnInit to set returnUrl
        authService.login.mockReturnValue(of({accessToken: 'token'}));

        component.loginForm.controls['username'].setValue('user');
        component.loginForm.controls['password'].setValue('pass');
        component.onSubmit();

        expect(authService.login).toHaveBeenCalledWith({username: 'user', password: 'pass'});
        expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
    });

    it('should handle login error', () => {
        const fixture = TestBed.createComponent(LoginComponent);
        const component = fixture.componentInstance;
        authService.login.mockReturnValue(throwError(() => ({error: {message: 'Invalid credentials'}})));

        component.loginForm.controls['username'].setValue('user');
        component.loginForm.controls['password'].setValue('wrong');
        component.onSubmit();

        expect(component.error()).toBe('Invalid credentials');
        expect(component.loading()).toBe(false);
    });

    it('should handle registered query param', () => {
        route.snapshot.queryParams = {registered: 'true'};
        const fixture = TestBed.createComponent(LoginComponent);
        fixture.detectChanges();
        expect(fixture.componentInstance.isRegistered()).toBe(true);
    });
});
