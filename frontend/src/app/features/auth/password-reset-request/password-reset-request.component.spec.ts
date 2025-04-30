import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { PasswordResetRequestComponent } from './password-reset-request.component';
import { PasswordResetService } from '../../../core/services/password-reset.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy } from '@angular/core';

describe('PasswordResetRequestComponent', () => {
  let component: PasswordResetRequestComponent;
  let fixture: ComponentFixture<PasswordResetRequestComponent>;
  let passwordResetServiceMock: jasmine.SpyObj<PasswordResetService>;
  let router: Router;

  beforeEach(async () => {
    passwordResetServiceMock = jasmine.createSpyObj('PasswordResetService', ['requestPasswordReset']);
    
    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        RouterTestingModule,
        PasswordResetRequestComponent
      ],
      providers: [
        { provide: PasswordResetService, useValue: passwordResetServiceMock }
      ]
    })
    .overrideComponent(PasswordResetRequestComponent, {
      set: { changeDetection: ChangeDetectionStrategy.Default }
    })
    .compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    
    fixture = TestBed.createComponent(PasswordResetRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {

    expect(component).toBeTruthy();
  });

  it('should initialize the form with empty username field', () => {

    expect(component.resetForm.value).toEqual({
      username: ''
    });
  });

  it('should mark form as invalid when username is empty', () => {

    expect(component.resetForm.valid).toBeFalsy();
  });

  it('should mark form as valid when username is provided', () => {

    component.resetForm.patchValue({
      username: 'testuser'
    });

    expect(component.resetForm.valid).toBeTruthy();
  });

  it('should not call requestPasswordReset when form is invalid', () => {

    component.onSubmit();

    expect(passwordResetServiceMock.requestPasswordReset).not.toHaveBeenCalled();
  });

  it('should call requestPasswordReset when form is valid', () => {
    passwordResetServiceMock.requestPasswordReset.and.returnValue(of({ message: 'Reset email sent' }));

    component.resetForm.patchValue({
      username: 'testuser'
    });

    component.onSubmit();
    
    expect(passwordResetServiceMock.requestPasswordReset).toHaveBeenCalledWith({
      username: 'testuser'
    });
    expect(component.successMessage).toBe('Reset email sent');
    expect(component.isSubmitting).toBeFalse();
  });

  it('should display error message on request failure', () => {
    const errorResponse = new HttpErrorResponse({
      error: { message: 'Username not found' },
      status: 400,
      statusText: 'Bad Request'
    });
    
    passwordResetServiceMock.requestPasswordReset.and.returnValue(throwError(() => errorResponse));

    component.resetForm.patchValue({
      username: 'testuser'
    });

    component.onSubmit();
    
    expect(component.errorMessage).toBe('Username not found');
    expect(component.isSubmitting).toBeFalse();
    expect(component.successMessage).toBe('');
  });

  it('should reset form after successful submission', () => {
    passwordResetServiceMock.requestPasswordReset.and.returnValue(of({ message: 'Reset email sent' }));

    component.resetForm.patchValue({
      username: 'testuser'
    });

    component.onSubmit();
    
    expect(component.resetForm.value).toEqual({
      username: null
    });
  });

  it('should navigate to login page when navigateToLogin is called', () => {

    component.navigateToLogin();
    
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});
