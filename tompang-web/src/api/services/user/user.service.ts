import api, { authHeader } from "../../client/http";
import type { uploadProfilePictureDto, UserProfile } from "./types";

export async function getUserById(userId: string, token: string): Promise<UserProfile> {
  return await api.get(`api/user/${userId}`, authHeader(token));
}

export async function uploadProfilePicture(dto: uploadProfilePictureDto): Promise<void> {
  const formData = new FormData();
  formData.append("file", dto.file);
  formData.append("id", dto.userId)
  await api.post(`api/user/profile/pic`, formData, authHeader(dto.token, "multipart/form-data"));
  return;
}

export async function test() {
  await api.get("/buh/huh")
}