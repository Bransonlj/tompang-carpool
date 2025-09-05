import { DomainEvent } from "..";

export class CarpoolRequestInvalidatedEvent implements DomainEvent {
  carpoolId: string;
  rideRequestId: string;
  reason: string;
  driverId: string;

  constructor(message: any) {
    this.carpoolId = message.carpoolId;
    this.rideRequestId = message.rideRequestId;
    this.driverId = message.driverId;
  }

  getMessage(): string {
    return `Rider request to your carpool has expired: ${this.reason}`;
  }
  getTargetUserId(): string {
    return this.driverId;
  }
  
}