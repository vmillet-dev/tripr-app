import { FormBuilder } from '@angular/forms';
import { passwordMatchValidator } from './password-match.validator';

describe('passwordMatchValidator', () => {
  let formBuilder: FormBuilder;

  beforeEach(() => {
    formBuilder = new FormBuilder();
  });

  it('should return null when passwords match', () => {
    const form = formBuilder.group({
      password: ['password123'],
      confirmPassword: ['password123']
    }, { validators: passwordMatchValidator() });

    const result = passwordMatchValidator()(form);

    expect(result).toBeNull();
    expect(form.get('confirmPassword')?.errors).toBeNull();
  });

  it('should return error when passwords do not match', () => {
    const form = formBuilder.group({
      password: ['password123'],
      confirmPassword: ['differentpassword']
    }, { validators: passwordMatchValidator() });

    const result = passwordMatchValidator()(form);

    expect(result).toEqual({ passwordMismatch: true });
    expect(form.get('confirmPassword')?.errors).toEqual({ passwordMismatch: true });
  });

  it('should return null when password controls are missing', () => {
    const form = formBuilder.group({
      username: ['testuser']
    }, { validators: passwordMatchValidator() });

    const result = passwordMatchValidator()(form);

    expect(result).toBeNull();
  });

  it('should clear passwordMismatch error when passwords match after mismatch', () => {
    const form = formBuilder.group({
      password: ['password123'],
      confirmPassword: ['differentpassword']
    }, { validators: passwordMatchValidator() });

    passwordMatchValidator()(form);
    expect(form.get('confirmPassword')?.errors).toEqual({ passwordMismatch: true });

    form.get('confirmPassword')?.setValue('password123');
    const result = passwordMatchValidator()(form);

    expect(result).toBeNull();
    expect(form.get('confirmPassword')?.errors).toBeNull();
  });
});
