import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { JwtHelperService } from '@auth0/angular-jwt';
import { environment } from '../../../environments/environment';
import { AuthRequest, AuthResponse, RegisterRequest } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private jwtHelper = new JwtHelperService();
  private currentUserSubject = new BehaviorSubject<any>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  
  constructor(private http: HttpClient) {
    this.loadToken();
  }
  
  private loadToken(): void {
    const token = localStorage.getItem('access_token');
    if (token && !this.jwtHelper.isTokenExpired(token)) {
      const decodedToken = this.jwtHelper.decodeToken(token);
      this.currentUserSubject.next({
        username: decodedToken.username,
        roles: decodedToken.roles
      });
    }
  }
  
  login(credentials: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials, { withCredentials: true })
      .pipe(
        tap(response => {
          localStorage.setItem('access_token', response.accessToken);
          this.currentUserSubject.next({
            username: response.username,
            roles: response.roles
          });
        }),
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
    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, {}, { withCredentials: true })
      .pipe(
        tap(response => {
          localStorage.setItem('access_token', response.accessToken);
          this.currentUserSubject.next({
            username: response.username,
            roles: response.roles
          });
        }),
        catchError(error => {
          return throwError(() => error);
        })
      );
  }
  
  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {}, { withCredentials: true })
      .pipe(
        tap(() => {
          localStorage.removeItem('access_token');
          this.currentUserSubject.next(null);
        }),
        catchError(error => {
          return throwError(() => error);
        })
      );
  }
  
  isAuthenticated(): boolean {
    const token = localStorage.getItem('access_token');
    return token !== null && !this.jwtHelper.isTokenExpired(token);
  }
  
  getToken(): string | null {
    return localStorage.getItem('access_token');
  }
}
