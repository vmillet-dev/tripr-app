import {TestBed} from '@angular/core/testing';
import {HttpErrorResponse, HttpHandlerFn, HttpRequest, HttpResponse} from '@angular/common/http';
import {authInterceptor} from './auth.interceptor';
import {AuthService} from '../services/auth.service';
import {of, throwError} from 'rxjs';
import {vi, describe, it, expect, beforeEach} from 'vitest';

describe('authInterceptor', () => {
    let authService: any;

    beforeEach(() => {
        authService = {
            getToken: vi.fn(),
            refreshToken: vi.fn(),
            logout: vi.fn()
        };

        TestBed.configureTestingModule({
            providers: [
                {provide: AuthService, useValue: authService}
            ]
        });
    });

    it('should add Authorization header if token exists', () => {
        authService.getToken.mockReturnValue('fake-token');
        const req = new HttpRequest('GET', '/api/test');
        const next: HttpHandlerFn = (r) => {
            expect(r.headers.get('Authorization')).toBe('Bearer fake-token');
            return of(new HttpResponse());
        };

        TestBed.runInInjectionContext(() => {
            authInterceptor(req, next).subscribe();
        });
    });

    it('should not add Authorization header if no token', () => {
        authService.getToken.mockReturnValue(null);
        const req = new HttpRequest('GET', '/api/test');
        const next: HttpHandlerFn = (r) => {
            expect(r.headers.has('Authorization')).toBe(false);
            return of(new HttpResponse());
        };

        TestBed.runInInjectionContext(() => {
            authInterceptor(req, next).subscribe();
        });
    });

    it('should handle 401 error and refresh token', () => {
        authService.getToken.mockReturnValue('old-token');
        authService.refreshToken.mockReturnValue(of({accessToken: 'new-token'}));

        const req = new HttpRequest('GET', '/api/test');
        let callCount = 0;
        const next: HttpHandlerFn = (r) => {
            callCount++;
            if (callCount === 1) {
                return throwError(() => new HttpErrorResponse({
                    status: 401,
                    error: 'FUNC_002'
                }));
            }
            expect(r.headers.get('Authorization')).toBe('Bearer new-token');
            return of(new HttpResponse());
        };

        TestBed.runInInjectionContext(() => {
            authInterceptor(req, next).subscribe();
        });
        expect(authService.refreshToken).toHaveBeenCalled();
    });

    it('should logout if refresh token fails', () => {
        authService.getToken.mockReturnValue('old-token');
        authService.refreshToken.mockReturnValue(throwError(() => new Error('Refresh failed')));

        const req = new HttpRequest('GET', '/api/test');
        const next: HttpHandlerFn = () => {
            return throwError(() => new HttpErrorResponse({
                status: 401,
                error: 'FUNC_002'
            }));
        };

        TestBed.runInInjectionContext(() => {
            authInterceptor(req, next).subscribe({
                error: () => {
                    expect(authService.logout).toHaveBeenCalled();
                }
            });
        });
    });
});
