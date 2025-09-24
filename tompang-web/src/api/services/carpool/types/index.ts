import type { UserData } from "../../../../types";
import type { RideRequestSummary } from "../../ride-request/types";

export type CarpoolSummary = {
  id: string;
  totalSeats: number;
  seatsAssigned: number;
  originAddress: string;
  destinationAddress: string;
  arrivalTime: string; // ISO datetime format string
}

export type CarpoolDetail = CarpoolSummary & {
  confirmedRides: (RideRequestSummary & { rider: UserData })[];
  pendingRequests: (RideRequestSummary & { rider: UserData })[];
}