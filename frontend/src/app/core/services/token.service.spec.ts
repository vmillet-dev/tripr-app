import {TestBed} from '@angular/core/testing';
import {TokenService} from './token.service';
import {JwtHelperService} from '@auth0/angular-jwt';
import {vi, describe, it, expect, beforeEach} from 'vitest';

describe('TokenService', () => {
    let service: TokenService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [TokenService]
        });
        service = TestBed.inject(TokenService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should set and get token', () => {
        const token = 'fake-token';
        vi.spyOn(JwtHelperService.prototype, 'decodeToken').mockReturnValue({ sub: 'testuser', roles: ['ROLE_USER'] });

        service.setToken(token);
        expect(service.getToken()).toBe(token);
        expect(service.currentUser()).toEqual({ username: 'testuser', roles: ['ROLE_USER'] });
    });

    it('should clear token', () => {
        vi.spyOn(JwtHelperService.prototype, 'decodeToken').mockReturnValue({ sub: 'testuser' });
        service.setToken('token');
        service.clearToken();
        expect(service.getToken()).toBeNull();
        expect(service.currentUser()).toBeNull();
    });

    it('should check if authenticated', () => {
        vi.spyOn(JwtHelperService.prototype, 'isTokenExpired').mockReturnValue(false);
        service.setToken('valid-token');
        expect(service.isAuthenticated()).toBe(true);

        vi.spyOn(JwtHelperService.prototype, 'isTokenExpired').mockReturnValue(true);
        expect(service.isAuthenticated()).toBe(false);
    });

    it('should handle role check', () => {
        vi.spyOn(JwtHelperService.prototype, 'decodeToken').mockReturnValue({ sub: 'testuser', roles: ['ADMIN'] });
        service.setToken('token');
        expect(service.hasRole('ADMIN')).toBe(true);
        expect(service.hasRole('USER')).toBe(false);
    });

    it('should handle invalid token decoding', () => {
        vi.spyOn(JwtHelperService.prototype, 'decodeToken').mockImplementation(() => {
            throw new Error('Invalid token');
        });
        service.setToken('invalid-token');
        expect(service.getToken()).toBeNull();
        expect(service.currentUser()).toBeNull();
    });
});
