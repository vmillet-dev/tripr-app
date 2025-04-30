import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { TokenStorageService } from './token-storage.service';
import { environment } from '../../../environments/environment';
import { JwtHelperService } from '@auth0/angular-jwt';
import { of } from 'rxjs';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let tokenStorageServiceMock: jasmine.SpyObj<TokenStorageService>;
  let jwtHelperServiceMock: jasmine.SpyObj<JwtHelperService>;

  beforeEach(() => {
    tokenStorageServiceMock = jasmine.createSpyObj('TokenStorageService', 
      ['getToken', 'setToken', 'removeToken', 'hasValidToken']);
    
    tokenStorageServiceMock.getToken.and.returnValue('valid-test-token');
    
    jwtHelperServiceMock = jasmine.createSpyObj('JwtHelperService',
      ['decodeToken', 'isTokenExpired']);
    
    jwtHelperServiceMock.isTokenExpired.and.returnValue(Promise.resolve(false));
    jwtHelperServiceMock.decodeToken.and.returnValue(Promise.resolve({ username: 'testuser', roles: ['USER'] }));

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: TokenStorageService, useValue: tokenStorageServiceMock },
        { provide: JwtHelperService, useValue: jwtHelperServiceMock },
        AuthService
      ]
    });
    
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {

    expect(service).toBeTruthy();
  });

  it('should login user and store token', () => {
    const testCredentials = { username: 'testuser', password: 'password' };
    const mockResponse = { 
      accessToken: 'test-token', 
      username: 'testuser', 
      roles: ['USER']
    };

    service.login(testCredentials).subscribe(response => {
      expect(response).toEqual(mockResponse);
      expect(tokenStorageServiceMock.setToken).toHaveBeenCalledWith('test-token');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(testCredentials);
    req.flush(mockResponse);
  });

  it('should logout user and remove token', () => {
    
    service.logout().subscribe(() => {
      expect(tokenStorageServiceMock.removeToken).toHaveBeenCalled();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/logout`);
    expect(req.request.method).toBe('POST');
    req.flush({});
  });

  it('should refresh token', () => {
    const mockResponse = { 
      accessToken: 'new-test-token', 
      username: 'testuser', 
      roles: ['USER'] 
    };

    service.refreshToken().subscribe(response => {
      expect(response).toEqual(mockResponse);
      expect(tokenStorageServiceMock.setToken).toHaveBeenCalledWith('new-test-token');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/refresh`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should register user', () => {
    const testData = { username: 'testuser', email: 'test@example.com', password: 'password' };
    const mockResponse = { message: 'User registered successfully' };

    service.register(testData).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/register`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(testData);
    req.flush(mockResponse);
  });

  it('should check if user is authenticated', () => {
    tokenStorageServiceMock.hasValidToken.and.returnValue(true);
    
    const result = service.isAuthenticated();
    
    expect(result).toBeTrue();
    expect(tokenStorageServiceMock.hasValidToken).toHaveBeenCalled();
  });

  it('should get token', () => {
    tokenStorageServiceMock.getToken.and.returnValue('test-token');
    
    const token = service.getToken();
    
    expect(token).toBe('test-token');
    expect(tokenStorageServiceMock.getToken).toHaveBeenCalled();
  });

  it('should load user from token when valid token exists', () => {
    const testToken = 'test-token';
    const decodedToken = { username: 'testuser', roles: ['USER'] };
    
    tokenStorageServiceMock.getToken.and.returnValue(testToken);
    jwtHelperServiceMock.isTokenExpired.and.returnValue(Promise.resolve(false));
    jwtHelperServiceMock.decodeToken.and.returnValue(Promise.resolve(decodedToken));
    
    service = new AuthService();
    
    expect(tokenStorageServiceMock.getToken).toHaveBeenCalled();
    expect(jwtHelperServiceMock.isTokenExpired).toHaveBeenCalled();
    expect(jwtHelperServiceMock.decodeToken).toHaveBeenCalled();
  });

  it('should attempt to refresh token when no valid token exists', () => {
    tokenStorageServiceMock.getToken.and.returnValue(null);
    spyOn(service, 'refreshToken').and.returnValue(of({
      accessToken: 'refreshed-token',
      username: 'testuser',
      roles: ['USER']
    }));
    
    service = new AuthService();
    
    expect(tokenStorageServiceMock.getToken).toHaveBeenCalled();
    expect(service.refreshToken).toHaveBeenCalled();
  });

  it('should handle login error', () => {
    const testCredentials = { username: 'testuser', password: 'password' };
    const mockError = { status: 401, statusText: 'Unauthorized' };
    
    service.login(testCredentials).subscribe({
      next: () => fail('Should have failed with 401 error'),
      error: (error) => {
        expect(error).toBeTruthy();
        expect(tokenStorageServiceMock.setToken).not.toHaveBeenCalled();
      }
    });
    
    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush('Invalid credentials', mockError);
  });

  it('should handle refresh token error', () => {
    const mockError = { status: 401, statusText: 'Unauthorized' };
    
    service.refreshToken().subscribe({
      next: () => fail('Should have failed with 401 error'),
      error: (error) => {
        expect(error).toBeTruthy();
        expect(tokenStorageServiceMock.setToken).not.toHaveBeenCalled();
      }
    });
    
    const req = httpMock.expectOne(`${environment.apiUrl}/auth/refresh`);
    expect(req.request.method).toBe('POST');
    req.flush('Invalid refresh token', mockError);
  });
});
