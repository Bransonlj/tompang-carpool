import type { UserData } from "../../../../types";
import type { CarpoolSummary } from "../../carpool/types";

export type RideRequestStatus = "PENDING" | "ASSIGNED" | "FAILED";

export type RideRequestSummary = {
  id: string;
  passengers: number;
  originAddress: string;
  destinationAddress: string;
  startTime: string; // ISO datetime format string
  endTime: string; // ISO datetime format string
  status: RideRequestStatus;
  originImageUrl: string | undefined;
  destinationImageUrl: string | undefined;
  destinationLatLng: {
    lat: number;
    lng: number;
  };
  originLatLng: {
    lat: number;
    lng: number;
  };
}

export type RideRequestDetail = RideRequestSummary & {
  assignedCarpool?: CarpoolSummary & { driver: UserData; };
  pendingCarpools: (CarpoolSummary & { driver: UserData; })[];
}

export type CreateRideRequestDto = {
  riderId: string;
  passengers: number;
  startTime: Date;
  endTime: Date;
  route: {
    origin: {
      latitude: number;
      longitude: number;
    };
    destination: {
      latitude: number;
      longitude: number;
    };
  };
}