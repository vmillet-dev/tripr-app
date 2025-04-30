import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('AuthGuard', () => {
  let authServiceMock: jasmine.SpyObj<AuthService>;
  let router: Router;
  let guard: typeof AuthGuard;

  beforeEach(() => {
    authServiceMock = jasmine.createSpyObj('AuthService', ['isAuthenticated']);
    
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: authServiceMock }
      ]
    });
    
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    guard = AuthGuard;
  });

  it('should allow access when user is authenticated', () => {
    authServiceMock.isAuthenticated.and.returnValue(true);
    
    const routeSnapshot = {} as ActivatedRouteSnapshot;
    const stateSnapshot = { url: '/dashboard' } as RouterStateSnapshot;
    
    const result = TestBed.runInInjectionContext(() => guard(routeSnapshot, stateSnapshot));
    
    expect(result).toBeTrue();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should redirect to login when user is not authenticated', () => {
    authServiceMock.isAuthenticated.and.returnValue(false);
    
    const routeSnapshot = {} as ActivatedRouteSnapshot;
    const stateSnapshot = { url: '/dashboard' } as RouterStateSnapshot;
    
    const result = TestBed.runInInjectionContext(() => guard(routeSnapshot, stateSnapshot));
    
    expect(result).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(
      ['/login'], 
      { queryParams: { returnUrl: '/dashboard' } }
    );
  });
});
