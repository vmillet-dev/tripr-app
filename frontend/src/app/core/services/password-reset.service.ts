import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { PasswordResetRequest, PasswordReset, TokenValidation } from '../models/password-reset.model';

@Injectable({
  providedIn: 'root'
})
export class PasswordResetService {
  private apiUrl = `${environment.apiUrl}/auth/password`;
  private http = inject(HttpClient);
  
  requestPasswordReset(request: PasswordResetRequest): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/reset-request`, request)
      .pipe(
        catchError(error => {
          return throwError(() => error);
        })
      );
  }
  
  resetPassword(resetData: PasswordReset): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/reset`, resetData)
      .pipe(
        catchError(error => {
          return throwError(() => error);
        })
      );
  }
  
  validateToken(token: string): Observable<TokenValidation> {
    return this.http.get<TokenValidation>(`${this.apiUrl}/validate-token?token=${token}`)
      .pipe(
        catchError(error => {
          return throwError(() => error);
        })
      );
  }
}
