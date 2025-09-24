import type { LoginRequestDto, LoginResponseDto, RegisterRequestDto } from "./types";

export async function authLogin(dto: LoginRequestDto): Promise<LoginResponseDto> {
  return {
    userId: "buh",
    token: "huh",
    roles: ["USER", "DRIVER"],
  }
}

export async function authRegister(dto: RegisterRequestDto): Promise<void> {
  
}