import {TestBed} from '@angular/core/testing';
import {AuthService} from './auth.service';
import {AuthenticationService} from '../api/generated';
import {TokenService} from './token.service';
import {of, throwError} from 'rxjs';
import {Mock, vi} from 'vitest';
import {HttpErrorResponse} from '@angular/common/http';

describe('AuthService', () => {
    let service: AuthService;
    let authApi: AuthenticationService;
    let tokenService: TokenService;

    beforeEach(() => {
        const authApiMock = {
            login: vi.fn(),
            register: vi.fn(),
            refreshToken: vi.fn(),
            logout: vi.fn(),
            requestPasswordReset: vi.fn(),
            resetPassword: vi.fn(),
            validateToken: vi.fn()
        };

        const tokenServiceMock = {
            setToken: vi.fn(),
            clearToken: vi.fn(),
            isAuthenticated: vi.fn(),
            getToken: vi.fn(),
            currentUser$: of(null)
        };

        TestBed.configureTestingModule({
            providers: [
                AuthService,
                {provide: AuthenticationService, useValue: authApiMock},
                {provide: TokenService, useValue: tokenServiceMock}
            ]
        });

        service = TestBed.inject(AuthService);
        authApi = TestBed.inject(AuthenticationService);
        tokenService = TestBed.inject(TokenService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    describe('login', () => {
        it('should call api.login and set token on success', () => {
            const credentials = {username: 'user', password: 'pwd'};
            const response = {accessToken: 'token123'};
            (authApi.login as Mock).mockReturnValue(of(response));

            return new Promise<void>((resolve) => {
                service.login(credentials).subscribe(res => {
                    expect(authApi.login).toHaveBeenCalledWith(credentials);
                    expect(tokenService.setToken).toHaveBeenCalledWith('token123');
                    expect(res).toEqual({accessToken: 'token123'});
                    resolve();
                });
            });
        });

        it('should handle missing accessToken in response', () => {
            (authApi.login as Mock).mockReturnValue(of({}));
            return new Promise<void>((resolve) => {
                service.login({username: 'u', password: 'p'}).subscribe(res => {
                    expect(tokenService.setToken).toHaveBeenCalledWith('');
                    expect(res.accessToken).toBe('');
                    resolve();
                });
            });
        });
    });

    describe('register', () => {
        it('should call api.register', () => {
            const data = {username: 'u', password: 'p', email: 'e@e.com'};
            (authApi.register as Mock).mockReturnValue(of({message: 'OK'}));

            return new Promise<void>((resolve) => {
                service.register(data).subscribe(res => {
                    expect(authApi.register).toHaveBeenCalledWith(data);
                    expect(res).toEqual({message: 'OK'});
                    resolve();
                });
            });
        });
    });

    describe('refreshToken', () => {
        it('should call api.refreshToken and update token', () => {
            (authApi.refreshToken as Mock).mockReturnValue(of({accessToken: 'new-token'}));

            return new Promise<void>((resolve) => {
                service.refreshToken().subscribe(res => {
                    expect(authApi.refreshToken).toHaveBeenCalled();
                    expect(tokenService.setToken).toHaveBeenCalledWith('new-token');
                    expect(res.accessToken).toBe('new-token');
                    resolve();
                });
            });
        });
    });

    describe('logout', () => {
        it('should call api.logout and clear token', () => {
            (authApi.logout as Mock).mockReturnValue(of({message: 'Logged out'}));

            return new Promise<void>((resolve) => {
                service.logout().subscribe(res => {
                    expect(authApi.logout).toHaveBeenCalled();
                    expect(tokenService.clearToken).toHaveBeenCalled();
                    expect(res.message).toBe('Logged out');
                    resolve();
                });
            });
        });

        it('should clear token even if api.logout fails', () => {
            const error = new HttpErrorResponse({status: 500});
            (authApi.logout as Mock).mockReturnValue(throwError(() => error));

            return new Promise<void>((resolve) => {
                service.logout().subscribe({
                    error: (err) => {
                        expect(tokenService.clearToken).toHaveBeenCalled();
                        expect(err).toBe(error);
                        resolve();
                    }
                });
            });
        });
    });

    describe('password reset', () => {
        it('should requestPasswordReset', () => {
            (authApi.requestPasswordReset as Mock).mockReturnValue(of({message: 'sent'}));
            return new Promise<void>((resolve) => {
                service.requestPasswordReset({username: 'user'}).subscribe(res => {
                    expect(authApi.requestPasswordReset).toHaveBeenCalledWith({username: 'user'});
                    expect(res.message).toBe('sent');
                    resolve();
                });
            });
        });

        it('should resetPassword', () => {
            const data = {token: 'tok', newPassword: 'new'};
            (authApi.resetPassword as Mock).mockReturnValue(of({message: 'reset'}));
            return new Promise<void>((resolve) => {
                service.resetPassword(data).subscribe(res => {
                    expect(authApi.resetPassword).toHaveBeenCalledWith({
                        token: 'tok',
                        newPassword: 'new'
                    });
                    expect(res.message).toBe('reset');
                    resolve();
                });
            });
        });

        it('should validatePasswordResetToken', () => {
            (authApi.validateToken as Mock).mockReturnValue(of({valid: true}));
            return new Promise<void>((resolve) => {
                service.validatePasswordResetToken('tok').subscribe(res => {
                    expect(authApi.validateToken).toHaveBeenCalledWith('tok');
                    expect(res.valid).toBe(true);
                    resolve();
                });
            });
        });
    });

    describe('helper methods', () => {
        it('should proxy isAuthenticated', () => {
            (tokenService.isAuthenticated as Mock).mockReturnValue(true);
            expect(service.isAuthenticated()).toBe(true);
        });

        it('should proxy getToken', () => {
            (tokenService.getToken as Mock).mockReturnValue('abc');
            expect(service.getToken()).toBe('abc');
        });
    });
});
