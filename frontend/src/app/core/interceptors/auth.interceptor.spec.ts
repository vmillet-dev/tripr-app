import {TestBed} from '@angular/core/testing';
import {HttpErrorResponse, HttpHandlerFn, HttpRequest} from '@angular/common/http';
import {authInterceptor} from './auth.interceptor';
import {AuthService} from '../services/auth.service';
import {of, throwError} from 'rxjs';
import {Mock, vi} from 'vitest';

describe('authInterceptor', () => {
    let authService: AuthService;

    beforeEach(() => {
        const authServiceMock = {
            getToken: vi.fn(),
            refreshToken: vi.fn(),
            logout: vi.fn()
        };

        TestBed.configureTestingModule({
            providers: [
                {provide: AuthService, useValue: authServiceMock}
            ]
        });

        authService = TestBed.inject(AuthService);
    });

    it('should add Authorization header if token exists', () => {
        (authService.getToken as Mock).mockReturnValue('fake-token');
        const req = new HttpRequest('GET', '/api/test');
        return new Promise<void>((resolve) => {
            const next: HttpHandlerFn = (request) => {
                expect(request.headers.get('Authorization')).toBe('Bearer fake-token');
                return of({} as any);
            };

            TestBed.runInInjectionContext(() => {
                authInterceptor(req, next).subscribe(() => resolve());
            });
        });
    });

    it('should not add Authorization header if no token', () => {
        (authService.getToken as Mock).mockReturnValue(null);
        const req = new HttpRequest('GET', '/api/test');
        return new Promise<void>((resolve) => {
            const next: HttpHandlerFn = (request) => {
                expect(request.headers.has('Authorization')).toBe(false);
                return of({} as any);
            };

            TestBed.runInInjectionContext(() => {
                authInterceptor(req, next).subscribe(() => resolve());
            });
        });
    });

    describe('handle 401 error', () => {
        it('should refresh token and retry on 401 with FUNC_002 error', () => {
            (authService.getToken as Mock).mockReturnValue('old-token');
            (authService.refreshToken as Mock).mockReturnValue(of({accessToken: 'new-token'}));

            const req = new HttpRequest('GET', '/api/test');
            let callCount = 0;
            return new Promise<void>((resolve) => {
                const next: HttpHandlerFn = (request) => {
                    callCount++;
                    if (callCount === 1) {
                        return throwError(() => new HttpErrorResponse({
                            status: 401,
                            error: 'FUNC_002'
                        }));
                    }
                    expect(request.headers.get('Authorization')).toBe('Bearer new-token');
                    return of({} as any);
                };

                TestBed.runInInjectionContext(() => {
                    authInterceptor(req, next).subscribe(() => {
                        expect(authService.refreshToken).toHaveBeenCalled();
                        expect(callCount).toBe(2);
                        resolve();
                    });
                });
            });
        });

        it('should logout and throw error if refresh token fails', () => {
            (authService.getToken as Mock).mockReturnValue('old-token');
            (authService.refreshToken as Mock).mockReturnValue(throwError(() => new Error('Refresh failed')));

            const req = new HttpRequest('GET', '/api/test');
            const next: HttpHandlerFn = () => {
                return throwError(() => new HttpErrorResponse({
                    status: 401,
                    error: 'FUNC_002'
                }));
            };

            return new Promise<void>((resolve) => {
                TestBed.runInInjectionContext(() => {
                    authInterceptor(req, next).subscribe({
                        error: () => {
                            expect(authService.logout).toHaveBeenCalled();
                            resolve();
                        }
                    });
                });
            });
        });

        it('should just throw error if 401 but not FUNC_002', () => {
            const req = new HttpRequest('GET', '/api/test');
            const next: HttpHandlerFn = () => {
                return throwError(() => new HttpErrorResponse({
                    status: 401,
                    error: 'OTHER_ERROR'
                }));
            };

            return new Promise<void>((resolve) => {
                TestBed.runInInjectionContext(() => {
                    authInterceptor(req, next).subscribe({
                        error: (err) => {
                            expect(err.error).toBe('OTHER_ERROR');
                            expect(authService.refreshToken).not.toHaveBeenCalled();
                            resolve();
                        }
                    });
                });
            });
        });
    });
});
