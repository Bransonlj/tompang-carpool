import { CarpoolSummary } from "src/frontend/carpool/dto";
import { UserData } from "src/types";

export type RideRequestStatus = "PENDING" | "ASSIGNED" | "FAILED";

export type RideRequestSummary = {
  id: string;
  passengers: number;
  originAddress: string;
  destinationAddress: string;
  startTime: string; // ISO datetime format string
  endTime: string; // ISO datetime format string
  status: RideRequestStatus;
}

export type RideRequestDetail = RideRequestSummary & {
  assignedCarpool?: CarpoolSummary & { driver: UserData; };
  pendingCarpools: (CarpoolSummary & { driver: UserData; })[];
}