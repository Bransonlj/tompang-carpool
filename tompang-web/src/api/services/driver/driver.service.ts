import type { DriverRegistrationResponseDto, RegisterDriverDto } from "./types";
import api, { authHeader } from "../../client/http";

export async function getDriverRegistrationByUserId(userId: string, token: string): Promise<DriverRegistrationResponseDto[]> {
  return await api.get(`api/driver/registration/${userId}`, authHeader(token));
}

export async function registerDriver(dto: RegisterDriverDto): Promise<void> {
  const { file, authToken, ...data } = dto;
  const formData = new FormData();
  formData.append("file", dto.file);

  formData.append("dto", JSON.stringify(data));
  await api.post(`api/driver/register`, formData, authHeader(authToken, "multipart/form-data"));
  return;
}
