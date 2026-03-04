import {JwtHelperService} from "@auth0/angular-jwt";
import {CurrentUser} from "../models/auth.model";
import {Injectable, signal} from "@angular/core";
import {toObservable} from "@angular/core/rxjs-interop";

@Injectable({providedIn: 'root'})
export class TokenService {

    private readonly jwtHelper = new JwtHelperService();

    // Token volatile en mémoire (jamais en localStorage → protection XSS)
    private token: string | null = null;

    private readonly currentUserSignal = signal<CurrentUser | null>(null);
    public readonly currentUser = this.currentUserSignal.asReadonly();
    public readonly currentUser$ = toObservable(this.currentUser);

    setToken(accessToken: string): void {
        this.token = accessToken;
        this.currentUserSignal.set(this.decodeUser(accessToken));
    }

    getToken(): string | null {
        return this.token;
    }

    clearToken(): void {
        this.token = null;
        this.currentUserSignal.set(null);
    }

    isAuthenticated(): boolean {
        return this.token !== null && !this.isExpired();
    }

    isExpired(): boolean {
        if (!this.token) return true;
        try {
            return this.jwtHelper.isTokenExpired(this.token);
        } catch {
            return true;
        }
    }

    getCurrentUser(): CurrentUser | null {
        return this.currentUser();
    }

    hasRole(role: string): boolean {
        return this.getCurrentUser()?.roles?.includes(role) ?? false;
    }

    private decodeUser(token: string): CurrentUser | null {
        try {
            const decoded = this.jwtHelper.decodeToken(token);
            return {
                username: decoded.sub,
                roles: decoded.roles ?? [],
            };
        } catch {
            this.clearToken();
            return null;
        }
    }
}
