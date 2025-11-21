import type { UserData } from "../../../../types";
import type { RideRequestSummary } from "../../ride-request/types";

export type CarpoolSummary = {
  id: string;
  totalSeats: number;
  seatsAssigned: number;
  originAddress: string;
  destinationAddress: string;
  arrivalTime: string; // ISO datetime format string
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

export type CarpoolDetail = CarpoolSummary & {
  confirmedRides: (RideRequestSummary & { rider: UserData })[];
  pendingRequests: (RideRequestSummary & { rider: UserData })[];
}

export type CreateCarpoolDto = {
  driverId: string;
  seats: number;
  arrivalTime: Date;
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

export type AcceptCarpoolRequestDto = {
  carpoolId: string;
  requestId: string;
}

export type DeclineCarpoolRequestDto = {
  carpoolId: string;
  requestId: string;
}