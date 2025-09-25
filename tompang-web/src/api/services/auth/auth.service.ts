import api from "../../client/http";
import type { LoginRequestDto, LoginResponseDto, RegisterRequestDto } from "./types";

export async function authLogin(dto: LoginRequestDto): Promise<LoginResponseDto> {
  return await api.post("api/auth/login", dto);
}

export async function authRegister(dto: RegisterRequestDto): Promise<void> {
  return await api.post("api/auth/register", dto);
}