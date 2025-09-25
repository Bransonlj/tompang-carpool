import api, { authHeader } from "../../client/http";
import type { CarpoolDetail, CarpoolSummary } from "./types";

export async function getCarpoolsByUser(userId: string, token: string): Promise<CarpoolSummary[]> {
  return await api.get(`api/carpool/user/${userId}`, authHeader(token));
}

export async function getCarpoolById(carpoolId: string, token: string): Promise<CarpoolDetail> {
  return await api.get(`api/carpool/${carpoolId}`, authHeader(token));
}