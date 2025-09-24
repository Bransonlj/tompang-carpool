export type LoginRequestDto = {
  email: string;
  password: string;
}

export type UserRole = "USER" | "ADMIN" | "DRIVER";

export type LoginResponseDto = {
  userId: string;
  token: string;
  roles: UserRole[];
}

export type RegisterRequestDto = {
  email: string;
  firstName: string;
  lastName: string;
  password: string;
}