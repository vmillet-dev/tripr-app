import {JwtHelperService} from "@auth0/angular-jwt";
import {BehaviorSubject, Observable} from "rxjs";
import {CurrentUser} from "../models/auth.model";
import {Injectable} from "@angular/core";

@Injectable({providedIn: 'root'})
export class TokenService {

    private readonly jwtHelper = new JwtHelperService();

    // Token volatile en mémoire (jamais en localStorage → protection XSS)
    private token: string | null = null;

    private readonly currentUserSubject = new BehaviorSubject<CurrentUser | null>(null);
    public readonly currentUser$: Observable<CurrentUser | null> = this.currentUserSubject.asObservable();

    setToken(accessToken: string): void {
        this.token = accessToken;
        this.currentUserSubject.next(this.decodeUser(accessToken));
    }

    getToken(): string | null {
        return this.token;
    }

    clearToken(): void {
        this.token = null;
        this.currentUserSubject.next(null);
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

    getExpirationDate(): Date | null {
        if (!this.token) return null;
        try {
            return this.jwtHelper.getTokenExpirationDate(this.token);
        } catch {
            return null;
        }
    }

    getCurrentUser(): CurrentUser | null {
        return this.currentUserSubject.getValue();
    }

    hasRole(role: string): boolean {
        return this.getCurrentUser()?.roles?.includes(role) ?? false;
    }

    hasAnyRole(...roles: string[]): boolean {
        return roles.some(role => this.hasRole(role));
    }

    private decodeUser(token: string): CurrentUser | null {
        try {
            const decoded = this.jwtHelper.decodeToken(token);
            return {
                username: decoded.username,
                roles: decoded.roles ?? [],
            };
        } catch {
            this.clearToken();
            return null;
        }
    }
}
