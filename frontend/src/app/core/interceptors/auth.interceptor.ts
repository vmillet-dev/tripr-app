import {HttpErrorResponse, HttpHandlerFn, HttpInterceptorFn, HttpRequest} from '@angular/common/http';
import {inject} from '@angular/core';
import {Observable, throwError} from 'rxjs';
import {catchError, switchMap} from 'rxjs/operators';
import {AuthService} from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (
    request: HttpRequest<unknown>,
    next: HttpHandlerFn
): Observable<any> => {
    const authService = inject(AuthService);
    const token = authService.getToken();

    if (token) {
        request = addToken(request, token);
    }

    return next(request).pipe(
        catchError(error => {
            if (error instanceof HttpErrorResponse && error.status === 401 && error.error === 'FUNC_002') {
                return handle401Error(request, next, authService);
            }
            return throwError(() => error);
        })
    );
};

function addToken(request: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
    return request.clone({
        setHeaders: {Authorization: `Bearer ${token}`}
    });
}

function handle401Error(
    request: HttpRequest<unknown>,
    next: HttpHandlerFn,
    authService: AuthService
): Observable<any> {
    return authService.refreshToken().pipe(
        switchMap(token => {
            return next(addToken(request, token.accessToken));
        }),
        catchError(error => {
            authService.logout();
            return throwError(() => error);
        })
    );
}
