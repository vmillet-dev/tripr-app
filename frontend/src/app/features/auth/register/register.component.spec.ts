import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { TranslocoPipe } from '@jsverse/transloco';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy } from '@angular/core';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceMock: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authServiceMock = jasmine.createSpyObj('AuthService', ['register']);
    
    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        RouterTestingModule,
        RegisterComponent
      ],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        {
          provide: TranslocoPipe,
          useValue: {
            transform: (key: string) => key
          }
        }
      ]
    })
    .overrideComponent(RegisterComponent, {
      set: { changeDetection: ChangeDetectionStrategy.Default }
    })
    .compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {

    expect(component).toBeTruthy();
  });

  it('should initialize the form with empty fields', () => {

    expect(component.registerForm.value).toEqual({
      username: '',
      email: '',
      password: '',
      confirmPassword: ''
    });
  });

  it('should mark form as invalid when fields are empty', () => {

    expect(component.registerForm.valid).toBeFalsy();
  });

  it('should mark form as valid when all fields are filled correctly', () => {

    component.registerForm.patchValue({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });

    expect(component.registerForm.valid).toBeTruthy();
  });

  it('should detect password mismatch', () => {

    component.registerForm.patchValue({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'different'
    });

    expect(component.registerForm.get('confirmPassword')?.hasError('passwordMismatch')).toBeTrue();
    expect(component.registerForm.valid).toBeFalsy();
  });

  it('should not call register when form is invalid', () => {

    component.onSubmit();

    expect(authServiceMock.register).not.toHaveBeenCalled();
  });

  it('should call register when form is valid', () => {
    authServiceMock.register.and.returnValue(of({ message: 'Registration successful' }));

    component.registerForm.patchValue({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });

    component.onSubmit();
    
    expect(authServiceMock.register).toHaveBeenCalledWith({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123'
    });
    expect(router.navigate).toHaveBeenCalledWith(['/login'], { queryParams: { registered: 'true' } });
  });

  it('should display error message on registration failure', () => {
    const errorResponse = new HttpErrorResponse({
      error: { message: 'Username already exists' },
      status: 400,
      statusText: 'Bad Request'
    });
    
    authServiceMock.register.and.returnValue(throwError(() => errorResponse));

    component.registerForm.patchValue({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });

    component.onSubmit();
    
    expect(component.error).toBe('Username already exists');
    expect(component.loading).toBeFalse();
  });

  it('should validate email format', () => {

    component.registerForm.patchValue({
      username: 'testuser',
      email: 'invalid-email',
      password: 'password123',
      confirmPassword: 'password123'
    });

    expect(component.registerForm.get('email')?.valid).toBeFalsy();
    expect(component.registerForm.get('email')?.hasError('email')).toBeTrue();
  });

  it('should validate password length', () => {

    component.registerForm.patchValue({
      username: 'testuser',
      email: 'test@example.com',
      password: 'short',
      confirmPassword: 'short'
    });

    expect(component.registerForm.get('password')?.valid).toBeFalsy();
    expect(component.registerForm.get('password')?.hasError('minlength')).toBeTrue();
  });
});
