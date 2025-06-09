import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PasswordResetService } from './password-reset.service';
import { PasswordResetRequest, PasswordReset, TokenValidation } from '../models/password-reset.model';
import { environment } from '../../../environments/environment';

describe('PasswordResetService', () => {
  let service: PasswordResetService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PasswordResetService]
    });

    service = TestBed.inject(PasswordResetService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {


    expect(service).toBeTruthy();
  });

  it('should request password reset successfully', () => {
    const request: PasswordResetRequest = { username: 'testuser' };
    const mockResponse = { message: 'Password reset email sent' };

    service.requestPasswordReset(request).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/password/reset-request`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);

    req.flush(mockResponse);
  });

  it('should reset password successfully', () => {
    const resetData: PasswordReset = {
      token: 'reset-token',
      newPassword: 'newpassword123'
    };
    const mockResponse = { message: 'Password reset successful' };

    service.resetPassword(resetData).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/password/reset`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(resetData);

    req.flush(mockResponse);
  });

  it('should validate token successfully', () => {
    const token = 'validation-token';
    const mockResponse: TokenValidation = { valid: true };

    service.validateToken(token).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/password/validate-token?token=${token}`);
    expect(req.request.method).toBe('GET');

    req.flush(mockResponse);
  });

  it('should handle validation token failure', () => {
    const token = 'invalid-token';
    const mockResponse: TokenValidation = { valid: false };

    service.validateToken(token).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/password/validate-token?token=${token}`);
    expect(req.request.method).toBe('GET');

    req.flush(mockResponse);
  });
});
