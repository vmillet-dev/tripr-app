import {inject, Injectable} from '@angular/core';
import {map, Observable, throwError} from 'rxjs';
import {catchError, tap} from 'rxjs/operators';
import {
    ApiMessage,
    AuthTokens,
    CurrentUser,
    LoginCredentials,
    PasswordReset,
    PasswordResetRequest,
    RegisterData,
    TokenValidation
} from "../models/auth.model";
import {TokenService} from "./token.service";
import {AuthenticationService} from "../api/generated";

@Injectable({providedIn: 'root'})
export class AuthService {

    private readonly authApi = inject(AuthenticationService);
    private readonly tokenService = inject(TokenService);

    public readonly currentUser$: Observable<CurrentUser | null> = this.tokenService.currentUser$;

    login(credentials: LoginCredentials): Observable<AuthTokens> {
        return this.authApi.login({
            username: credentials.username,
            password: credentials.password,
        }).pipe(
            tap(res => this.tokenService.setToken(res.accessToken ?? '')),
            map(res => ({accessToken: res.accessToken ?? ''}))
        );
    }

    register(data: RegisterData): Observable<ApiMessage> {
        return this.authApi.register({
            username: data.username,
            password: data.password,
            email: data.email,
        }).pipe(
            map(res => ({message: res.message ?? ''}))
        );
    }

    refreshToken(): Observable<AuthTokens> {
        return this.authApi.refreshToken().pipe(
            tap(res => this.tokenService.setToken(res.accessToken ?? '')),
            map(res => ({accessToken: res.accessToken ?? ''}))
        );
    }

    logout(): Observable<ApiMessage> {
        return this.authApi.logout().pipe(
            tap(() => this.tokenService.clearToken()),
            map(res => ({message: res.message ?? ''})),
            catchError(err => {
                this.tokenService.clearToken(); // nettoyage même en cas d'erreur réseau
                return throwError(() => err);
            })
        );
    }

    requestPasswordReset(data: PasswordResetRequest): Observable<ApiMessage> {
        return this.authApi.requestPasswordReset({username: data.username}).pipe(
            map(res => ({message: res.message ?? ''}))
        );
    }

    resetPassword(data: PasswordReset): Observable<ApiMessage> {
        return this.authApi.resetPassword({
            token: data.token,
            newPassword: data.newPassword,
        }).pipe(
            map(res => ({message: res.message ?? ''}))
        );
    }

    validatePasswordResetToken(token: string): Observable<TokenValidation> {
        return this.authApi.validateToken(token).pipe(
            map(res => ({valid: res.valid ?? false}))
        );
    }

    isAuthenticated(): boolean {
        return this.tokenService.isAuthenticated();
    }

    getToken(): string | null {
        return this.tokenService.getToken();
    }
}
