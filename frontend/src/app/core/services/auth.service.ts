import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { JwtHelperService } from '@auth0/angular-jwt';
import { environment } from '../../../environments/environment';
import { AuthRequest, AuthResponse, RegisterRequest } from '../models/auth.model';
import { TokenStorageService } from './token-storage.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private jwtHelper = new JwtHelperService();
  private currentUserSubject = new BehaviorSubject<{username: string, roles: string[]} | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  
  private http = inject(HttpClient);
  private tokenStorage = inject(TokenStorageService);
  
  constructor() {
    this.loadToken();
  }
  
  private loadToken(): void {
    const token = this.tokenStorage.getToken();
    if (token && !this.jwtHelper.isTokenExpired(token)) {
      const decodedToken = this.jwtHelper.decodeToken(token);
      this.currentUserSubject.next({
        username: decodedToken.username,
        roles: decodedToken.roles
      });
    } else {
      this.refreshToken().subscribe({
        next: () => {
        },
        error: () => {
        }
      });
    }
  }
  
  login(credentials: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials, { withCredentials: true })
      .pipe(
        tap((response: AuthResponse) => {
          this.tokenStorage.setToken(response.accessToken);
          this.currentUserSubject.next({
            username: response.username,
            roles: response.roles
          });
        }),
        catchError((error: any) => {
          return throwError(() => error);
        })
      );
  }
  
  register(registerData: RegisterRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, registerData)
      .pipe(
        catchError((error: any) => {
          return throwError(() => error);
        })
      );
  }
  
  refreshToken(): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, {}, { withCredentials: true })
      .pipe(
        tap((response: AuthResponse) => {
          this.tokenStorage.setToken(response.accessToken);
          this.currentUserSubject.next({
            username: response.username,
            roles: response.roles
          });
        }),
        catchError((error: any) => {
          return throwError(() => error);
        })
      );
  }
  
  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {}, { withCredentials: true })
      .pipe(
        tap(() => {
          this.tokenStorage.removeToken();
          this.currentUserSubject.next(null);
        }),
        catchError((error: any) => {
          return throwError(() => error);
        })
      );
  }
  
  isAuthenticated(): boolean {
    return this.tokenStorage.hasValidToken(this.jwtHelper);
  }
  
  getToken(): string | null {
    return this.tokenStorage.getToken();
  }
}
