import api, { authHeader } from "../../client/http";
import type { RideRequestDetail, RideRequestSummary } from "./types";

export async function getRideRequestsByUser(userId: string, token: string): Promise<RideRequestSummary[]> {
  return await api.get(`api/ride-request/user/${userId}`, authHeader(token));
}

export async function getRideRequestById(requestId: string, token: string): Promise<RideRequestDetail> {
  return await api.get(`api/ride-request/${requestId}`, authHeader(token));
}