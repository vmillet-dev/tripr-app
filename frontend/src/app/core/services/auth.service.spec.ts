import {TestBed} from '@angular/core/testing';
import {AuthService} from './auth.service';
import {AuthenticationService} from '../api/generated';
import {TokenService} from './token.service';
import {of} from 'rxjs';
import {vi, describe, it, expect, beforeEach} from 'vitest';

describe('AuthService', () => {
    let service: AuthService;
    let authApi: any;
    let tokenService: any;

    beforeEach(() => {
        authApi = {
            login: vi.fn(),
            register: vi.fn(),
            refreshToken: vi.fn(),
            logout: vi.fn(),
            requestPasswordReset: vi.fn(),
            resetPassword: vi.fn(),
            validateToken: vi.fn()
        };
        tokenService = {
            setToken: vi.fn(),
            clearToken: vi.fn(),
            getToken: vi.fn(),
            isAuthenticated: vi.fn(),
            currentUser$: of(null),
            currentUser: vi.fn()
        };

        TestBed.configureTestingModule({
            providers: [
                AuthService,
                {provide: AuthenticationService, useValue: authApi},
                {provide: TokenService, useValue: tokenService}
            ]
        });
        service = TestBed.inject(AuthService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should login and set token', () => {
        const credentials = {username: 'test', password: 'password'};
        const response = {accessToken: 'fake-token'};
        authApi.login.mockReturnValue(of(response));

        service.login(credentials).subscribe(res => {
            expect(res.accessToken).toBe('fake-token');
        });

        expect(authApi.login).toHaveBeenCalledWith({
            username: credentials.username,
            password: credentials.password
        });
        expect(tokenService.setToken).toHaveBeenCalledWith('fake-token');
    });

    it('should register successfully', () => {
        const data = {username: 'test', password: 'password', email: 'test@example.com'};
        authApi.register.mockReturnValue(of({message: 'Success'}));

        service.register(data).subscribe(res => {
            expect(res.message).toBe('Success');
        });

        expect(authApi.register).toHaveBeenCalledWith({
            username: data.username,
            password: data.password,
            email: data.email
        });
    });

    it('should refresh token', () => {
        authApi.refreshToken.mockReturnValue(of({accessToken: 'new-token'}));
        service.refreshToken().subscribe();
        expect(authApi.refreshToken).toHaveBeenCalled();
        expect(tokenService.setToken).toHaveBeenCalledWith('new-token');
    });

    it('should logout and clear token', () => {
        authApi.logout.mockReturnValue(of({message: 'Logged out'}));
        service.logout().subscribe();
        expect(authApi.logout).toHaveBeenCalled();
        expect(tokenService.clearToken).toHaveBeenCalled();
    });
});
