export interface AuthRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
}

export interface AuthResponse {
  accessToken: string;
  username: string;
  roles: string[];
}
