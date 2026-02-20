export interface LoginCredentials {
    username: string;
    password: string;
}

export interface RegisterData {
    username: string;
    password: string;
    email: string;
}

export interface AuthTokens {
    accessToken: string;
}

export interface CurrentUser {
    username: string;
    roles: string[];
}

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

export interface ApiMessage {
    message: string;
}
