import type { DriverRegistrationResponseDto, RegisterDriverDto } from "./types";
import api, { authHeader } from "../../client/http";

async function getDriverRegistrationsByUserId(userId: string, token: string): Promise<DriverRegistrationResponseDto[]> {
  return await api.get(`api/driver/registration/user/${userId}`, authHeader(token));
}

async function getDriverRegistrationById(id: string, token: string): Promise<DriverRegistrationResponseDto> {
  return await api.get(`api/driver/registration//${id}`, authHeader(token));
}

async function registerDriver(dto: RegisterDriverDto): Promise<void> {
  const { file, authToken, ...data } = dto;
  const formData = new FormData();
  formData.append("file", dto.file);

  formData.append("dto", JSON.stringify(data));
  await api.post(`api/driver/register`, formData, authHeader(authToken, "multipart/form-data"));
  return;
}

async function getAdminPendingDriverRegistrations(token: string): Promise<DriverRegistrationResponseDto[]> {
  return await api.get("api/driver/admin/pending-registrations/", authHeader(token));
}

async function adminAcceptDriverRegistration(id: string, token: string): Promise<void> {
  return await api.post(`api/driver/admin/registration/${id}/approve`, {}, authHeader(token));
}

async function adminRejectDriverRegistration(id: string, reason: string, token: string): Promise<void> {
  return await api.post(`api/driver/admin/registration/${id}/reject`, { reason }, authHeader(token));
}

const DriverService = {
  getDriverRegistrationsByUserId,
  getDriverRegistrationById,
  registerDriver,
  getAdminPendingDriverRegistrations,
  adminAcceptDriverRegistration,
  adminRejectDriverRegistration,
};

export default DriverService;