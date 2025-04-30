import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PasswordResetService } from './password-reset.service';
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

  it('should request password reset', () => {
    const testRequest = { username: 'testuser' };
    const mockResponse = { message: 'Password reset email sent' };

    service.requestPasswordReset(testRequest).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/password/reset-request`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(testRequest);
    req.flush(mockResponse);
  });

  it('should reset password', () => {
    const testRequest = { token: 'reset-token', newPassword: 'newpassword' };
    const mockResponse = { message: 'Password reset successful' };

    service.resetPassword(testRequest).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/password/reset`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(testRequest);
    req.flush(mockResponse);
  });

  it('should validate token', () => {
    const testToken = 'reset-token';
    const mockResponse = { valid: true };

    service.validateToken(testToken).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/password/validate-token?token=${testToken}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should handle errors when requesting password reset', () => {
    const testRequest = { username: 'testuser' };
    const mockError = { status: 400, statusText: 'Bad Request' };

    service.requestPasswordReset(testRequest).subscribe({
      next: () => fail('Should have failed with the 400 error'),
      error: (error) => {
        expect(error).toBeTruthy();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/password/reset-request`);
    expect(req.request.method).toBe('POST');
    req.flush('Invalid email', mockError);
  });

  it('should handle errors when resetting password', () => {
    const testRequest = { token: 'reset-token', newPassword: 'newpassword' };
    const mockError = { status: 400, statusText: 'Bad Request' };

    service.resetPassword(testRequest).subscribe({
      next: () => fail('Should have failed with the 400 error'),
      error: (error) => {
        expect(error).toBeTruthy();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/password/reset`);
    expect(req.request.method).toBe('POST');
    req.flush('Invalid token', mockError);
  });

  it('should handle errors when validating token', () => {
    const testToken = 'reset-token';
    const mockError = { status: 400, statusText: 'Bad Request' };

    service.validateToken(testToken).subscribe({
      next: () => fail('Should have failed with the 400 error'),
      error: (error) => {
        expect(error).toBeTruthy();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/password/validate-token?token=${testToken}`);
    expect(req.request.method).toBe('GET');
    req.flush('Invalid token', mockError);
  });
});
