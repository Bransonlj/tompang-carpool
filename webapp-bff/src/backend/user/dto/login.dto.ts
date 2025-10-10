import { IsString } from "class-validator";

export class LoginRequestDto {
  @IsString()
  email: string;

  @IsString()
  password: string;
}

export type UserRoles = "USER" | "ADMIN" | "DRIVER"

export class LoginResponseDto {
  userId: string;
  token: string;
  roles: UserRoles[];
}