import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { HeaderComponent } from './header.component';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/auth.model';
import { getTranslocoModule } from '../../../transloco/testing/transloco-testing.module';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  const mockUser: User = {
    username: 'testuser',
    roles: ['ROLE_USER']
  };

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', ['logout']);
    Object.defineProperty(authSpy, 'currentUser$', {
      value: of(mockUser),
      writable: true
    });
    const routerSpyObj = jasmine.createSpyObj('Router', ['navigate']);
    const activatedRouteSpy = jasmine.createSpyObj('ActivatedRoute', [], {
      snapshot: { queryParams: {} }
    });

    await TestBed.configureTestingModule({
      imports: [HeaderComponent, getTranslocoModule()],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpyObj },
        { provide: ActivatedRoute, useValue: activatedRouteSpy }
      ]
    }).compileComponents();

    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {


    expect(component).toBeTruthy();
  });

  it('should set authentication state and username on init', () => {

    component.ngOnInit();

    expect(component.isAuthenticated).toBe(true);
    expect(component.username).toBe(mockUser.username);
  });

  it('should set authentication state to false when no user', () => {
    Object.defineProperty(authServiceSpy, 'currentUser$', {
      value: of(null),
      writable: true
    });

    component.ngOnInit();

    expect(component.isAuthenticated).toBe(false);
    expect(component.username).toBeNull();
  });

  it('should logout successfully and navigate to home', () => {
    authServiceSpy.logout.and.returnValue(of({ message: 'Logout successful' }));

    component.logout();

    expect(authServiceSpy.logout).toHaveBeenCalled();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should handle logout error and still navigate to home', () => {
    authServiceSpy.logout.and.returnValue(throwError(() => new Error('Logout failed')));
    spyOn(console, 'error');

    component.logout();

    expect(authServiceSpy.logout).toHaveBeenCalled();
    expect(console.error).toHaveBeenCalledWith('Logout failed:', jasmine.any(Error));
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should unsubscribe on destroy', () => {
    component.ngOnInit();
    spyOn(component['subscription'], 'unsubscribe');

    component.ngOnDestroy();

    expect(component['subscription'].unsubscribe).toHaveBeenCalled();
  });
});
