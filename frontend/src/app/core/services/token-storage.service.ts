import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {
  private token: string | null = null;
  
  /**
   * Stores the authentication token in memory
   * @param token The token to store
   */
  setToken(token: string): void {
    this.token = token;
  }
  
  /**
   * Retrieves the stored authentication token from memory
   * @returns The stored token or null if not found
   */
  getToken(): string | null {
    return this.token;
  }
  
  /**
   * Removes the stored authentication token from memory
   */
  removeToken(): void {
    this.token = null;
  }
  
  /**
   * Checks if a token exists and is not expired
   * @param jwtHelper JWT Helper service to check token expiration
   * @returns True if a valid token exists
   */
  hasValidToken(jwtHelper: any): boolean {
    return this.token !== null && !jwtHelper.isTokenExpired(this.token);
  }
}
