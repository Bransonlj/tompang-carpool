import { RideRequestSummary } from "src/frontend/ride-request/dto";
import { UserData } from "src/frontend/types";

export type CarpoolSummary = {
  id: string;
  totalSeats: number;
  seatsAssigned: number;
  originAddress: string;
  destinationAddress: string;
  arrivalTime: Date;
  originImageUrl: string | undefined;
  destinationImageUrl: string | undefined;
}

export type CarpoolDetail = CarpoolSummary & {
  destinationLatLng: {
    lat: number;
    lng: number;
  };
  originLatLng: {
    lat: number;
    lng: number;
  };
  confirmedRides: (RideRequestSummary & { rider: UserData })[];
  pendingRequests: (RideRequestSummary & { rider: UserData })[];
}

export * from "./create-carpool.dto"
export * from "./accept-carpool.dto"
export * from "./decline-carpool.dto"