import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../../core/services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { TranslocoPipe } from '@jsverse/transloco';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy } from '@angular/core';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceMock: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authServiceMock = jasmine.createSpyObj('AuthService', ['login']);
    
    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        RouterTestingModule,
        LoginComponent
      ],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParams: {
                returnUrl: '/dashboard',
                registered: 'true'
              }
            }
          }
        },
        {
          provide: TranslocoPipe,
          useValue: {
            transform: (key: string) => key
          }
        }
      ]
    })
    .overrideComponent(LoginComponent, {
      set: { changeDetection: ChangeDetectionStrategy.Default }
    })
    .compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {

    expect(component).toBeTruthy();
  });

  it('should initialize the form with empty fields', () => {

    expect(component.loginForm.value).toEqual({
      username: '',
      password: ''
    });
  });

  it('should mark form as invalid when fields are empty', () => {

    expect(component.loginForm.valid).toBeFalsy();
  });

  it('should mark form as valid when all fields are filled', () => {

    component.loginForm.patchValue({
      username: 'testuser',
      password: 'password'
    });

    expect(component.loginForm.valid).toBeTruthy();
  });

  it('should not call login when form is invalid', () => {

    component.onSubmit();

    expect(authServiceMock.login).not.toHaveBeenCalled();
  });

  it('should call login when form is valid', () => {
    authServiceMock.login.and.returnValue(of({
      accessToken: 'test-token',
      username: 'testuser',
      roles: ['USER']
    }));

    component.loginForm.patchValue({
      username: 'testuser',
      password: 'password'
    });

    component.onSubmit();
    
    expect(authServiceMock.login).toHaveBeenCalledWith({
      username: 'testuser',
      password: 'password'
    });
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
  });

  it('should display error message on login failure', () => {
    const errorResponse = new HttpErrorResponse({
      error: { message: 'Invalid credentials' },
      status: 401,
      statusText: 'Unauthorized'
    });
    
    authServiceMock.login.and.returnValue(throwError(() => errorResponse));

    component.loginForm.patchValue({
      username: 'testuser',
      password: 'password'
    });

    component.onSubmit();
    
    expect(component.error).toBe('Invalid credentials');
    expect(component.loading).toBeFalse();
  });

  it('should set returnUrl from query params', () => {

    expect(component.returnUrl).toBe('/dashboard');
  });

  it('should set isRegistered from query params', () => {

    expect(component.isRegistered).toBeTrue();
  });
});
