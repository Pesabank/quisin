export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterData extends LoginCredentials {
  name: string;
  role?: string;
}

export interface UserProfile {
  id: string;
  email: string;
  name: string;
  role: string;
  createdAt: string;
  updatedAt: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: UserProfile;
}

export interface TokenPayload {
  sub: string;
  email: string;
  role: string;
  exp: number;
  iat: number;
} 