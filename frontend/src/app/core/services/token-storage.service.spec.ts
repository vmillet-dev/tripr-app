import { TestBed } from '@angular/core/testing';
import { TokenStorageService } from './token-storage.service';

describe('TokenStorageService', () => {
  let service: TokenStorageService;
  let jwtHelperMock: any;

  beforeEach(() => {
    jwtHelperMock = {
      isTokenExpired: jasmine.createSpy('isTokenExpired')
    };

    TestBed.configureTestingModule({
      providers: [TokenStorageService]
    });
    
    service = TestBed.inject(TokenStorageService);
  });

  it('should be created', () => {

    expect(service).toBeTruthy();
  });

  it('should store and retrieve token', () => {
    const testToken = 'test-token';
    
    service.setToken(testToken);
    
    expect(service.getToken()).toBe(testToken);
  });

  it('should remove token', () => {
    const testToken = 'test-token';
    service.setToken(testToken);
    
    service.removeToken();
    
    expect(service.getToken()).toBeNull();
  });

  it('should check if token is valid when token exists and not expired', () => {
    const testToken = 'test-token';
    service.setToken(testToken);
    jwtHelperMock.isTokenExpired.and.returnValue(false);
    
    const result = service.hasValidToken(jwtHelperMock);
    
    expect(result).toBeTrue();
    expect(jwtHelperMock.isTokenExpired).toHaveBeenCalledWith(testToken);
  });

  it('should check if token is valid when token exists but is expired', () => {
    const testToken = 'test-token';
    service.setToken(testToken);
    jwtHelperMock.isTokenExpired.and.returnValue(true);
    
    const result = service.hasValidToken(jwtHelperMock);
    
    expect(result).toBeFalse();
    expect(jwtHelperMock.isTokenExpired).toHaveBeenCalledWith(testToken);
  });

  it('should check if token is valid when token does not exist', () => {
    service.removeToken();
    
    const result = service.hasValidToken(jwtHelperMock);
    
    expect(result).toBeFalse();
    expect(jwtHelperMock.isTokenExpired).not.toHaveBeenCalled();
  });
});
