import {TestBed} from '@angular/core/testing';
import {TokenService} from './token.service';
import {vi} from 'vitest';

// Mock JwtHelperService since it's used inside TokenService
vi.mock('@auth0/angular-jwt', () => {
    class MockJwtHelperService {
        isTokenExpired = vi.fn().mockReturnValue(false);
        decodeToken = vi.fn().mockReturnValue({sub: 'testuser', roles: ['ROLE_USER']});
    }

    return {
        JwtHelperService: MockJwtHelperService
    };
});

describe('TokenService', () => {
    let service: TokenService;
    let jwtHelper: any;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [TokenService]
        });
        service = TestBed.inject(TokenService);
        jwtHelper = (service as any).jwtHelper;
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should set and get token', () => {
        service.setToken('my-token');
        expect(service.getToken()).toBe('my-token');
    });

    it('should decode user and update currentUser$ when setting token', () => {
        return new Promise<void>((resolve) => {
            service.currentUser$.subscribe(user => {
                if (user) {
                    expect(user.username).toBe('testuser');
                    expect(user.roles).toContain('ROLE_USER');
                    resolve();
                }
            });
            service.setToken('some-token');
        });
    });

    it('should clear token and update currentUser$', () => {
        service.setToken('token');
        return new Promise<void>((resolve) => {
            let callCount = 0;
            service.currentUser$.subscribe(user => {
                callCount++;
                if (callCount === 2) {
                    expect(user).toBeNull();
                    expect(service.getToken()).toBeNull();
                    resolve();
                }
            });
            service.clearToken();
        });
    });

    describe('isAuthenticated', () => {
        it('should return false if no token', () => {
            expect(service.isAuthenticated()).toBe(false);
        });

        it('should return false if token is expired', () => {
            service.setToken('expired-token');
            vi.spyOn(jwtHelper, 'isTokenExpired').mockReturnValue(true);
            expect(service.isAuthenticated()).toBe(false);
        });

        it('should return true if token is valid and not expired', () => {
            service.setToken('valid-token');
            vi.spyOn(jwtHelper, 'isTokenExpired').mockReturnValue(false);
            expect(service.isAuthenticated()).toBe(true);
        });
    });

    describe('isExpired', () => {
        it('should return true if no token', () => {
            expect(service.isExpired()).toBe(true);
        });

        it('should catch errors and return true', () => {
            service.setToken('bad-token');
            vi.spyOn(jwtHelper, 'isTokenExpired').mockImplementation(() => {
                throw new Error('Invalid token');
            });
            expect(service.isExpired()).toBe(true);
        });
    });

    describe('hasRole', () => {
        it('should return true if user has role', () => {
            service.setToken('token');
            expect(service.hasRole('ROLE_USER')).toBe(true);
        });

        it('should return false if user does not have role', () => {
            service.setToken('token');
            expect(service.hasRole('ROLE_ADMIN')).toBe(false);
        });

        it('should return false if no user', () => {
            expect(service.hasRole('ROLE_USER')).toBe(false);
        });
    });

    it('should handle decode error', () => {
        vi.spyOn(jwtHelper, 'decodeToken').mockImplementation(() => {
            throw new Error('Decode error');
        });
        service.setToken('garbage');
        expect(service.getCurrentUser()).toBeNull();
        expect(service.getToken()).toBeNull();
    });
});
