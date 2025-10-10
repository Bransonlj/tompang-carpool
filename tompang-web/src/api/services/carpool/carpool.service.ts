import api, { authHeader } from "../../client/http";
import type { AcceptCarpoolRequestDto, CarpoolDetail, CarpoolSummary, CreateCarpoolDto, DeclineCarpoolRequestDto } from "./types";

export async function getCarpoolsByUser(userId: string, token: string): Promise<CarpoolSummary[]> {
  return await api.get(`api/carpool/user/${userId}`, authHeader(token));
}

export async function getCarpoolById(carpoolId: string, token: string): Promise<CarpoolDetail> {
  return await api.get(`api/carpool/${carpoolId}`, authHeader(token));
}

async function createCarpool(dto: CreateCarpoolDto, token: string): Promise<void> {
  return await api.post(`api/carpool`, dto, authHeader(token));
}

async function acceptCarpoolRequest(dto: AcceptCarpoolRequestDto, token: string): Promise<void> {
  return await api.post(`api/carpool/request/accept`, dto, authHeader(token));
}

async function declineCarpoolRequest(dto: DeclineCarpoolRequestDto, token: string): Promise<void> {
  return await api.post(`api/carpool/request/decline`, dto, authHeader(token));
}

const CarpoolService = {
  getCarpoolsByUser,
  getCarpoolById,
  createCarpool,
  acceptCarpoolRequest,
  declineCarpoolRequest,
}

export default CarpoolService;