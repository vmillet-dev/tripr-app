import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../../core/services/auth.service';
import { AuthResponse } from '../../../core/models/auth.model';
import { getTranslocoModule } from '../../../transloco/testing/transloco-testing.module';
import { HttpErrorResponse } from '@angular/common/http';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let activatedRouteSpy: jasmine.SpyObj<ActivatedRoute>;

  const mockAuthResponse: AuthResponse = {
    accessToken: 'mock-token',
    username: 'testuser',
    roles: ['ROLE_USER']
  };

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', ['login']);
    authSpy.login.and.returnValue(of(mockAuthResponse));
    const routerSpyObj = jasmine.createSpyObj('Router', ['navigate']);
    const routeSpy = jasmine.createSpyObj('ActivatedRoute', [], {
      snapshot: {
        queryParams: { returnUrl: '/dashboard', registered: 'true' }
      }
    });

    await TestBed.configureTestingModule({
      imports: [LoginComponent, ReactiveFormsModule, getTranslocoModule()],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpyObj },
        { provide: ActivatedRoute, useValue: routeSpy }
      ]
    }).compileComponents();

    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    activatedRouteSpy = TestBed.inject(ActivatedRoute) as jasmine.SpyObj<ActivatedRoute>;
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {


    expect(component).toBeTruthy();
  });

  it('should initialize form with empty values', () => {


    expect(component.loginForm.get('username')?.value).toBe('');
    expect(component.loginForm.get('password')?.value).toBe('');
  });

  it('should set return URL and registration status on init', () => {

    component.ngOnInit();

    expect(component.returnUrl).toBe('/dashboard');
    expect(component.isRegistered).toBe(true);
  });

  it('should not submit when form is invalid', () => {
    component.loginForm.patchValue({ username: '', password: '' });

    component.onSubmit();

    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });

  it('should login successfully and navigate to return URL', () => {
    component.loginForm.patchValue({ username: 'testuser', password: 'password' });
    authServiceSpy.login.and.returnValue(of(mockAuthResponse));

    component.onSubmit();

    expect(component.loading).toBe(true);
    expect(authServiceSpy.login).toHaveBeenCalledWith({
      username: 'testuser',
      password: 'password'
    });
    expect(routerSpy.navigate).toHaveBeenCalledWith([component.returnUrl]);
  });

  it('should handle login error', () => {
    component.loginForm.patchValue({ username: 'testuser', password: 'wrongpassword' });
    const errorResponse = new HttpErrorResponse({
      error: { message: 'Invalid credentials' },
      status: 401
    });
    authServiceSpy.login.and.returnValue(throwError(() => errorResponse));

    component.onSubmit();

    expect(component.error).toBe('Invalid credentials');
    expect(component.loading).toBe(false);
  });

  it('should handle login error without message', () => {
    component.loginForm.patchValue({ username: 'testuser', password: 'wrongpassword' });
    const errorResponse = new HttpErrorResponse({ status: 500 });
    authServiceSpy.login.and.returnValue(throwError(() => errorResponse));

    component.onSubmit();

    expect(component.error).toBe('Login failed');
    expect(component.loading).toBe(false);
  });
});
