import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { ActivatedRoute, Router, RouterModule, provideRouter } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { of, throwError } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { getTranslocoModule } from '../../../transloco/testing/transloco-testing.module';
import { provideLocationMocks } from '@angular/common/testing';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', ['register']);
    const routerSpyObj = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [
        RegisterComponent,
        ReactiveFormsModule,
        RouterModule,
        getTranslocoModule()
      ],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpyObj },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParams: {}
            }
          }
        },
        provideRouter([]),
        provideLocationMocks()
      ]
    }).compileComponents();

    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with empty fields', () => {
    expect(component.registerForm.get('username')?.value).toBe('');
    expect(component.registerForm.get('email')?.value).toBe('');
    expect(component.registerForm.get('password')?.value).toBe('');
    expect(component.registerForm.get('confirmPassword')?.value).toBe('');
  });

  it('should mark form as invalid when empty', () => {
    expect(component.registerForm.valid).toBeFalsy();
  });

  it('should validate email format', () => {
    const emailControl = component.registerForm.get('email');
    emailControl?.setValue('invalid-email');
    expect(emailControl?.errors?.['email']).toBeTruthy();

    emailControl?.setValue('valid@email.com');
    expect(emailControl?.errors?.['email']).toBeFalsy();
  });

  it('should validate password length', () => {
    const passwordControl = component.registerForm.get('password');
    passwordControl?.setValue('short');
    expect(passwordControl?.errors?.['minlength']).toBeTruthy();

    passwordControl?.setValue('password123');
    expect(passwordControl?.errors?.['minlength']).toBeFalsy();
  });

  it('should validate password match', () => {
    component.registerForm.patchValue({
      password: 'password123',
      confirmPassword: 'different'
    });
    
    expect(component.registerForm.errors?.['passwordMismatch']).toBeTruthy();

    component.registerForm.patchValue({
      confirmPassword: 'password123'
    });
    
    expect(component.registerForm.errors?.['passwordMismatch']).toBeFalsy();
  });

  it('should call auth service and navigate on successful registration', () => {
    authServiceSpy.register.and.returnValue(of({ success: true }));

    component.registerForm.patchValue({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });

    component.onSubmit();

    expect(authServiceSpy.register).toHaveBeenCalledWith({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123'
    });
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/auth/login'], { queryParams: { registered: true } });
  });

  it('should display error message on registration failure', () => {
    authServiceSpy.register.and.returnValue(throwError(() => ({ error: { message: 'Username already exists' } })));

    component.registerForm.patchValue({
      username: 'existinguser',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });

    component.onSubmit();

    expect(authServiceSpy.register).toHaveBeenCalled();
    expect(component.error).toBe('Username already exists');
    expect(component.loading).toBe(false);
  });
});
