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
import {
    AuthenticationService,
    AuthRequestDto,
    PasswordResetDto,
    PasswordResetRequestDto,
    RegisterRequestDto
} from "../api/generated";

@Injectable({providedIn: 'root'})
export class AuthService {

    private readonly authApi = inject(AuthenticationService);
    private readonly tokenService = inject(TokenService);

    public readonly currentUser$: Observable<CurrentUser | null> = this.tokenService.currentUser$;

    login(credentials: LoginCredentials): Observable<AuthTokens> {
        const dto: AuthRequestDto = {
            username: credentials.username,
            password: credentials.password,
        };

        return this.authApi.login(dto).pipe(
            tap(res => this.tokenService.setToken(res.accessToken ?? '')),
            map(res => ({accessToken: res.accessToken ?? ''})),
            catchError(err => throwError(() => err))
        );
    }

    register(data: RegisterData): Observable<ApiMessage> {
        const dto: RegisterRequestDto = {
            username: data.username,
            password: data.password,
            email: data.email,
        };

        return this.authApi.register(dto).pipe(
            map(res => ({message: res.message ?? ''})),
            catchError(err => throwError(() => err))
        );
    }

    refreshToken(): Observable<AuthTokens> {
        return this.authApi.refreshToken().pipe(
            tap(res => this.tokenService.setToken(res.accessToken ?? '')),
            map(res => ({accessToken: res.accessToken ?? ''})),
            catchError(err => throwError(() => err))
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
        const dto: PasswordResetRequestDto = {username: data.username};

        return this.authApi.requestPasswordReset(dto).pipe(
            map(res => ({message: res.message ?? ''})),
            catchError(err => throwError(() => err))
        );
    }

    resetPassword(data: PasswordReset): Observable<ApiMessage> {
        const dto: PasswordResetDto = {
            token: data.token,
            newPassword: data.newPassword,
        };

        return this.authApi.resetPassword(dto).pipe(
            map(res => ({message: res.message ?? ''})),
            catchError(err => throwError(() => err))
        );
    }

    validatePasswordResetToken(token: string): Observable<TokenValidation> {
        return this.authApi.validateToken(token).pipe(
            map(res => ({valid: res.valid ?? false})),
            catchError(err => throwError(() => err))
        );
    }

    isAuthenticated(): boolean {
        return this.tokenService.isAuthenticated();
    }

    getToken(): string | null {
        return this.tokenService.getToken();
    }
}
