import { DomainEvent } from "..";

export class CarpoolMatchedEvent implements DomainEvent {

  carpoolId: string;
  rideRequestId: string;
  driverId: string;

  constructor(message: any) {
    this.carpoolId = message.carpoolId;
    this.rideRequestId = message.rideRequestId;
    this.driverId = message.driverId;
  }

  getMessage(): string {
    return `A rider has request to join your carpool`;
  }
  getTargetUserId(): string {
    return this.driverId;
  }
  
}