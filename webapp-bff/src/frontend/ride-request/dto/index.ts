import { RideRequestStatus } from "src/backend/carpool/dto";
import { CarpoolSummary } from "src/frontend/carpool/dto";
import { UserData } from "src/frontend/types";

export type RideRequestSummary = {
  id: string;
  passengers: number;
  originAddress: string;
  destinationAddress: string;
  startTime: Date
  endTime: Date
  status: RideRequestStatus;
  originImageUrl: string | undefined;
  destinationImageUrl: string | undefined;
}

export type RideRequestDetail = RideRequestSummary & {
  destinationLatLng: {
    lat: number;
    lng: number;
  };
  originLatLng: {
    lat: number;
    lng: number;
  };
  assignedCarpool: CarpoolSummary & { driver: UserData; } | null;
  pendingCarpools: (CarpoolSummary & { driver: UserData; })[];
}