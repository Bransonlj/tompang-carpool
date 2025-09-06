export type CarpoolRequestInvalidatedEvent = {
  carpoolId: string;
  rideRequestId: string;
  reason: string;
  driverId: string;  
}