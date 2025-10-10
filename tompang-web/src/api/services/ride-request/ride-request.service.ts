import api, { authHeader } from "../../client/http";
import type { CreateRideRequestDto, RideRequestDetail, RideRequestSummary } from "./types";

export async function getRideRequestsByUser(userId: string, token: string): Promise<RideRequestSummary[]> {
  return await api.get(`api/ride-request/user/${userId}`, authHeader(token));
}

export async function getRideRequestById(requestId: string, token: string): Promise<RideRequestDetail> {
  return await api.get(`api/ride-request/${requestId}`, authHeader(token));
}

async function createRideRequest(dto: CreateRideRequestDto, token: string): Promise<void> {
  return await api.post(`api/ride-request`, dto, authHeader(token));
}

const RideRequestService = {
  getRideRequestsByUser,
  getRideRequestById,
  createRideRequest,
};

export default RideRequestService;