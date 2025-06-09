import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { JwtHelperService } from '@auth0/angular-jwt';
import { AuthService } from './auth.service';
import { AuthRequest, AuthResponse, RegisterRequest, User } from '../models/auth.model';
import { environment } from '../../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let jwtHelperSpy: jasmine.SpyObj<JwtHelperService>;

  const mockUser: User = {
    username: 'testuser',
    roles: ['ROLE_USER']
  };

  const mockAuthResponse: AuthResponse = {
    accessToken: 'mock-token',
    username: 'testuser',
    roles: ['ROLE_USER']
  };

  beforeEach(() => {
    const spy = jasmine.createSpyObj('JwtHelperService', ['isTokenExpired', 'decodeToken']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AuthService,
        { provide: JwtHelperService, useValue: spy }
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    jwtHelperSpy = TestBed.inject(JwtHelperService) as jasmine.SpyObj<JwtHelperService>;
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {


    expect(service).toBeTruthy();
  });

  it('should load token on initialization when valid token exists', () => {
    const mockToken = 'valid-token';
    const mockDecodedToken = { username: 'testuser', roles: ['ROLE_USER'] };
    localStorage.setItem('access_token', mockToken);
    jwtHelperSpy.isTokenExpired.and.returnValue(false);
    jwtHelperSpy.decodeToken.and.returnValue(mockDecodedToken);

    const newService = TestBed.inject(AuthService);

    newService.currentUser$.subscribe(user => {
      expect(user).toEqual(mockUser);
    });
  });

  it('should not load token when token is expired', () => {
    const mockToken = 'expired-token';
    localStorage.setItem('access_token', mockToken);
    jwtHelperSpy.isTokenExpired.and.returnValue(true);

    const newService = TestBed.inject(AuthService);

    newService.currentUser$.subscribe(user => {
      expect(user).toBeNull();
    });
  });

  it('should login successfully', () => {
    const credentials: AuthRequest = { username: 'testuser', password: 'password' };
    let currentUser: User | null = null;

    service.currentUser$.subscribe(user => currentUser = user);

    service.login(credentials).subscribe(response => {
      expect(response).toEqual(mockAuthResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(credentials);
    expect(req.request.withCredentials).toBe(true);

    req.flush(mockAuthResponse);

    expect(localStorage.getItem('access_token')).toBe(mockAuthResponse.accessToken);
    expect(currentUser).toEqual(mockUser);
  });

  it('should register successfully', () => {
    const registerData: RegisterRequest = {
      username: 'testuser',
      password: 'password',
      email: 'test@example.com'
    };
    const mockResponse = { message: 'Registration successful' };

    service.register(registerData).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/register`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(registerData);

    req.flush(mockResponse);
  });

  it('should refresh token successfully', () => {
    let currentUser: User | null = null;
    service.currentUser$.subscribe(user => currentUser = user);

    service.refreshToken().subscribe(response => {
      expect(response).toEqual(mockAuthResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/refresh`);
    expect(req.request.method).toBe('POST');
    expect(req.request.withCredentials).toBe(true);

    req.flush(mockAuthResponse);

    expect(localStorage.getItem('access_token')).toBe(mockAuthResponse.accessToken);
    expect(currentUser).toEqual(mockUser);
  });

  it('should logout successfully', () => {
    localStorage.setItem('access_token', 'some-token');
    let currentUser: User | null = mockUser;
    service.currentUser$.subscribe(user => currentUser = user);
    const mockResponse = { message: 'Logout successful' };

    service.logout().subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/logout`);
    expect(req.request.method).toBe('POST');
    expect(req.request.withCredentials).toBe(true);

    req.flush(mockResponse);

    expect(localStorage.getItem('access_token')).toBeNull();
    expect(currentUser).toBeNull();
  });

  it('should check authentication status correctly', () => {
    const mockToken = 'valid-token';
    localStorage.setItem('access_token', mockToken);
    jwtHelperSpy.isTokenExpired.and.returnValue(false);

    const isAuthenticated = service.isAuthenticated();

    expect(isAuthenticated).toBe(true);
    expect(jwtHelperSpy.isTokenExpired).toHaveBeenCalledWith(mockToken);
  });

  it('should return false for authentication when token is expired', () => {
    const mockToken = 'expired-token';
    localStorage.setItem('access_token', mockToken);
    jwtHelperSpy.isTokenExpired.and.returnValue(true);

    const isAuthenticated = service.isAuthenticated();

    expect(isAuthenticated).toBe(false);
  });

  it('should get token from localStorage', () => {
    const mockToken = 'test-token';
    localStorage.setItem('access_token', mockToken);

    const token = service.getToken();

    expect(token).toBe(mockToken);
  });
});
