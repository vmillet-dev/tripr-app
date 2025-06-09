import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../../core/services/auth.service';
import { getTranslocoModule } from '../../../transloco/testing/transloco-testing.module';
import { HttpErrorResponse } from '@angular/common/http';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', ['register']);
    const routerSpyObj = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [RegisterComponent, ReactiveFormsModule, getTranslocoModule()],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpyObj }
      ]
    }).compileComponents();

    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {


    expect(component).toBeTruthy();
  });

  it('should initialize form with empty values', () => {


    expect(component.registerForm.get('username')?.value).toBe('');
    expect(component.registerForm.get('email')?.value).toBe('');
    expect(component.registerForm.get('password')?.value).toBe('');
    expect(component.registerForm.get('confirmPassword')?.value).toBe('');
  });

  it('should validate password match', () => {
    component.registerForm.patchValue({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'differentpassword'
    });

    component.registerForm.updateValueAndValidity();

    expect(component.registerForm.get('confirmPassword')?.hasError('passwordMismatch')).toBe(true);
  });

  it('should not submit when form is invalid', () => {
    component.registerForm.patchValue({
      username: '',
      email: 'invalid-email',
      password: '123',
      confirmPassword: '456'
    });

    component.onSubmit();

    expect(authServiceSpy.register).not.toHaveBeenCalled();
  });

  it('should register successfully and navigate to login', () => {
    component.registerForm.patchValue({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });
    authServiceSpy.register.and.returnValue(of({ message: 'Registration successful' }));

    component.onSubmit();

    expect(component.loading).toBe(true);
    expect(authServiceSpy.register).toHaveBeenCalledWith({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123'
    });
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login'], { queryParams: { registered: true } });
  });

  it('should handle registration error', () => {
    component.registerForm.patchValue({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });
    const errorResponse = new HttpErrorResponse({
      error: { message: 'Username already exists' },
      status: 400
    });
    authServiceSpy.register.and.returnValue(throwError(() => errorResponse));

    component.onSubmit();

    expect(component.error).toBe('Username already exists');
    expect(component.loading).toBe(false);
  });
});
