import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable, throwError} from 'rxjs';
import {catchError, tap} from 'rxjs/operators';
import {JwtHelperService} from '@auth0/angular-jwt';
import {environment} from '../../../environments/environment';
import {AuthRequest, AuthResponse, RegisterRequest} from '../models/auth.model';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = `${environment.apiUrl}/auth`;
    private http = inject(HttpClient);

    private jwtHelper = new JwtHelperService();
    private currentUserSubject = new BehaviorSubject<any>(null);
    private token: string | null = null; // volatile token for safety purpose

    public currentUser$ = this.currentUserSubject.asObservable();

    login(credentials: AuthRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials, {withCredentials: true})
            .pipe(
                tap(res => this.handleAccessToken(res)),
                catchError(error => {
                    return throwError(() => error);
                })
            );
    }

    register(registerData: RegisterRequest): Observable<any> {
        return this.http.post(`${this.apiUrl}/register`, registerData)
            .pipe(
                catchError(error => {
                    return throwError(() => error);
                })
            );
    }

    refreshToken(): Observable<AuthResponse> {
        return this.http
            .post<AuthResponse>(`${this.apiUrl}/refresh`, {}, {withCredentials: true})
            .pipe(tap(res => this.handleAccessToken(res)));
    }


    logout(): Observable<any> {
        return this.http.post(`${this.apiUrl}/logout`, {}, {withCredentials: true})
            .pipe(
                tap(() => {
                    this.token = null;
                    this.currentUserSubject.next(null);
                }),
                catchError(error => {
                    return throwError(() => error);
                })
            );
    }

    isAuthenticated(): boolean {
        return this.token !== null && !this.jwtHelper.isTokenExpired(this.token);
    }

    getToken(): string | null {
        return this.token
    }

    private handleAccessToken(response: AuthResponse) {
        this.token = response.accessToken;
        const decodedToken = this.jwtHelper.decodeToken(this.token);
        this.currentUserSubject.next({
            username: decodedToken.username,
            roles: decodedToken.roles
        });
    }
}
