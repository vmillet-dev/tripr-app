import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpClient } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { authInterceptor } from './auth.interceptor';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

describe('AuthInterceptor', () => {
  let httpClient: HttpClient;
  let httpMock: HttpTestingController;
  let authServiceMock: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authServiceMock = jasmine.createSpyObj('AuthService', 
      ['getToken', 'refreshToken', 'logout']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        provideHttpClient(withInterceptors([authInterceptor]))
      ]
    });

    httpClient = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should add authorization header if token exists', () => {
    authServiceMock.getToken.and.returnValue('test-token');

    httpClient.get('/api/test').subscribe();

    const req = httpMock.expectOne('/api/test');
    expect(req.request.headers.has('Authorization')).toBeTrue();
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush({});
  });

  it('should not add authorization header if token does not exist', () => {
    authServiceMock.getToken.and.returnValue(null);

    httpClient.get('/api/test').subscribe();

    const req = httpMock.expectOne('/api/test');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush({});
  });

  it('should attempt to refresh token when receiving 401 with FUNC_002 error', () => {
    authServiceMock.getToken.and.returnValue('test-token');
    authServiceMock.refreshToken.and.returnValue(of({ 
      accessToken: 'new-test-token', 
      username: 'testuser', 
      roles: ['USER'] 
    }));

    httpClient.get('/api/test').subscribe({
      next: (_response) => {},
      error: () => {}
    });

    const req = httpMock.expectOne('/api/test');
    req.flush('FUNC_002', { status: 401, statusText: 'Unauthorized' });
    
    expect(authServiceMock.refreshToken).toHaveBeenCalled();
  });

  it('should logout when token refresh fails', () => {
    authServiceMock.getToken.and.returnValue('test-token');
    authServiceMock.refreshToken.and.returnValue(throwError(() => new Error('Refresh failed')));

    httpClient.get('/api/test').subscribe({
      next: () => {},
      error: () => {}
    });

    const req = httpMock.expectOne('/api/test');
    req.flush('FUNC_002', { status: 401, statusText: 'Unauthorized' });
    
    expect(authServiceMock.logout).toHaveBeenCalled();
  });
});
