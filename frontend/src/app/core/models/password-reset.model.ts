export interface PasswordResetRequest {
  username: string;
}

export interface PasswordReset {
  token: string;
  newPassword: string;
}

export interface TokenValidation {
  valid: boolean;
}
