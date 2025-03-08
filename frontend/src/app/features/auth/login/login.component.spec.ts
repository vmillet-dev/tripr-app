import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { of, throwError } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { getTranslocoModule } from '../../../transloco/testing/transloco-testing.module';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', ['login']);
    const routerSpyObj = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [
        LoginComponent,
        ReactiveFormsModule,
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
        }
      ]
    }).compileComponents();

    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with empty fields', () => {
    expect(component.loginForm.get('username')?.value).toBe('');
    expect(component.loginForm.get('password')?.value).toBe('');
  });

  it('should mark form as invalid when empty', () => {
    expect(component.loginForm.valid).toBeFalsy();
  });

  it('should mark form as valid when all fields are filled', () => {
    component.loginForm.patchValue({
      username: 'testuser',
      password: 'password123'
    });
    expect(component.loginForm.valid).toBeTruthy();
  });

  it('should call auth service and navigate on successful login', () => {
    authServiceSpy.login.and.returnValue(of({
      accessToken: 'test-token',
      username: 'testuser',
      roles: ['USER']
    }));

    component.loginForm.patchValue({
      username: 'testuser',
      password: 'password123'
    });

    component.onSubmit();

    expect(authServiceSpy.login).toHaveBeenCalledWith({
      username: 'testuser',
      password: 'password123'
    });
    expect(routerSpy.navigate).toHaveBeenCalled();
  });

  it('should display error message on login failure', () => {
    authServiceSpy.login.and.returnValue(throwError(() => ({ error: { message: 'Invalid credentials' } })));

    component.loginForm.patchValue({
      username: 'testuser',
      password: 'wrongpassword'
    });

    component.onSubmit();

    expect(authServiceSpy.login).toHaveBeenCalled();
    expect(component.error).toBe('Invalid credentials');
    expect(component.loading).toBe(false);
  });
});
