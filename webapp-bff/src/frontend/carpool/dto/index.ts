import { RideRequestSummary } from "src/frontend/ride-request/dto";
import { UserData } from "src/types";

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